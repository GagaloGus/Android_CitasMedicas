package ifp.pmdm.aplicacioncitasmedicas

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import ifp.pmdm.aplicacioncitasmedicas.clases.Frecuencia
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.Notificacion
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.clases.codigoExtraKey
import ifp.pmdm.aplicacioncitasmedicas.clases.defaultChannelID
import ifp.pmdm.aplicacioncitasmedicas.clases.messageExtraKey
import ifp.pmdm.aplicacioncitasmedicas.clases.notificationID
import ifp.pmdm.aplicacioncitasmedicas.clases.titleExtraKey
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityAgregarMedBinding
import java.util.Calendar
import java.util.Date
import kotlin.math.min

class AgregarMedActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgregarMedBinding
    lateinit var timePicker: MaterialTimePicker

    lateinit var preferencias: SharedPreferences
    var frecuenciaMed = Frecuencia.NADA
    var codigoQR = ""
    var horaSel = 0
    var minSel = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgregarMedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.agrTxtcodeQR.visibility = View.GONE
        
        crearCanalNotificacion()

        preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)

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
                        frecuenciaMed = Frecuencia.DIA
                    }
                    R.id.agr_btnSemana -> {
                        frecuenciaMed = Frecuencia.SEMANA
                    }
                    R.id.agr_btnMes -> {
                        frecuenciaMed = Frecuencia.MES
                    }
                }

                toggleDiasSemanaLayout(frecuenciaMed == Frecuencia.SEMANA)
            }
        }

        binding.agrBtnAbrirCamara.setOnClickListener {
            //openQRscanner()
            codigoQR = "Ibuprofeno"

            binding.agrTxtcodeQR.visibility = View.VISIBLE
            binding.agrTxtcodeQR.setText(getString(R.string.txt_codigoScan, codigoQR))
        }

        binding.agrBtnVolverMenu.setOnClickListener {
            Utils.ChangeActivity(this, MainMenu::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun scheduleNotification(med: Medicamento){
        val intent = Intent(applicationContext, Notificacion::class.java)
        val title = "Te toca tomar ${med.nombre}!!!"
        val message = "${String.format("%02d:%02d", med.hora, med.min)} // Dosis: ${med.dosis}"
        intent.putExtra(titleExtraKey, title)
        intent.putExtra(messageExtraKey, message)
        intent.putExtra(codigoExtraKey, med.codigoEscaner)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            med.getUltimaFechaMillis(),
            pendingIntent
        )

        Toast.makeText(this, "Notificacion aplazada a: ${med.getUltimaFechaString()} (${Math.floor((med.getUltimaFechaMillis()/60).toDouble())}s)", Toast.LENGTH_SHORT).show()
    }

    fun crearCanalNotificacion(){
        val channel = NotificationChannel(
            defaultChannelID,
            "Notif channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Canal de notificaciones default"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun mostrarAlertaCrear(title: String, message: String){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Vale"){_,_-> Utils.ChangeActivity(this, MainMenu::class.java)}
            .show()
    }

    fun guardarMed(){
        val nombreMed = binding.agrTxtNombreMed.text.toString()
        val dosisMed = binding.agrTxtDosisMed.text.toString()
        val diasSemana = getDiasSemanaSeleccionados()

        if(nombreMed == ""){
            Toast.makeText(this, "Escribe un nombre!", Toast.LENGTH_SHORT).show()
            return
        }

        if(dosisMed == ""){
            Toast.makeText(this, "Escribe la dosis!", Toast.LENGTH_SHORT).show()
            return
        }

        if(frecuenciaMed == Frecuencia.NADA){
            Toast.makeText(this, "Selecciona la frecuencia!", Toast.LENGTH_SHORT).show()
            return
        }

        if(frecuenciaMed == Frecuencia.SEMANA && diasSemana.isEmpty()){
            Toast.makeText(this, "Selecciona los dias de la semana!", Toast.LENGTH_SHORT).show()
            return
        }

        if(codigoQR == ""){
            Toast.makeText(this, "Tienes que escanear un medicamento!", Toast.LENGTH_SHORT).show()
            return
        }

        val gson = Gson()

        val newMed = Medicamento(
            nombre = nombreMed,
            descripcion = binding.agrTxtDescMed.text.toString(),
            frecuencia = frecuenciaMed,
            diasSemana = diasSemana,
            hora = horaSel,
            min = minSel,
            dosis = dosisMed,
            codigoEscaner = codigoQR
        )

        val jsonData = gson.toJson(newMed)
        preferencias.edit(commit = true){
            putString(codigoQR, jsonData)
        }

        scheduleNotification(newMed)
        mostrarAlertaCrear(
            "Medicamento creado correctamente",
            "Nombre: ${newMed.nombre}\nDosis: ${newMed.dosis}\nSiguiente dosis: ${newMed.getUltimaFechaString()}"
        )
    }

    fun openQRscanner(){
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.setCaptureActivity(CaptureActivity::class.java)
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            codigoQR = result.contents

            binding.agrTxtcodeQR.visibility = View.VISIBLE
            binding.agrTxtcodeQR.setText(getString(R.string.txt_codigoScan, codigoQR))
        } else {
            Toast.makeText(this, "No devolvio QR valido", Toast.LENGTH_LONG).show()
        }
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

    fun getDiasSemanaSeleccionados(): List<Int> {
        val dias = mutableListOf<Int>()

        if (binding.agrBtnL.isChecked) dias.add(Calendar.MONDAY)
        if (binding.agrBtnM.isChecked) dias.add(Calendar.TUESDAY)
        if (binding.agrBtnX.isChecked) dias.add(Calendar.WEDNESDAY)
        if (binding.agrBtnJ.isChecked) dias.add(Calendar.THURSDAY)
        if (binding.agrBtnV.isChecked) dias.add(Calendar.FRIDAY)
        if (binding.agrBtnS.isChecked) dias.add(Calendar.SATURDAY)
        if (binding.agrBtnD.isChecked) dias.add(Calendar.SUNDAY)

        return dias
    }
}
