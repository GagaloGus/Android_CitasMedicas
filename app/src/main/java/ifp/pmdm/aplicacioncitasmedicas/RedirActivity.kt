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
import com.journeyapps.barcodescanner.Util
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.clases.codigoExtraKey
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityRedirBinding

class RedirActivity : AppCompatActivity() {
    lateinit var binding: ActivityRedirBinding

    lateinit var medicamento: Medicamento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRedirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivRedirCam.setOnClickListener {
            openQRscanner()
        }

        val preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)
        val gson = Gson()

        val codigoQR = intent.getStringExtra(codigoExtraKey)

        for ((key, value) in preferencias.all) {
            try {
                if (value is String && key == codigoQR) {
                    medicamento = gson.fromJson(value, Medicamento::class.java)

                    binding.tvRedirMedName.text = getString(R.string.txt_recordatorioNombreMed, medicamento.nombre)
                    binding.tvRedirDosisMed.text = getString(R.string.txt_recordatorioDosisMed, medicamento.dosis)
                    binding.tvRedirHoraMed.text = getString(R.string.txt_recordatorioHoraMed,
            String.format("%02d:%02d", medicamento.hora, medicamento.min
                    ))
                    break
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
        options.setPrompt("Escanea el QR del medicamento: '${medicamento.nombre}'")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == binding.tvRedirMedName.text) {
            medicamento.actualizarUltimaFecha()
            Toast.makeText(this, "'${medicamento.nombre}' escaneado correctamente!", Toast.LENGTH_LONG).show()
            Utils.scheduleNotification(this, medicamento)
            Utils.ChangeActivity(this, MainMenu::class.java)

        } else if(result.contents != binding.tvRedirMedName.text){
            Toast.makeText(this, "No es el medicamento pedido", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "cancelled", Toast.LENGTH_LONG).show()
        }
    }
}