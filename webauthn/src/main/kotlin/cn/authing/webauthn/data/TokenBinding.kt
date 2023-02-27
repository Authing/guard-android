package cn.authing.webauthn.data

data class TokenBinding(
    var status: TokenBindingStatus,
    var id: String
)

