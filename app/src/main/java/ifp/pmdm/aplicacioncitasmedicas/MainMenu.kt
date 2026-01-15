package ifp.pmdm.aplicacioncitasmedicas

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.scaleMatrix
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import ifp.pmdm.aplicacioncitasmedicas.clases.Frecuencia
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento
import ifp.pmdm.aplicacioncitasmedicas.clases.PrefsHelper
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityMainMenuBinding
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.content.edit


class MainMenu : AppCompatActivity() {
    lateinit var binding: ActivityMainMenuBinding
    val listaMedicamentos = mutableListOf<Medicamento>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)
        val gson = Gson()

        if(preferencias.all.isNotEmpty()){
            for ((key, value) in preferencias.all) {
                try {
                    if (value is String) {
                        val med = gson.fromJson(value, Medicamento::class.java)
                        listaMedicamentos.add(med)
                        Toast.makeText(this, "MED CREADA: ${med.nombre}", Toast.LENGTH_SHORT).show()
                        createCard(med)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            //TODO: Mostrar una pantallita que esta vacio
        }

        binding.btnAddMed.setOnClickListener {
            Utils.ChangeActivity(this, AgregarMedActivity::class.java)
        }
        binding.btnNoti.setOnClickListener {
            Utils.ChangeActivity(this, RedirActivity::class.java)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun createCard(med: Medicamento) {

        // layout principal
        val linearPrimary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.color.dark_white)
            val pad10 = Utils.dp(10)
            setPadding(pad10, pad10, pad10, pad10)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0,0,0, Utils.dp(15))
            }
        }

        // layout del titulo y el boton de cerrar
        val linearUp = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Utils.dp(38)
            )
        }

        linearPrimary.addView(linearUp)

        // texto titulo
        val titulo = TextView(this).apply {
            text = med.nombre
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        }

        // boton para borrar
        val btnClose = Button(this).apply {
            text = "X"
            setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red))
            setTextColor(ContextCompat.getColorStateList(context, R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )

            setOnClickListener {
                //1: Borra la vista del layout
                binding.linearMain.removeView(linearPrimary)

                //2: Borrar de las sharedPreferences
                val preferencias = getSharedPreferences(PrefsHelper.PREF_NAME, MODE_PRIVATE)
                preferencias.edit { remove(med.codigoEscaner) }

                Toast.makeText(this@MainMenu, "'${med.nombre}' eliminado", Toast.LENGTH_SHORT).show()
            }
        }

        linearUp.addView(titulo)
        linearUp.addView(btnClose)

        //Texto de la descripcion (SOLO SI HAY TEXTO)
        if(med.descripcion != ""){
            val descripcion = TextView(this).apply {
                text = med.descripcion
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, Utils.dp(2), 0, Utils.dp(2))
                }
            }

            linearPrimary.addView(descripcion)
        }

        //Layout de abajo
        val linearDown = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        linearPrimary.addView(linearDown)

        //Texto de la dosis
        val dosis = TextView(this).apply {
            text = med.dosis
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        //Texto de la siguiente dosis
        val nextUse = TextView(this).apply {
            text = "Siguiente dosis: ${med.getUltimaFechaString()}"
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        linearDown.addView(dosis)
        linearDown.addView(nextUse)

        // Añade el layout principal al layout del scrollview
        binding.linearMain.addView(linearPrimary)
    }
}