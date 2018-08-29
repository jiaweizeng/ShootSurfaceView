package com.example.admin.balasurfaceview

/**
 * Created by zjw on 2018/8/28.
 */
/**
 * 奖励
 */
interface Award {
    /** 获得奖励类型(上面的0或1)  */
    abstract fun getType(): Int

    companion object {
        val DOUBLE_FIRE = 0  //双倍火力
        val LIFE = 1   //1条命
    }
}