package com.enihsyou.map.baidu

import java.io.Serializable

data class Info(
    val latitude: Double,
    val longitude: Double,
    val imageId: Int,
    val name: String,
    val distance: String
) :Serializable{
companion object {

    val infos = listOf(
        Info(31.297430, 121.5614234, R.drawable.hall, "上海理工大学-音乐堂", "200米"),
        Info(31.2985566, 121.55901, R.drawable.hall2, "上海理工大学-学生活动中心", "250米"),
        Info(31.29247789289, 121.560273, R.drawable.hall3, "卜蜂莲花(周家嘴路店)", "1250米")
    )
}
}
