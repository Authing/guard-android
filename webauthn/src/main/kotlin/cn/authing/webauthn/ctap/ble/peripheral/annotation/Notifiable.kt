package cn.authing.webauthn.ctap.ble.peripheral.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Notifiable(val value: Boolean)
