package kr.khs.kkotgil.network

data class Response(
    val result : Double,
    val detail : String,
    val data : Any? = null
)