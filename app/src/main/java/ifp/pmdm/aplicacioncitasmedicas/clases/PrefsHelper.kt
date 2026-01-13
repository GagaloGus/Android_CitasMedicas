package ifp.pmdm.aplicacioncitasmedicas.clases

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefsHelper {
    private const val PREF_NAME = "meds_pref"
    private const val KEY_LISTA = "lista_meds"

    private val gson = Gson()

    fun guardar(context: Context, med: Medicamento) {
        guardarVarios(context, listOf(med))
    }

    fun guardarVarios(context: Context, lista: List<Medicamento>) {
        //Se tienen que añadir todos de golpe cada vez
        val lista = leer(context)
        lista.addAll(lista)

        val json = gson.toJson(lista)

        //Rerellena las shared preferences
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LISTA, json)
            .apply()
    }

    fun leer(context: Context): MutableList<Medicamento> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LISTA, null) ?: return mutableListOf()

        val type = object : TypeToken<MutableList<Medicamento>>() {}.type
        return gson.fromJson(json, type)
    }
}

