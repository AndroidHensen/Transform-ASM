package com.demo.asm.transforms

import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CatClassVisitor(private val project: Project, classVisitor: ClassVisitor) :
    ClassVisitor(Opcodes.ASM6, classVisitor) {

    private lateinit var mClassName: String

    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        mClassName = name
        println("------visit = 【className】$mClassName")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        desc: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("------visitMethod = 【name】$name 【desc】$desc")
        var methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor =
            CatMethodVisitor(Opcodes.ASM6, methodVisitor, access, mClassName, name, desc)
        return methodVisitor
    }
}