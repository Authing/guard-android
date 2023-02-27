package cn.authing.webauthn.client.operation

enum class OperationType {
    Create,
    Get
}

interface OperationListener {
    fun onFinish(opType: OperationType, opId: String)
}