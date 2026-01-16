package ifp.pmdm.aplicacioncitasmedicas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityPermisosBinding
import android.content.Intent
import android.os.Build

class PermisosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermisosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPermisosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnConceder.setOnClickListener {
            solicitarPermisos()
        }
        binding.btnOmitir.setOnClickListener {
            irSiguientePantalla()
        }
    }
    private fun solicitarPermisos(){
        val permisos = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            permisos.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED){
                permisos.add(Manifest.permission.POST_NOTIFICATIONS)
            }


        }

        if (permisos.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permisos.toTypedArray(),200)

        }else{
            irSiguientePantalla()
        }

    }
    override fun onRequestPermissionsResult(
        requesCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requesCode, permissions, grantResults)
        irSiguientePantalla()
    }
    private fun irSiguientePantalla(){
        startActivity(Intent(this, MainMenu::class.java))
        finish()
    }

}