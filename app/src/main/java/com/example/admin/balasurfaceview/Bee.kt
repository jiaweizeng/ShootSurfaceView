package com.example.admin.balasurfaceview

import android.graphics.Bitmap
import java.util.*

/**
 * Created by zjw on 2018/8/28.
 */
/** 蜜蜂  */
class Bee(private var screenWidth:Int, private var screenHeight:Int, bitmap: Bitmap) : FlyingObject(), Award {


    private var xSpeed = 1   //x坐标移动速度
    private val ySpeed = 2   //y坐标移动速度
    /** 获得奖励类型  */
    private val awardType: Int    //奖励类型

    /** 初始化数据  */
    init {

        this.image = bitmap
        if (this.image!=null){
            width = image!!.width
            height = image!!.height
        }
        y = -height
        val rand = Random()
        x = rand.nextInt(screenWidth - width)
        awardType = rand.nextInt(2)   //初始化时给奖励
    }

    /** 越界处理  */
    override fun outOfBounds(): Boolean {
        return y > screenHeight
    }

    /** 获得奖励类型 */
    override fun getType(): Int {
        return awardType
    }

    /** 移动，可斜着飞  */
    override fun step() {
        x += xSpeed
        y += ySpeed
        if (x > screenWidth - width) {
            xSpeed = -1
        }
        if (x < 0) {
            xSpeed = 1
        }
    }
}