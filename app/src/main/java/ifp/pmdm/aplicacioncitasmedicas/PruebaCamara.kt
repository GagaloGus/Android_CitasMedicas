package ifp.pmdm.aplicacioncitasmedicas

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityPruebaCamaraBinding

class PruebaCamara : AppCompatActivity() {

    lateinit var binding: ActivityPruebaCamaraBinding

    var onQRScanned: ((String) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPruebaCamaraBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            onQRScanned = { texto -> binding.tvScan.text = texto}

            val options = ScanOptions()
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            options.setCaptureActivity(CaptureActivity::class.java)
            barcodeLauncher.launch(options)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            onQRScanned?.invoke(it)
        }
    }
}