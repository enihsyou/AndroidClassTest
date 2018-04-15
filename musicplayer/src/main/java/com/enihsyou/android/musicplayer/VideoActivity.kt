package com.enihsyou.android.musicplayer

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class VideoActivity : AppCompatActivity() {

    private var playbackPosition: Long = 0

    private fun hideSystemUi() {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
        hideSystemUi()
    }

    /*https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33*/
    private fun initializePlayer() {
        /*获取视频URL*/
        val videoUrl = intent.getStringExtra(INTENT_VIDEO_URL_PLAIN)
        val dashUrl = intent.getStringExtra(INTENT_VIDEO_URL_DASH)
        val hlsUrl = intent.getStringExtra(INTENT_VIDEO_URL_HLS)

        /*构建视频数据*/
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelector = DefaultTrackSelector(bandwidthMeter)
        val player = ExoPlayerFactory.newSimpleInstance(ctx, trackSelector)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(ctx, "enihsyou", bandwidthMeter)

        val mediaSource: MediaSource = when {
            videoUrl != null -> {
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl))
            }

            dashUrl != null  -> {
                DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceFactory),
                    DefaultDataSourceFactory(ctx, "enihsyou")
                ).createMediaSource(Uri.parse(dashUrl))
            }

            hlsUrl != null   -> {
                HlsMediaSource
                    .Factory(DefaultHttpDataSourceFactory("enihsyou"))
                    .createMediaSource(Uri.parse(hlsUrl))
            }

            else             -> throw NoWhenBranchMatchedException()
        }
        player.prepare(mediaSource)
        player.seekTo(playbackPosition)

        fullscreen_content.player = player

        player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        fullscreen_content?.player?.run {
            playbackPosition = currentPosition
            release()
        }
    }

    companion object {
        const val INTENT_VIDEO_URL_PLAIN = "video_url_plain"
        const val INTENT_VIDEO_URL_DASH = "video_url_dash"
        const val INTENT_VIDEO_URL_HLS = "video_url_hls"
    }
}

@Suppress("FINAL_UPPER_BOUND")
inline fun <reified T : VideoActivity> AppCompatActivity.startActivity(url: String) =
    startActivity<T>(VideoActivity.INTENT_VIDEO_URL_PLAIN to url)

@Suppress("FINAL_UPPER_BOUND")
inline fun <reified T : VideoActivity> AppCompatActivity.startActivity2(url: String) =
    startActivity<T>(VideoActivity.INTENT_VIDEO_URL_DASH to url)

@Suppress("FINAL_UPPER_BOUND")
inline fun <reified T : VideoActivity> AppCompatActivity.startActivity3(url: String) =
    startActivity<T>(VideoActivity.INTENT_VIDEO_URL_HLS to url)

