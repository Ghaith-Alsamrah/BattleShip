package com.example.buttleships

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class dataBase : ViewModel() {
    var db: FirebaseFirestore? = Firebase.firestore
    var localPlayerId = mutableStateOf<String?>(null)
    val playerList = MutableStateFlow<Map<String, player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, game>>(emptyMap())


    fun listentoPlayer() {
            db?.collection("players") // this players sould be the same as the collection name in firebase
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        val updateplayer = value.documents.associate { doc ->
                            doc.id to doc.toObject(player::class.java)!!
                        }
                        playerList.value = updateplayer

                    }
                }

        println("connection suc")
        }

    fun listentoGame(){
        // Listen for games
        db?.collection("games")
            ?.addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    val updatedMap = value.documents.associate { doc ->
                        doc.id to doc.toObject(game::class.java)!!
                    }
                    gameMap.value = updatedMap
                }
            }
    }

    fun makeNewPlaye(name: String) {
        val newPlayer = player(name = name)
        db?.collection("players")
            ?.add(newPlayer)
            ?.addOnSuccessListener { documentReference ->
                val playerId = documentReference.id
                localPlayerId.value = playerId
            }
    }

    fun UpdatGame(gameId: String){
        db?.collection("games")?.document(gameId)
            ?.update("gameState", "player1_turn")

    }
    fun deleteOfflinePlayers() {
        val query = db?.collection("players")?.whereEqualTo("status", "offline")
        query?.get()?.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                document.reference.delete()
                    .addOnSuccessListener {
                        // Successfully deleted document
                        println("Player with ID ${document.id} (offline) deleted.")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        println("Error deleting document: $e")
                    }
            }
        }?.addOnFailureListener { e ->
            // Handle failure in fetching documents
            println("Error fetching documents: $e")
        }
    }








}

