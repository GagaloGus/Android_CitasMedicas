package ifp.pmdm.aplicacioncitasmedicas.clases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import ifp.pmdm.aplicacioncitasmedicas.AgregarMedActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import android.content.ContextWrapper
import androidx.core.content.ContextCompat.startActivity

enum class Frecuencia{
    DIA, SEMANA, MES, NADA
}

data class Medicamento(
    val nombre:String,
    val descripcion: String = "",
    val frecuencia: Frecuencia = Frecuencia.DIA,
    val dosis:String,
    val diasSemana:List<Int> = listOf(),

    val hora:Int = 0,
    val min:Int = 0,
    val codigoEscaner: String,
) {
    private lateinit var ultimaFecha: Date

    //Se llama justo al crear el objeto
    init{
        actualizarUltimaFecha()
    }

    fun actualizarUltimaFecha(){
        ultimaFecha = getFechaSiguiente()
    }

    fun getUltimaFecha(): Date{
        return ultimaFecha
    }

    fun getUltimaFechaString(format:String = "EEE dd MMM HH:mm"): String{
        val formatoDia = SimpleDateFormat(format, Locale("es", "ES"))
        return formatoDia.format(ultimaFecha)
    }

    fun getUltimaFechaMillis(): Long{
        val cal = Calendar.getInstance()
        cal.time = ultimaFecha
        return cal.timeInMillis
    }

    fun getFechaSiguiente(): Date{
        val currentTime = Calendar.getInstance()
        val nowHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val nowMin = currentTime.get(Calendar.MINUTE)

        val cal = currentTime.clone() as Calendar

        val nextTime = when (frecuencia) {
            Frecuencia.MES -> {
                cal.add(Calendar.MONTH, 1)
                cal
            }

            Frecuencia.SEMANA ->{
                val currentWeekDay = cal.get(Calendar.DAY_OF_WEEK)
                var nextDay = 0

                var i = currentWeekDay
                while (true){
                    if(diasSemana.contains(i))
                        break

                    i+=1
                    nextDay += 1
                    if(i > Calendar.SATURDAY)
                        i = Calendar.SUNDAY
                }

                cal.add(Calendar.DAY_OF_MONTH, nextDay)
                cal
            }

            //Fallback a DIA
            else -> {
                //Si la hora es menor a la hora actual, se le añade un dia
                if(hora < nowHour ||
                  (hora == nowHour && min < nowMin))
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                cal
            }
        }

        nextTime.set(Calendar.HOUR_OF_DAY, hora)
        nextTime.set(Calendar.MINUTE, min)
        nextTime.set(Calendar.SECOND, 0)
        nextTime.set(Calendar.MILLISECOND, 0)
        return nextTime.time
    }
}

