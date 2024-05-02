package com.example.saiyagym
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        replaceFragment(Option1Fragment())


    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // Add animations
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in_left,
            R.anim.slide_out_right,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

}
