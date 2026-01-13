package ifp.pmdm.aplicacioncitasmedicas

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scaleMatrix
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.pmdm.aplicacioncitasmedicas.databinding.ActivityMainMenuBinding


class MainMenu : AppCompatActivity() {
    lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferencias = getSharedPreferences("meds_pref", MODE_PRIVATE)


        binding.btnAddMed.setOnClickListener {
            createCard();
        }

        binding.mainBtnAgregarMed.setOnClickListener {
            Utils.ChangeActivity(this, AgregarMedActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun createCard(){
        val layoutPrueba = LinearLayout(this);
        layoutPrueba.orientation = LinearLayout.VERTICAL;
        layoutPrueba.setBackgroundResource(R.color.dark_white)
        layoutPrueba.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            450
        ));
        binding.linearMain.addView(layoutPrueba);

        val layoutPruebaUP = LinearLayout(this);
        layoutPruebaUP.orientation = LinearLayout.HORIZONTAL;
        layoutPruebaUP.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150,
            1f
        ));
        layoutPrueba.addView(layoutPruebaUP);

        val imgCard = ImageView(this);
        imgCard.setImageResource(R.drawable.ibu);
        imgCard.setLayoutParams(LinearLayout.LayoutParams(
            825,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ));
        imgCard.scaleType = ImageView.ScaleType.FIT_START

        val btnClose = Button(this);
        btnClose.setBackgroundResource(R.color.red);
        btnClose.text = "X";
        btnClose.setTextColor(R.color.white)
        btnClose.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ));

        layoutPruebaUP.addView(imgCard);
        layoutPruebaUP.addView(btnClose);

        val textPrueba1 = TextView(this);
        val textPrueba2 = TextView(this);
        textPrueba1.text = getString(R.string.tv_med);
        textPrueba1.setTypeface(null, Typeface.BOLD)
        textPrueba1.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            75
        ));
        textPrueba2.text = getString(R.string.tv_med2);
        textPrueba2.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        ));
        layoutPrueba.addView(textPrueba1);
        layoutPrueba.addView(textPrueba2);

        val linearLayoutDown = LinearLayout(this);
        linearLayoutDown.orientation = LinearLayout.HORIZONTAL;
        linearLayoutDown.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            75
        ));
        layoutPrueba.addView(linearLayoutDown);

        val tv_Dosis = TextView(this);
        tv_Dosis.setTypeface(null, Typeface.BOLD);
        tv_Dosis.text = "Dosis: 600mg";
        tv_Dosis.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
            ));
        linearLayoutDown.addView(tv_Dosis);

        val tv_NextUse = TextView(this);
        tv_NextUse.setTypeface(null, Typeface.BOLD);
        tv_NextUse.text = "Proximo: 17 de Enero";
        tv_NextUse.setLayoutParams(LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ));

        linearLayoutDown.addView(tv_NextUse);
    }
}