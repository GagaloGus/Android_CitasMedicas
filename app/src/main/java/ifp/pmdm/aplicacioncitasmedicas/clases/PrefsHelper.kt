package ifp.pmdm.aplicacioncitasmedicas.clases

import android.content.Context
import androidx.collection.ArrayMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ifp.pmdm.aplicacioncitasmedicas.clases.Medicamento

object PrefsHelper {
    const val PREF_NAME = "meds_pref"
    private val gson = Gson()

    fun getAllMeds(context: Context): ArrayMap<String, Medicamento> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val resultado = ArrayMap<String, Medicamento>()

        for ((key, value) in prefs.all){
            if(value is String) {
                try {
                    //Transforma el json a objeto
                    resultado[key] = gson.fromJson(value, Medicamento::class.java)
                } catch (e: Exception) {
                    //Json no valido
                }
            }
        }

        return resultado
    }
}