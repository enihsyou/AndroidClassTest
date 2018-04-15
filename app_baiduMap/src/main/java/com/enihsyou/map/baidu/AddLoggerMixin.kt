package com.enihsyou.map.baidu

import android.util.Log

private fun getTag(clazz: Class<*>): String {
    val tag = clazz.simpleName
    return if (tag.length <= 23) {
        tag
    } else {
        tag.substring(0, 23)
    }
}

interface AddLoggerMixin {
    val loggerTag: String
        get() = getTag(javaClass)
}

inline fun AddLoggerMixin.debug(message: () -> Any?) {
    val tag = loggerTag
    Log.d(tag, message()?.toString() ?: "null")
}
inline fun AddLoggerMixin.error(message: () -> Any?) {
    val tag = loggerTag
    Log.e(tag, message()?.toString() ?: "null")
}
