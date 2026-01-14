package ifp.pmdm.aplicacioncitasmedicas.clases

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import ifp.pmdm.aplicacioncitasmedicas.R
import ifp.pmdm.aplicacioncitasmedicas.RedirActivity

const val notificationID = 1
const val defaultChannelID = "channelDefault"
const val importantChannelID = "channelImportant"
const val titleExtraKey = "titleExtra"
const val messageExtraKey = "MessageExtra"
const val codigoExtraKey = "codigoQR"

class Notificacion : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val newIntent = Intent(context, RedirActivity::class.java).apply {
            putExtra(codigoExtraKey, intent.getStringExtra(codigoExtraKey))
        }

        val resultPendingIntent = TaskStackBuilder.create(context).run{
            addNextIntentWithParentStack(newIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, defaultChannelID)
            .setSmallIcon(R.drawable.pipa)
            .setContentTitle(intent.getStringExtra(titleExtraKey))
            .setContentText(intent.getStringExtra(messageExtraKey))
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}