package com.example.saiyagym.principal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.saiyagym.firebase.LoginActivity
import com.example.saiyagym.principal.adminFragments.AdminFragment
import com.example.saiyagym.principal.adminFragments.AdminFragment2
import com.example.saiyagym.principal.userFragments.Fragment1.Option1Fragment
import com.example.saiyagym.principal.userFragments.Fragment2.Option2Fragment
import com.example.saiyagym.principal.userFragments.Option3Fragment
import com.example.saiyagym.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Principal : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_option1 -> {
                replaceFragment(Option1Fragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_option2 -> {
                replaceFragment(Option2Fragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_option3 -> {
                replaceFragment(Option3Fragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_option4, R.id.navigation_option5 -> {
                isAdmin { isAdmin ->
                    if (isAdmin) {
                        when (item.itemId) {
                            R.id.navigation_option4 -> replaceFragment(AdminFragment())
                            R.id.navigation_option5 -> replaceFragment(AdminFragment2())
                        }
                    }
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        checkMorosoAndRedirect()

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            replaceFragment(Option1Fragment())
        }


        isAdmin { isAdmin ->
            bottomNavigationView.menu.findItem(R.id.navigation_option4).isVisible = isAdmin
            bottomNavigationView.menu.findItem(R.id.navigation_option5).isVisible = isAdmin
        }
        setDayNight()
    }

    private fun replaceFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment?.javaClass != fragment.javaClass) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_out_left,
                R.anim.slide_in_right
            )
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commit()
        }
    }

    private fun isAdmin(callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("rol")
                    callback(role != null && role == "admin")
                } else {
                    callback(false)
                }
            }.addOnFailureListener {
                callback(false)
            }
        } else {
            callback(false)
        }
    }

    private fun checkMorosoAndRedirect() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val moroso = document.getLong("moroso")
                    if (moroso != null && moroso == 1L) {
                        clearPreferencesAndLogout()
                    }
                }
            }
        }
    }

    private fun clearPreferencesAndLogout() {
        val sp = getSharedPreferences("my_preferences", MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()

        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
    }

    fun setDayNight() {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation)
        val mainContainer = findViewById<View>(R.id.padre)
        mainContainer.startAnimation(fadeInAnimation)

        val sp = getSharedPreferences("my_preferences", MODE_PRIVATE)
        val theme = sp.getInt("theme", 1)
        if (theme == 0) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }
    }
}
