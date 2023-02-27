package cn.authing.webauthn.data

data class AuthenticatorSelectionCriteria(
    var authenticatorAttachment: AuthenticatorAttachment? = null,
    var requireResidentKey: Boolean = true,
    var userVerification: UserVerificationRequirement = UserVerificationRequirement.Required
)

