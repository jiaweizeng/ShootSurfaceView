package com.example.admin.balasurfaceview

import android.graphics.Bitmap
import java.util.*

/**
 * Created by zjw on 2018/8/28.
 */
class Airplane(private var screenWidth:Int, private var screenHeight:Int, bitmap: Bitmap) : FlyingObject(), Enemy {
    private val speed = 3  //移动步骤

    /** 获取分数  */
    override val score: Int
        get() = 5

    /** 初始化数据  */
    init {
        this.image = bitmap
        if (image!=null){
            width = image!!.width
            height = image!!.height
        }
        y = -height
        val rand = Random()
        x = rand.nextInt(screenWidth - width)
    }

    /** //越界处理  */
    override fun outOfBounds(): Boolean {
        return y > screenHeight
    }

    /** 移动  */
    override fun step() {
        y += speed
    }

}