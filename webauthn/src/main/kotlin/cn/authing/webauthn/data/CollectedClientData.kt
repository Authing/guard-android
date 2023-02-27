package cn.authing.webauthn.data

data class CollectedClientData(
    //val type: CollectedClientDataType,
    val type: String,
    var challenge: String,
    var origin: String,
    var tokenBinding: TokenBinding? = null
)

