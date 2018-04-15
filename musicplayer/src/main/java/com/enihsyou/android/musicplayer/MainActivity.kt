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

        /*https://github.com/Dash-Industry-Forum/dash-live-source-simulator/wiki/Test-URLs*/
        val live_url =
            "http://video-weaver.lax03.hls.ttvnw.net/v1/playlist/CqcC29Ypg6ft7jOz55uCKwdzqZUQSDaQw4Lx2RNh9Jm3Rb4iawLmLE_lgBJctoZoKwHKyWRUW9zhGwo8ET6W3B5_2MxNQhY1guI88rL0cnnetKrGcRKZG7ZervAn-tOXAfixcfyDpU8F19K2hsqn7fV-udbHM3FN4HMvQblr1j9Tx6Scgxow31vps3Q8dpkv_6Rr5tHFN3sljXlbZjBIIfN61S93cGl-eLnCJMyjP34AQS2EMnD0dm_iGoBLddlX40z4Qpa9NamIlCuNLsaeQs_utfnvA5T6vVrWp6AOt3kpcazKeWRLPjAs7Mp6JMrDtJKLa-rbrOES_9jR1Ye6viCGCkHNthjBBnWvIkQ-ffHGm_jcYWE0SveThvLK5tc-r5udeiMJdCvL4xIQYHMnAb5ItFgZZG69FBWwdRoMdECc9T5139I2wJEi.m3u8"
        btn_local.setOnClickListener { startActivity<LocalActivity>() }
        btn_live.setOnClickListener { startActivity3<VideoActivity>(live_url) }
        btn_pics.setOnClickListener { startActivity<PicsActivity>() }
    }
}

inline fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) = beginTransaction().apply(block).commit()

