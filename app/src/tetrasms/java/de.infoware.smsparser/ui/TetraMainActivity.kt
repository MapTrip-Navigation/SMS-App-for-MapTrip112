package de.infoware.smsparser.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.infoware.smsparser.R


class TetraMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TetraMainFragment())
                .commit()
        }
    }
}
