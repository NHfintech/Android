package kr.khs.nh2020.network

data class Response(
    val result : Double,
    val detail : String,
    val data : Any? = null
)