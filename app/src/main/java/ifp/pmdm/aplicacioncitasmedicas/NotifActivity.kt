package ifp.pmdm.aplicacioncitasmedicas

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityNotifBinding

class NotifActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotifBinding

    companion object{
        const val MY_CHANNEL_ID = "myChannel"
        const val REDIR_CHANNEL_ID = "redirChannel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotifBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotifChannels()
        binding.notifBtnMandarNoti.setOnClickListener {
            createSimpleNotif()
        }

        binding.notifBtnRedir.setOnClickListener {
            createRedirectNotif(RedirActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun <T> createRedirectNotif(cls: Class<T>){
        val resultIntent = Intent(this, cls)

        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = Notification.Builder(this, REDIR_CHANNEL_ID)
            .setSmallIcon(R.drawable.pipa)
            .setContentTitle("Ey")
            .setContentText("mira esto")
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(1, builder.build())
        }

        Toast.makeText(this, "Redir", Toast.LENGTH_SHORT).show()
    }

    fun createSimpleNotif(){
        val builder = Notification.Builder(this, MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.muu)
            .setContentTitle("Alo")
            .setContentText("soy tu ano")
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(1, builder.build())
        }

        Toast.makeText(this, "Noti", Toast.LENGTH_SHORT).show()
    }

    fun createNotifChannels(){
        val channelNames = arrayOf( MY_CHANNEL_ID, REDIR_CHANNEL_ID )

        for (ch in channelNames){
            //Si nuestro dispositivo es mayor a la api 28 (Oreo)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(
                    ch,
                    ch+"_name",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Canal"
                }

                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.createNotificationChannel(channel)
            }
        }

    }
}