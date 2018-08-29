package com.example.admin.balasurfaceview

import android.graphics.Bitmap

/**
 * Created by zjw on 2018/8/28.
 */
class Bullet
/** 初始化数据  */
(x: Int, y: Int,image:Bitmap) : FlyingObject() {
    private val speed = 3  //移动的速度

    init {
        this.x = x
        this.y = y
        this.image = image
    }

    /** 移动  */
    override fun step() {
        y -= speed
    }

    /** 越界处理  */
    override fun outOfBounds(): Boolean {
        return y < -height
    }

}