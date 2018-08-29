package com.example.admin.balasurfaceview

import android.graphics.Bitmap

/**
 * Created by zjw on 2018/8/28.
 * 英雄机:是飞行物
 */
class Hero(var hero0:Bitmap, var hero1:Bitmap,var bullet:Bitmap) : FlyingObject() {

    private var images = arrayOf<Bitmap>()  //英雄机图片
    private var index = 0                //英雄机图片切换索引

    /** 获取双倍火力  */
    /** 设置双倍火力  */
    var isDoubleFire: Int = 0   //双倍火力
    /** 获取命  */
    var life: Int = 0
        private set   //命

    /** 初始化数据  */
    init {
        life = 3   //初始3条命
        isDoubleFire = 0   //初始火力为0
        images = arrayOf<Bitmap>(hero0, hero1) //英雄机图片数组
        image = hero0   //初始为hero0图片
        width = image!!.width
        height = image!!.height
        x = 500
        y = 1200
    }

    /** 增加火力  */
    fun addDoubleFire() {
        isDoubleFire = 40
    }

    /** 增命  */
    fun addLife() {  //增命
        life++
    }

    /** 减命  */
    fun subtractLife() {   //减命
        life--
    }

    /** 当前物体移动了一下，相对距离，x,y鼠标位置   */
    fun moveTo(x: Int, y: Int) {
        this.x = x - width / 2
        this.y = y - height / 2
    }

    /** 越界处理  */
    override fun outOfBounds(): Boolean {
        return false
    }

    /** 发射子弹  */
    fun shoot(): Array<Bullet?> {
        val xStep = width / 4      //4半
        val yStep = 20  //步
        if (isDoubleFire > 0) {  //双倍火力
            val bullets = arrayOfNulls<Bullet>(2)
            bullets[0] = Bullet(x + xStep, y - yStep,bullet)  //y-yStep(子弹距飞机的位置)
            bullets[1] = Bullet(x + 3 * xStep, y - yStep,bullet)
            return bullets
        } else {      //单倍火力
            val bullets = arrayOfNulls<Bullet>(1)
            bullets[0] = Bullet(x + 2 * xStep, y - yStep,bullet)
            return bullets
        }
    }

    /** 移动  */
    override fun step() {
        if (images.isNotEmpty()) {
            image = images[index++ / 10 % images.size]  //切换图片hero0，hero1
        }
    }

    /** 碰撞算法  */
    fun hit(other: FlyingObject): Boolean {

        val x1 = other.x - this.width / 2                 //x坐标最小距离
        val x2 = other.x + this.width / 2 + other.width   //x坐标最大距离
        val y1 = other.y - this.height / 2                //y坐标最小距离
        val y2 = other.y + this.height / 2 + other.height //y坐标最大距离

        val herox = this.x + this.width / 2               //英雄机x坐标中心点距离
        val heroy = this.y + this.height / 2              //英雄机y坐标中心点距离

        return herox > x1 && herox < x2 && heroy > y1 && heroy < y2   //区间范围内为撞上了
    }

}