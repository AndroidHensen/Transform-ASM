package com.demo.asm.plugin

import com.android.build.gradle.AppExtension
import com.demo.asm.transforms.CatIncrementalTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class PhoenixPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        //project.extensions.create("phoenix", PhoenixExtension::class.java, project.objects)
        project.afterEvaluate {
            println()
            println("===================================PhoenixPlugin===============begin==================")
            println()
            println()
            println("===================================PhoenixPlugin===============end==================")
            println()
        }
        registerTransform(project)
    }

    private fun registerTransform(project: Project) {
        val appExtension = project.extensions.getByName("android")
        if (appExtension is AppExtension) {
            appExtension.registerTransform(CatIncrementalTransform(project))
        }
    }
}