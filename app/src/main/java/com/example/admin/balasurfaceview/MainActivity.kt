package com.example.admin.balasurfaceview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val shootSurfaceView by lazy {
        findViewById<ShootSurfaceView>(R.id.shoot)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onDestroy() {
        super.onDestroy()
        shootSurfaceView.release()
    }

}
