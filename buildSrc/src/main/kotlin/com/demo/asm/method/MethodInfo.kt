package com.demo.asm.method

data class MethodInfo(
    var className: String = "",
    var methodName: String = "",
    var returnParam: Any? = "",
    var time: Float = 0f,
    var params: ArrayList<Any?> = ArrayList()
) {
    override fun equals(other: Any?): Boolean {
        val m: MethodInfo = other as MethodInfo
        return m.methodName == this.methodName
    }

    override fun toString(): String {
        return "MethodInfo(className='$className', methodName='$methodName', returnParam=$returnParam, time=$time, params=$params)"
    }
}