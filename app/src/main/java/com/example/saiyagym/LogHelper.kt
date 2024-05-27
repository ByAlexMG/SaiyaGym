package com.example.saiyagym

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object LogHelper {

    fun saveChangeLog(context: Context, action: String, tipo: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Snackbar.make(
                (context as AppCompatActivity).findViewById(android.R.id.content),
                "Por favor vuelva a iniciar sesión",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val logEntry = hashMapOf(
            "UID" to userId,
            "fecha" to Date(),
            "action" to action,
            "tipo" to tipo
        )
        val docRef = db.collection("log").document("log")
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                docRef.update("logs", FieldValue.arrayUnion(logEntry))
            } else {
                val logs = arrayListOf(logEntry)
                docRef.set(hashMapOf("logs" to logs))
            }
        }
    }
}
