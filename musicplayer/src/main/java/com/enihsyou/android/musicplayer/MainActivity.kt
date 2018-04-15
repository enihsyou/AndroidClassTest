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
            "http://video-weaver.lax03.hls.ttvnw.net/v1/playlist/CqcC7ioz7IZbZiKjzo1Cy9Ntadv4qRu23x8AGTizUah8Wh7HHeTANJROBpxAEwHUFSBrYqAWSQJrJzYsBMLPzPihQEU7_BkZUrGDMZTQPMqD6Gq3zX2HOW-8sofsOJ4nqqvsavJpUxCWBvJwhFbbEJ0E5bMKGCJMi2ONqZS5JEbhjF7yllFlJcGoiLBHfk0HLHwpZ4iokb_NPkbaYa0BB3MxW5YjdvCJNG8Sf-LA9-kCbpe3xkKAANFjDiUEAntZM8EGzYSMzI1fKhk3eall5thfezJeWbQXBfOr1DeAmWihpPHTwG8Vs5wwRyuvzce93yxzJIqJKBgmtL98vxmp3eKrbzH_ELND9ETH7348HJ2v4plUJ9rIfNrxSyMcqtoqO2rw4N-zqs5-ORIQCi5ron3keozAN9feXiJAwRoM1XZlH9w2yVizyxAV.m3u8"
        btn_local.setOnClickListener { startActivity<LocalActivity>() }
        btn_live.setOnClickListener { startActivity3<VideoActivity>(live_url) }
        btn_pics.setOnClickListener { startActivity<PicsActivity>() }
    }
}

inline fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) = beginTransaction().apply(block).commit()

