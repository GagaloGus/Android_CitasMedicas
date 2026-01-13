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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPruebaCamaraBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {

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
        if (result.contents != null) {
            binding.tvScan.text = "${result.contents}"
            if(binding.tvScan.text == "Ibuprofeno"){
                binding.ivCameraView.setImageResource(R.drawable.ibu)
            }else if(binding.tvScan.text == "Paracetamol"){
                binding.ivCameraView.setImageResource(R.drawable.para)
            }else if(binding.tvScan.text == "Omeprazol"){
                binding.ivCameraView.setImageResource(R.drawable.ome)
            }
            else{
                binding.tvScan.text = "No es un medicamento"
                binding.ivCameraView.setImageResource(R.drawable.no_image)
            }
        } else {
            Toast.makeText(this, "Volviendo", Toast.LENGTH_LONG).show()
        }
    }
}