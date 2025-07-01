package io.github.shimoranla.utils.basic

class Tracker {
    companion object {
        fun getExceptionSummary(ex: Throwable): String {
            val sb = StringBuilder()
            var current: Throwable? = ex
            while (current != null) {
                sb.append("${current.javaClass.name}: ${current.message}\n")
                current.stackTrace.forEach { stack -> sb.append("    at $stack\n") }
                // 处理被抑制的异常
                current.suppressed?.takeIf { it.isNotEmpty() }?.forEach { suppressed ->
                    sb.append("    Suppressed: ${suppressed.javaClass.name}: ${suppressed.message}\n")
                    suppressed.stackTrace.forEach { stack -> sb.append("        at $stack\n") }
                }
                current = current.cause?.takeIf { it !== current }
                if (current != null) {
                    sb.append("Caused by ")
                }
            }
            return sb.toString()
        }
    }
}