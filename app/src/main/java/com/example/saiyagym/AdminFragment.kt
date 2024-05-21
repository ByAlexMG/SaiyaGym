package com.example.saiyagym
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminFragment : Fragment() {

    data class User(val uid: String, val email: String, val moroso: Int = 0)

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

        val addUserFloatingButton: FloatingActionButton = rootView.findViewById(R.id.adduser)
        addUserFloatingButton.setOnClickListener {
            showAddUserDialog()
        }

        return rootView
    }

    fun loadUsersFromFirestore() {
        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                usersList.clear() // Limpiar la lista antes de añadir nuevos datos
                for (document in result) {
                    val uid = document.id
                    val email = document.getString("email") ?: ""
                    val moroso = document.getLong("moroso")?.toInt() ?: 0
                    val user = User(uid, email, moroso)
                    usersList.add(user)
                }
                // Ordenar la lista: Primero los usuarios sin "moroso"
                usersList.sortBy { it.moroso }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                val snackbar = Snackbar.make(requireView(), "Error al cargar usuarios", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
    }


    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_user, null)
        val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Nuevo Usuario")
            .setView(dialogView)
            .setPositiveButton("Agregar") { dialog, _ ->
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                addNewUser(email, password)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

            if (currentUser.moroso == 1) {
                holder.uidTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColorMoroso))
                holder.emailTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColorMoroso))
                holder.actionButton.visibility = View.GONE
            } else {
                holder.uidTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColorDefault))
                holder.emailTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.textColorDefault))
                holder.actionButton.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        private fun deleteUser(position: Int) {
            val db = FirebaseFirestore.getInstance()
            val userToMark = users[position]
            val uid = userToMark.uid

            db.collection("users").document(uid)
                .update("moroso", 1)
                .addOnSuccessListener {
                    usersList[position] = userToMark.copy(moroso = 1)
                    // Ordenar y notificar cambios en la lista
                    usersList.sortBy { it.moroso }
                    notifyDataSetChanged()
                    LogHelper.saveChangeLog(requireContext(), "Usuario marcado como moroso", "INFO")
                    // Llamar a loadUsersFromFirestore() para asegurar la lista actualizada
                    loadUsersFromFirestore()
                }
                .addOnFailureListener { exception ->
                    LogHelper.saveChangeLog(requireContext(), "Error al marcar usuario como moroso: ${exception.message}", "ERROR")
                    val snackbar = Snackbar.make(requireView(), "Error al marcar usuario como moroso", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
        }
    }



    private fun addNewUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUserTask ->
                if (createUserTask.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: ""

                    val user = hashMapOf(
                        "email" to email,
                    )
                    db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            LogHelper.saveChangeLog(requireContext(), "Usuario creado", "INFO")

                            val intent = Intent(requireContext(), IntroducirDatos::class.java)
                            startActivity(intent)
                            activity?.finish()
                            editor.clear()
                            editor.apply()
                        }
                } else {
                    LogHelper.saveChangeLog(requireContext(), "Error al crear usuario", "ERROR")
                    val snackbar = Snackbar.make(requireView(), "Error al crear usuario", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }
    }
}