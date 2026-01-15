package ifp.pmdm.aplicacioncitasmedicas

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.provider.Settings
import android.widget.Toast
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.Notificacion
import ifp.pmdm.aplicacioncitasmedicas.clases.codigoExtraKey
import ifp.pmdm.aplicacioncitasmedicas.clases.defaultChannelID
import ifp.pmdm.aplicacioncitasmedicas.clases.messageExtraKey
import ifp.pmdm.aplicacioncitasmedicas.clases.titleExtraKey

//El "objeto" lo utilice para no tener que copiar la misma funcion de cambiar escena por cada
//activity, asi la puedo llamar desde cualquier clase y los errores son mas faciles de detectar
object Utils {
    fun <T> ChangeActivity(context: Context, cls: Class<T>){
        context.startActivity(Intent(context, cls))

        if(context is Activity){
            context.finish()
        }
    }

    fun scheduleNotification(context: Context, med: Medicamento) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Falta permiso de alarmas y recordatorios", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }
        try {
            val intent = Intent(context, Notificacion::class.java).apply {
                putExtra(titleExtraKey, "Te toca tomar ${med.nombre}")
                putExtra(
                    messageExtraKey,
                    String.format("%02d:%02d · Dosis: %s", med.hora, med.min, med.dosis)
                )
                putExtra(codigoExtraKey, med.codigoEscaner)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                med.codigoEscaner.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                med.getUltimaFechaMillis(),
                pendingIntent
            )
        } catch (e: Exception){
            Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun crearCanalNotificacion(context: Context){
        val channel = NotificationChannel(
            defaultChannelID,
            "Notif channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Canal de notificaciones default"

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    fun dp(value: Int): Int =
        (value * Resources.getSystem().displayMetrics.density).toInt()

}