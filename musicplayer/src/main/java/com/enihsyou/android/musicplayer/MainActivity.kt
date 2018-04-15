package com.enihsyou.android.musicplayer

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

//        val live_url = "http://vm2.dashif.org/livesim/utc_direct-head/testpic_2s/Manifest.mpd"
        /*https://github.com/Dash-Industry-Forum/dash-live-source-simulator/wiki/Test-URLs*/
        val live_url =
            "http://video-weaver.lax03.hls.ttvnw.net/v1/playlist/CqcCd3YcI_-uF1101emLtjdsPSqodzFKwHhbbAZ4FzX9pqBvrn58TTWp7IzYVnQGligOIF6uIYsVs-JHuCReaffa2XqfMYud4DgnhXpeWuulbwP15dAlTueV_phy658c5JvR42Mv101CfpS9DeNm8L33P9FIo18WJCu2G_h_mPq5XLUc5jh9tRpn6QXlxjP8kCH2FHtdbK-w5He0pEyova7YQcxfOxmuxz4sdYXvHQhuCgg80IXkdxf670e_kI3hrALquDFhIqfKAJmbiywF3eeh5HS919iFMKKpo_SCE05p3EBbUdZdPwo2A7KNcVQuQwDdzRnzVdJ4sewYSnLoOnPlsEt_ZrIYxJvSzfQbD1YuiG6ieHgine8nhssTZHm2MjA46hBMFaox4BIQS2FxIZ8MvCSYiTj4gAsSGRoMisOtTMfabjer-2sv.m3u8"
        btn_local.setOnClickListener { startActivity<LocalActivity>() }
        btn_live.setOnClickListener { startActivity3<VideoActivity>(live_url) }
        btn_pics.setOnClickListener { startActivity<PicsActivity>() }
    }
}

inline fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) = beginTransaction().apply(block).commit()

