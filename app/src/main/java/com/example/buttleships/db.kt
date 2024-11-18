package com.example.buttleships

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow

object dataBase{
        @SuppressLint("StaticFieldLeak")
        var db: FirebaseFirestore? = Firebase.firestore
        val playerList = MutableStateFlow<List<player>>(emptyList())


    fun MakeListner() {
            db?.collection("players") // this players sould be the same as the collection name in firebase
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        playerList.value = value.toObjects()
                    }
                }
        println("connection suc")
        }

    fun NewPlayer(playerId: String ,name: String) {
       // val query = db?.collection("players")?.whereEqualTo("playerId", "player1")

        val newPlayer = player(playerId = playerId, name = name, status = "online")
        db?.collection("players")
            ?.document(playerId) // Custom document ID
            ?.set(newPlayer)
            ?.addOnSuccessListener {
                // Handle success, e.g., log or notify
                if (newPlayer.status == "offline")
                    newPlayer.status = "online"
                else {
                    newPlayer.status = "offline"
                }
            }
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

