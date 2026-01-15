package ifp.pmdm.aplicacioncitasmedicas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityRedirBinding

class RedirActivity : AppCompatActivity() {
    lateinit var binding: ActivityRedirBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRedirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRedirVolver.visibility = View.GONE

        binding.ivRedirCam.setOnClickListener {
            openQRscanner()
        }

        binding.btnRedirVolver.setOnClickListener {
            Utils.ChangeActivity(this, NotifActivity::class.java)
        }

        val preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)
        val gson = Gson()

        for ((key, value) in preferencias.all) {
            try {
                if (value is String) {
                    val med = gson.fromJson(value, Medicamento::class.java)
                    binding.tvRedirMedName.text = med.nombre
                    binding.tvRedirDosisMed.text = med.dosis
                    binding.tvRedirHoraMed.setText(String.format("%02d:%02d", med.hora, med.min))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun openQRscanner(){
        val options = ScanOptions()
        options.setPrompt("Scan any QR code of meds")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == binding.tvRedirMedName.text) {
            binding.tvRedirRessult.text = "¡Medicamento escaneado! : ${result.contents}"
            binding.btnRedirVolver.visibility = View.VISIBLE

        } else if(result.contents != binding.tvRedirMedName.text){
            Toast.makeText(this, "No es el medicamento pedido", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "cancelled", Toast.LENGTH_LONG).show()
        }
    }
}