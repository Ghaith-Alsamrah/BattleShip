package com.example.buttleships

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

var playerListener: ListenerRegistration? = null
var gameListener: ListenerRegistration? = null
class Database : ViewModel() {
    var db = Firebase.firestore
    var localPlayerId : String? = null
    val playerList = MutableStateFlow<Map<String, player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, game>>(emptyMap())


    fun listentoPlayer() {
        playerListener = db.collection("players")
                .addSnapshotListener { value, error ->
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

    fun stopListening(listener : ListenerRegistration?) {
        listener?.remove()
        if (listener == playerListener)
            playerListener = null
        else
            gameListener = null
    }

    fun listentoGame(){
        // Listen for games
        gameListener = db.collection("games")
            .addSnapshotListener { value, error ->
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
        db.collection("players")
            .add(newPlayer)
            .addOnSuccessListener { documentReference ->
                val playerId = documentReference.id
                localPlayerId = playerId
            }
    }

    fun UpdatGame(gameId: String){
        db.collection("games")
            .document(gameId)
            .update("gameState", "player1_turn")
            .addOnSuccessListener {

            }

    }


    fun deleteOfflinePlayers() {
        val query = db.collection("players").whereEqualTo("status", "offline")
        query.get().addOnSuccessListener { querySnapshot ->
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
        }.addOnFailureListener { e ->
            // Handle failure in fetching documents
            println("Error fetching documents: $e")
        }
    }








}

