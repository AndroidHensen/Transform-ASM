package com.demo.asm.transforms

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.demo.asm.method.MethodManager
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class CatTransform(val project: Project) : Transform() {


    private var SCOPES: MutableSet<QualifiedContent.Scope> = mutableSetOf()

    init {
        SCOPES.add(QualifiedContent.Scope.PROJECT)
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS)
        SCOPES.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    override fun getName(): String {
        return "cat"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return SCOPES
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val outputProvider = transformInvocation?.outputProvider

        //非增量则全部删除输出文件，全量清空
        if (!isIncremental) {
            outputProvider?.deleteAll()
        }

        //知识点一：把类打进进包里面
        addClass(transformInvocation)

        //知识点二：把字节码打进包里面（分文件夹和jar包）
        transformInvocation?.inputs?.forEach { input ->
            //1、遍历输入的文件路径Dir
            input.directoryInputs.forEach { directoryInput ->
                println("--directoryInput = $directoryInput")
                if (directoryInput.file.isDirectory) {
                    //2、遍历输入的所有文件File
                    FileUtils.getAllFiles(directoryInput.file).forEach { it ->
                        println("----file = $it")
                        val file = it
                        val name = file.name
                        if (name.endsWith(".class")) {
                            //3、通过ClassReader->ClassVisitor->ClassWriter（读取字节码->访问并修改字节码->输出字节码）
                            val reader = ClassReader(file.readBytes())
                            val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
                            val visitor = CatClassVisitor(project, writer)
                            reader.accept(visitor, ClassReader.EXPAND_FRAMES)

                            //4、将输出的字节码写入原有的class文件
                            val code = writer.toByteArray()
                            val classPath = file.parentFile.absolutePath + File.separator + name
                            val fos = FileOutputStream(classPath)
                            fos.write(code)
                            fos.close()
                        }
                    }

                    //5、将class文件返回transform打包路径
                    val dest = transformInvocation.outputProvider?.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                    )
                    println("----src = ${directoryInput.file}")
                    println("----dest = $dest")
                    FileUtils.copyDirectoryToDirectory(directoryInput.file, dest)
                }
            }

            //1、遍历输入的jar路径
            input.jarInputs.forEach { jarInput ->
                println("--jarInput = $jarInput")

                val src = jarInput.file
                val dest = transformInvocation.outputProvider?.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                //2、创建新的空jar包，用来装改完后的jar包
                val temp = src.absolutePath.substring(0, src.absolutePath.length - 4) + "_cat.jar"
                val tempFile = File(temp)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
                val outputStream = JarOutputStream(FileOutputStream(tempFile))

                //3、遍历输入的jar文件
                val jarFile = JarFile(src)
                val entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    //4、对jar文件加入到临时新的jar包中
                    val jarEntry = entries.nextElement()
                    val className = jarEntry.name
                    val inputStream = jarFile.getInputStream(jarEntry)
                    val classEntry = ZipEntry(className)
                    outputStream.putNextEntry(classEntry)

                    if (className.contains("glide")
                        && className.endsWith(".class") && !className.contains("R$")
                        && !className.contains("R.class") && !className.contains("BuildConfig.class")
                    ) {
                        //5、通过ClassReader->ClassVisitor->ClassWriter（读取字节码->访问并修改字节码->输出字节码）
                        val reader = ClassReader(inputStream)
                        val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
                        val visitor = CatClassVisitor(project, writer)
                        reader.accept(visitor, ClassReader.EXPAND_FRAMES)
                        val code = writer.toByteArray()
                        outputStream.write(code)
                    } else {
                        //6、不符合条件的直接源文件读取后放入到临时新的jar包中
                        var len = inputStream.read()
                        while (len != -1) {
                            outputStream.write(len)
                            len = inputStream.read()
                        }
                    }
                    inputStream.close()
                }
                //7、刷新后，改造后的字节码jar包会返回到transform打包路径
                outputStream.flush()
                outputStream.close()
                FileUtils.copyFile(tempFile, dest)
                //8、删除临时新的jar包
                tempFile.delete()

                println("----tempFile = $tempFile")
                println("----dest = $dest")
            }
        }
    }

    private fun addClass(transformInvocation: TransformInvocation?) {
        var dir = File(
            System.getProperties()
                .getProperty("user.dir")
                    + File.separator + "buildSrc"
                    + File.separator + "build"
                    + File.separator + "classes" + File.separator + "kotlin" + File.separator + "main"
                    + File.separator + MethodManager.javaClass.`package`.name.replace(
                ".",
                File.separator
            )
        )
        val dest = transformInvocation?.outputProvider?.getContentLocation(
            dir.name,
            setOf(QualifiedContent.DefaultContentType.CLASSES),
            mutableSetOf(QualifiedContent.Scope.PROJECT),
            Format.DIRECTORY
        )
        println("----src = $dir")
        println("----dest = $dest")
        FileUtils.copyDirectoryToDirectory(dir, dest)
    }
}