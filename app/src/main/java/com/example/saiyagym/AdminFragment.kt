package com.example.saiyagym
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

        // Botón para agregar un nuevo usuario
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
                for (document in result) {
                    val uid = document.id
                    val email = document.getString("email") ?: ""
                    val moroso = document.getLong("moroso")?.toInt() ?: 0 // Obtener el valor del campo "moroso"
                    val user = User(uid, email, moroso) // Pasar el valor de "moroso" al constructor
                    usersList.add(user)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
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
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
            return UserViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val currentUser = users[position]
            if (currentUser.moroso == 1) {
                // Si el usuario es moroso, ocultar el elemento de la lista
                holder.itemView.visibility = View.GONE
            } else {
                holder.itemView.visibility = View.VISIBLE
                holder.uidTextView.text = currentUser.uid
                holder.emailTextView.text = currentUser.email
            }
        }

        override fun getItemCount(): Int {
            return users.size
        }

        /*
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
                                        val adminEmail = "admin@admin.com"
                                        val adminPassword = "Navidad14"
                                        auth.signInWithEmailAndPassword(adminEmail, adminPassword)

                                    }

                            }
                        }
                    }
                }
        }
        */
        private fun deleteUser(position: Int) {
            val db = FirebaseFirestore.getInstance()
            val userToDelete = users[position]
            val uid = userToDelete.uid

            val userRef = db.collection("users").document(uid)
            // Campos a borrar
            val fieldsToDelete = listOf("altura", "edad", "grasa", "peso", "genero")

            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    // Verificar si el documento existe antes de continuar
                    if (documentSnapshot.exists()) {
                        // Construir un mapa con los campos a eliminar y sus valores asociados
                        val updates = mutableMapOf<String, Any?>()
                        fieldsToDelete.forEach { field ->
                            updates[field] = FieldValue.delete()
                        }

                        // Borrar los campos específicos
                        userRef.update(updates)
                            .addOnSuccessListener {
                                usersList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, itemCount)
                                userRef.update("moroso", 1)

                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error updating user", exception)
                                        // Manejar el error según sea necesario
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error deleting fields", exception)
                                // Manejar el error según sea necesario
                            }
                    } else {
                        Log.d(TAG, "Document does not exist")
                        // Manejar la situación en la que el documento no existe
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting user document", exception)
                    // Manejar el error según sea necesario
                }
        }

    }
        private fun addNewUser(email: String, password: String) {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { createUserTask ->
                    if (createUserTask.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val uid = firebaseUser?.uid ?: ""

                        // Guardar el usuario en la base de datos de Firestore
                        val user = hashMapOf(
                            "email" to email,
                            // Puedes agregar más campos aquí si es necesario
                        )

                        db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                //ya veremos como solucionar esto
                                val adminEmail = "admin@admin.com"
                                val adminPassword = "Navidad14"
                                auth.signInWithEmailAndPassword(adminEmail, adminPassword)
                            }
                            .addOnFailureListener { exception ->
                                // Manejar el fallo al agregar el usuario a Firestore
                                // Aquí podrías mostrar un mensaje de error o manejar de otra manera el fallo
                            }
                    } else {
                        // Manejar el fallo al crear el usuario en Firebase Authentication
                        // Aquí podrías mostrar un mensaje de error o manejar de otra manera el fallo
                    }
                }
        }
    }
