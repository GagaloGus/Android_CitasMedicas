package ifp.pmdm.aplicacioncitasmedicas.clases

import java.util.Date
import java.util.Calendar

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
    lateinit var ultimaFecha: Date

    //Se llama justo al crear el objeto
    init{
        actualizarUltimaFecha()
    }

    fun actualizarUltimaFecha(){
        ultimaFecha = getFechaSiguiente()
    }

    fun getFechaSiguiente(): Date{
        val currentTime = Calendar.getInstance()

        val nextTime = when (frecuencia) {
            Frecuencia.MES -> {
                val cal = currentTime.clone() as Calendar
                cal.add(Calendar.MONTH, 1)
                cal
            }

            Frecuencia.SEMANA ->{
                val cal = currentTime.clone() as Calendar
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
                val cal = currentTime.clone() as Calendar
                val nowHour = cal.get(Calendar.HOUR_OF_DAY)
                val nowMin = cal.get(Calendar.MINUTE)

                //Si la hora es menor a la hora actual, se le añade un dia
                if(hora < nowHour ||
                   (hora == nowHour && min < nowMin))
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                cal
            }
        }

        nextTime.set(Calendar.HOUR_OF_DAY, hora)
        nextTime.set(Calendar.MINUTE, min)
        return nextTime.time
    }
}

