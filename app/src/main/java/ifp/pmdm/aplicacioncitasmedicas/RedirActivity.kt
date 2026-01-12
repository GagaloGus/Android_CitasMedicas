package ifp.pmdm.aplicacioncitasmedicas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityRedirBinding

class RedirActivity : AppCompatActivity() {

    lateinit var binding: ActivityRedirBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRedirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.redirBtnVolver.setOnClickListener {
            Utils.ChangeActivity(this, NotifActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}