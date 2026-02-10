package com.zzt.zt_java_lib

class MyClass {
    fun main() {
        val testString: String = "Hello, World!"
        val size: Long = ObjectSizeCalculatorAgent.getObjectSize(testString)
        println("The size of the string object is: " + size + " bytes")
    }
}