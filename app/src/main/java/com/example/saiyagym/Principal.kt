package com.example.saiyagym


import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

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
            R.id.navigation_option4 -> {
                if (isAdmin()) {
                    replaceFragment(AdminFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        setDayNight()

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            replaceFragment(Option1Fragment())
        }

        // Ocultar opción 4 si el usuario no es administrador
        val menuItem = bottomNavigationView.menu.findItem(R.id.navigation_option4)
        menuItem.isVisible = isAdmin()
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

    private fun isAdmin(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val adminUid = "iiWMCHqIp7Rfg9uyjdOk51NLh932"
        return currentUser != null && currentUser.uid == adminUid
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
