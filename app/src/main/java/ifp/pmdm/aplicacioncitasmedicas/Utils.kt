package ifp.pmdm.aplicacioncitasmedicas

import android.app.Activity
import android.content.Context
import android.content.Intent

//El "objeto" lo utilice para no tener que copiar la misma funcion de cambiar escena por cada
//activity, asi la puedo llamar desde cualquier clase y los errores son mas faciles de detectar
object Utils {
    fun <T> ChangeActivity(context: Context, cls: Class<T>){
        context.startActivity(Intent(context, cls))

        if(context is Activity){
            context.finish()
        }
    }
}