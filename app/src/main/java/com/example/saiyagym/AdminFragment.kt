package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminFragment : Fragment() {

    data class User(val uid: String, val email: String)

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var progressBar: ProgressBar
    private val usersList = mutableListOf<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_admin, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        progressBar = rootView.findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UserAdapter(usersList)
        recyclerView.adapter = adapter

        loadUsersFromFirestore()

        return rootView
    }

    private fun loadUsersFromFirestore() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    val email = document.getString("email") ?: ""
                    val user = User(uid, email)
                    usersList.add(user)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
    }

    private inner class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val uidTextView: TextView = itemView.findViewById(R.id.uidTextView)
            val emailTextView: TextView = itemView.findViewById(R.id.emailTextView)
            val actionButton: ImageButton = itemView.findViewById(R.id.actionButton)

            init {
                actionButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        deleteUser(position)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
            return UserViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val currentUser = users[position]
            holder.uidTextView.text = currentUser.uid
            holder.emailTextView.text = currentUser.email
        }

        override fun getItemCount(): Int {
            return users.size
        }

        private fun deleteUser(position: Int) {
            val db = FirebaseFirestore.getInstance()
            val userToDelete = users[position]

            val email = userToDelete.email
            val uid = userToDelete.uid

            val auth = FirebaseAuth.getInstance()
            val credential = EmailAuthProvider.getCredential(email, "Navidad14")
            auth.signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        val currentUser = auth.currentUser
                        currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                db.collection("users").document(uid)
                                    .delete()
                                    .addOnSuccessListener {
                                        usersList.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, itemCount)

                                        //ya veremos como solucionar esto
                                        val adminEmail = "nuevo@nuevo.com"
                                        val adminPassword = "Navidad14"
                                        auth.signInWithEmailAndPassword(adminEmail, adminPassword)

                                    }

                            }
                        }
                    }
                }
        }
    }
}
