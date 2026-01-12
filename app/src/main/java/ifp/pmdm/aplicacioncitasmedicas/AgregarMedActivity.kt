package ifp.pmdm.aplicacioncitasmedicas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityAgregarMedBinding

class AgregarMedActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgregarMedBinding
    lateinit var timePicker: MaterialTimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgregarMedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(R.string.txt_medHoraSeleccion)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            binding.agrBtnHora.setText(String.format("%02d:%02d", timePicker.hour, timePicker.minute))
        }

        //TimePicker para la hora
        binding.agrBtnHora.setOnClickListener{
            if (!timePicker.isAdded)
                timePicker.show(supportFragmentManager, "timePicker")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}