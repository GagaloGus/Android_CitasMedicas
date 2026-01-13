package ifp.pmdm.aplicacioncitasmedicas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityAgregarMedBinding

class AgregarMedActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgregarMedBinding
    lateinit var timePicker: MaterialTimePicker
    var frequenciaMed = "dia"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgregarMedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TimePicker para la hora
        createTimePicker()
        binding.agrBtnHora.setOnClickListener {
            if (!timePicker.isAdded)
                timePicker.show(supportFragmentManager, "timePicker")
        }

        //Guardado de datos
        binding.agrBtnGuardar.setOnClickListener {
            guardarMed()
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
        val nombreMed = binding.agrTxtNombreMed.toString()
        val newMed = Medicamento(
            nombre = nombreMed,
            frequencia = frequenciaMed,
            diasSemana = getDiasSemanaSeleccionados(),
            hora = timePicker.hour,
            min = timePicker.minute,
            codigoEscaner = ""
        )

        PrefsHelper.guardar(this, newMed)

        Toast.makeText(this, "DATOS GUARDADOS: $nombreMed", Toast.LENGTH_SHORT).show()
    }

    fun createTimePicker(){
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(R.string.txt_medHoraSeleccion)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            binding.agrBtnHora.setText(String.format("%02d:%02d", timePicker.hour, timePicker.minute))
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