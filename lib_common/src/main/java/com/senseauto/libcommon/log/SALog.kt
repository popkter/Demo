package com.senseauto.libcommon.log

/**
 * Kotlin日志打印
 */
object SALog {

    enum class LogLevel {
        RED,
        GREEN,
        YELLOW,
        BLUE,
        PURPLE,
        CYAN,
        WHITE
    }
    @JvmStatic
    fun v(msg: String) {
        v(getCallerClassName(), msg)
    }

    @JvmStatic
    fun v(tag: String, msg: String) {
        printColoredText("$tag: $msg", LogLevel.BLUE)
    }


    @JvmStatic
    fun d(msg: String) {
        d(getCallerClassName(), msg)
    }

    @JvmStatic
    fun d(tag: String, msg: String) {
        printColoredText("$tag: $msg", LogLevel.BLUE)
    }


    @JvmStatic
    fun i(msg: String) {
        i(getCallerClassName(), msg)
    }

    @JvmStatic
    fun i(tag: String, msg: String) {
        printColoredText("$tag: $msg", LogLevel.GREEN)
    }


    @JvmStatic
    fun w(msg: String) {
        w(getCallerClassName(), msg)
    }

    @JvmStatic
    fun w(tag: String = getCallerClassName(), msg: String) {
        printColoredText("$tag: $msg", LogLevel.YELLOW)
    }

    @JvmStatic
    fun e(msg: String) {
        e(getCallerClassName(), msg)
    }

    @JvmStatic
    fun e(tag: String = getCallerClassName(), msg: String) {
        printColoredText("$tag: $msg", LogLevel.RED)
    }

    private fun printColoredText(text: String, level: LogLevel) {
        // todo 控制log等级,可以接入其他Log工具
        val colorCode = when (level) {
            LogLevel.RED -> "\u001B[31m"
            LogLevel.GREEN -> "\u001B[32m"
            LogLevel.YELLOW -> "\u001B[33m"
            LogLevel.BLUE -> "\u001B[34m"
            LogLevel.PURPLE -> "\u001B[35m"
            LogLevel.CYAN -> "\u001B[36m"
            LogLevel.WHITE -> "\u001B[37m"
        }
        println("$colorCode$text\u001B[0m")
    }

    private fun getCallerClassName(): String {
        // 获取当前线程的堆栈跟踪
        val stackTrace = Thread.currentThread().stackTrace

        // 获取当前类的全限定名
        val currentClassName = javaClass.name

        // 遍历堆栈跟踪元素以找到第一个不属于当前类或 Java 标准库的类
        for (element in stackTrace) {
            // 如果元素不属于当前类并且不是 Java 标准库的一部分，则返回该类名
            if (element.className != currentClassName && !element.className.startsWith("java.lang.Thread")) {
                return "${element.className}:${element.lineNumber}"
            }
        }
        return ""
    }

}