package com.lockboxth.lockboxkiosk

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.lockboxth.lockboxkiosk.helpers.BaseActivity
import kotlinx.android.synthetic.main.activity_screen_server.*


class ScreenServerActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        allowOpenLocker = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_server)


        val videoUrl = intent.getStringExtra("videoUrl")
        val uri: Uri = Uri.parse(videoUrl)

        videoView.setVideoURI(uri)
//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(videoView)
//        mediaController.setMediaPlayer(videoView)
//        videoView.setMediaController(mediaController)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        onBackPressed()
    }
}