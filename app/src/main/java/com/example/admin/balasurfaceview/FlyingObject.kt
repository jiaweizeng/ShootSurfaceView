package com.example.admin.balasurfaceview

import android.graphics.Bitmap

/**
 * Created by zjw on 2018/8/28.
 */
/**
 * 飞行物(敌机，蜜蜂，子弹，英雄机)
 */
abstract class FlyingObject {
    var x: Int = 0    //x坐标
    var y: Int = 0    //y坐标
    var width: Int = 0    //宽
    var height: Int = 0   //高
    var image: Bitmap?=null    //图片

    /**
     * 检查是否出界
     * @return true 出界与否
     */
    abstract fun outOfBounds(): Boolean

    /**
     * 飞行物移动一步
     */
    abstract fun step()

    /**
     * 检查当前飞行物体是否被子弹(x,y)击(shoot)中
     * @param Bullet 子弹对象
     * @return true表示被击中了
     */
    fun shootBy(bullet: Bullet): Boolean {
        val x = bullet.x  //子弹横坐标
        val y = bullet.y  //子弹纵坐标
        return this.x < x && x < this.x + width && this.y < y && y < this.y + height
    }

}