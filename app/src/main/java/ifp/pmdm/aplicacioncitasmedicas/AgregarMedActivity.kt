package ifp.pmdm.aplicacioncitasmedicas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityAgregarMedBinding
import kotlin.math.min

class AgregarMedActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgregarMedBinding
    lateinit var timePicker: MaterialTimePicker
    lateinit var onQRScanned: (String) -> Unit
    var frequenciaMed = ""
    var codigoQR = ""

    var horaSel = 0
    var minSel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgregarMedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.agrTxtcodeQR.visibility = View.GONE

        //TimePicker para la hora
        createTimePicker()
        binding.agrBtnHora.setOnClickListener {
            if (!timePicker.isAdded)
                timePicker.show(supportFragmentManager, "timePicker")
        }

        //Guardado de datos
        binding.agrBtnGuardar.setOnClickListener {
            try{
                guardarMed()
            } catch (e: Exception){
                binding.agrTxtcodeQR.text = "ERROR: ${e.message}"
            }
        }

        binding.agrToggleGroupFreq.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.agr_btnDia -> {
                        frequenciaMed = "dia"
                    }
                    R.id.agr_btnSemana -> {
                        frequenciaMed = "semana"
                    }
                    R.id.agr_btnMes -> {
                        frequenciaMed = "mes"
                    }
                }

                toggleDiasSemanaLayout(frequenciaMed == "semana")
                Toast.makeText(this, frequenciaMed, Toast.LENGTH_SHORT).show()
            }
        }

        binding.agrBtnAbrirCamara.setOnClickListener {
            codigoQR = "codigo ejemplo"
            binding.agrTxtcodeQR.visibility = View.VISIBLE
            binding.agrTxtcodeQR.setText(getString(R.string.txt_codigoScan, codigoQR))
        }

        binding.agrBtnVolverMenu.setOnClickListener {
            Utils.ChangeActivity(this, MainActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    fun guardarMed(){
        val nombreMed = binding.agrTxtNombreMed.text.toString()
        val diasSemana = getDiasSemanaSeleccionados()

        if(nombreMed == ""){
            Toast.makeText(this, "Escribe un nombre!", Toast.LENGTH_SHORT).show()
            return
        }

        if(frequenciaMed == ""){
            Toast.makeText(this, "Selecciona la frecuencia!", Toast.LENGTH_SHORT).show()
            return
        }

        if(frequenciaMed == "semana" && diasSemana.isEmpty()){
            Toast.makeText(this, "Selecciona los dias de la semana!", Toast.LENGTH_SHORT).show()
            return
        }

        if(codigoQR == ""){
            Toast.makeText(this, "Tienes que escanear un medicamento!", Toast.LENGTH_SHORT).show()
            return
        }

        val preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)
        val gson = Gson()

        val newMed = Medicamento(
            nombre = nombreMed,
            frequencia = frequenciaMed,
            diasSemana = diasSemana,
            hora = horaSel,
            min = minSel,
            codigoEscaner = codigoQR
        )

        val jsonData = gson.toJson(newMed)
        preferencias.edit(commit = true){
            putString(codigoQR, jsonData)
        }

        Toast.makeText(this, "DATOS GUARDADOS: $nombreMed", Toast.LENGTH_SHORT).show()
        Utils.ChangeActivity(this, MainMenu::class.java)
    }

    fun openQRscanner(){
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        /*if (result.contents != null) {
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
        }*/
    }

    fun createTimePicker(){
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(R.string.txt_medHoraSeleccion)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            horaSel = timePicker.hour
            minSel = timePicker.minute
            binding.agrBtnHora.setText(String.format("%02d:%02d", horaSel, minSel))

        }
    }

    fun toggleDiasSemanaLayout(on: Boolean){
        binding.agrTxtlabelDiaSemana.visibility = if (on) View.VISIBLE else View.GONE
        binding.agrLayoutDiasSemana.visibility = if (on) View.VISIBLE else View.GONE
    }

    fun getDiasSemanaSeleccionados(): List<String> {
        val dias = mutableListOf<String>()

        if (binding.agrBtnL.isChecked) dias.add("L")
        if (binding.agrBtnM.isChecked) dias.add("M")
        if (binding.agrBtnX.isChecked) dias.add("X")
        if (binding.agrBtnJ.isChecked) dias.add("J")
        if (binding.agrBtnV.isChecked) dias.add("V")
        if (binding.agrBtnS.isChecked) dias.add("S")
        if (binding.agrBtnD.isChecked) dias.add("D")

        return dias
    }
}