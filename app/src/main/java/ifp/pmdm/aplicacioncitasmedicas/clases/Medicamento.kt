package ifp.pmdm.aplicacioncitasmedicas.clases

data class Medicamento(
    val nombre:String,
    val frequencia:String = "dia",
    val diasSemana:List<String> = listOf("L"),

    val hora:Int = 0,
    val min:Int = 0,
    val codigoEscaner: String
)