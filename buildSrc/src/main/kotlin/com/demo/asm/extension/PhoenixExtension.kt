package com.demo.asm.extension

import org.gradle.api.model.ObjectFactory

class PhoenixExtension(objectFactory: ObjectFactory) {

    var transform: TransformExtension = objectFactory.newInstance(TransformExtension::class.java)

    override fun toString(): String {
        return "PhoenixExtension(transform=$transform)"
    }
}