package com.demo.asm.method

import java.util.*

object MethodManager {

    private val methodWareHouse = Vector<MethodInfo>(1024)

    @JvmStatic
    fun start(): Int {
        methodWareHouse.add(MethodInfo())
        return methodWareHouse.size - 1
    }

    @JvmStatic
    fun addParams(param: Any?, index: Int) {
        val method = methodWareHouse[index]
        method.params.add(param)
    }

    @JvmStatic
    fun end(result: Any?, className: String, methodName: String, startTime: Long, index: Int) {
        val method = methodWareHouse[index]
        method.className = className
        method.methodName = methodName
        method.returnParam = result
        method.time = (System.nanoTime() - startTime) / 1000000f
        println("┌───────────────────────────────────------───────────────────────────────────------")
        println("│ [类名] ${method.className}")
        println("│ [函数] ${method.methodName}")
        println("│ [参数] ${method.params}")
        println("│ [返回] ${method.returnParam}")
        println("│ [耗时] ${method.time}")
        println("└───────────────────────────────────------───────────────────────────────────------")
    }
}