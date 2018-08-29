package com.example.admin.balasurfaceview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*

/**
 * Created by zjw on 2018/8/28.
 */
open class ShootSurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var score = 0 // 得分
    private val intervel = 1000 / 100 // 时间间隔(毫秒)
    private lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var mCanvas: Canvas//绘图的画布
    private var mIsDrawing: Boolean = false//控制绘画线程的标志位

    private var screenWidth: Int? = null
    private var screenHeight: Int? = null

    /** 游戏的当前状态: START RUNNING PAUSE GAME_OVER  */
    private var state: Int = 0
    private val START = 0
    private val RUNNING = 1
    private val PAUSE = 2
    private val GAME_OVER = 3

    private var mBackGround: Bitmap? = null
    private var mStart: Bitmap? = null
    private var mPause: Bitmap? = null
    private var mGameOver: Bitmap? = null
    private var mPaint: Paint? = null
    private var mBullets = arrayOfNulls<Bullet>(0)
    private var mFlying = arrayOfNulls<FlyingObject>(0)


    /*private val  mStart by lazy {
        BitmapFactory.decodeResource(resources,R.mipmap.start)
    }*/
    private val mBee by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.bee)
    }
    /* private val  mPause by lazy {
         BitmapFactory.decodeResource(resources,R.mipmap.pause)
     }*/
    /*private val  mGameOver by lazy {
        BitmapFactory.decodeResource(resources,R.mipmap.gameover)
    }*/
    private val mBullet by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.bullet)
    }
    private val mHero0 by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.hero0)
    }
    private val mHero1 by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.hero1)
    }

    private val mAirPlane by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.airplane)
    }

    /*private val mHero by lazy {
        Hero(mHero0,mHero1,mBullet)
    }*/
    private lateinit var mHero: Hero

    init {
        initView()
    }

    private fun initView() {
        mSurfaceHolder = holder//获取SurfaceHolder对象
        mSurfaceHolder.addCallback(this)//注册SurfaceHolder的回调方法
        isFocusable = true
        isFocusableInTouchMode = true
        keepScreenOn = true
        mHero = Hero(mHero0, mHero1, mBullet)

    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        mHero.x=(width-mHero0.width)/2
        mHero.y=height-mHero0.height
        mBackGround = BitmapUtil.scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.background), width, height)
        mStart = BitmapUtil.scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.start), width, height)
        mPause = BitmapUtil.scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.pause), width, height)
        mGameOver = BitmapUtil.scaleBitmap(BitmapFactory.decodeResource(resources, R.mipmap.gameover), width, height)

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mIsDrawing = false
        mSurfaceHolder.removeCallback(this)//界面销毁的时候注销回调
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

        mPaint = Paint()
        mIsDrawing = true

        Thread {
            while (mIsDrawing) {
                drawImage()
            }
            Thread.sleep(16)
        }.start()


        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (state == RUNNING) {
                    enterAction() // 飞行物入场
                    stepAction() // 走一步
                    shootAction() // 英雄机射击
                    bangAction() // 子弹打飞行物
                    outOfBoundsAction() // 删除越界飞行物及子弹
                    checkGameOverAction() // 检查游戏结束
                }
//                invalidate()// 重绘，调用paint()方法
            }

        }, intervel.toLong(), intervel.toLong())


    }


    private var flyEnteredIndex = 0 // 飞行物入场计数
    /** 飞行物入场  */
    fun enterAction() {
        flyEnteredIndex++
        if (flyEnteredIndex % 40 == 0) { // 400毫秒生成一个飞行物--10*40
            val obj = nextOne() // 随机生成一个飞行物
            mFlying = mFlying.copyOf(mFlying.size + 1)
            mFlying[mFlying.size - 1] = obj
        }
    }

    /** 走一步  */
    fun stepAction() {
        for (i in mFlying) { // 飞行物走一步
            i?.let {
                i.step()
            }
        }

        for (i in mBullets) { // 子弹走一步
            i?.let {
                i.step()
            }
        }
        mHero.step() // 英雄机走一步
    }

    private var shootIndex = 0 // 射击计数

    /** 射击  */
    fun shootAction() {
        shootIndex++
        if (shootIndex % 30 == 0) { // 300毫秒发一颗
            val bs = mHero.shoot() // 英雄打出子弹
            mBullets = mBullets.copyOf(mBullets.size + bs.size)// 扩容
            System.arraycopy(bs, 0, mBullets, mBullets.size - bs.size,
                    bs.size) // 追加数组
        }
    }

    /** 子弹与飞行物碰撞检测  */
    fun bangAction() {
        for (i in mBullets) { // 遍历所有子弹
            i?.let {
                bang(i) // 子弹和飞行物之间的碰撞检查
            }
        }
    }

    /** 删除越界飞行物及子弹  */
    fun outOfBoundsAction() {
        var index = 0 // 索引
        val flyingLives = arrayOfNulls<FlyingObject>(mFlying.size) // 活着的飞行物
        for (i in mFlying) {
            if (i != null && !i.outOfBounds()) {
                flyingLives[index++] = i // 不越界的留着
            }
        }
        mFlying = Arrays.copyOf<FlyingObject>(flyingLives, index) // 将不越界的飞行物都留着

        index = 0 // 索引重置为0
        val bulletLives = arrayOfNulls<Bullet>(mBullets.size)
        for (i in mBullets) {
            if (i != null && !i.outOfBounds()) {
                bulletLives[index++] = i
            }
        }
        mBullets = Arrays.copyOf<Bullet>(bulletLives, index) // 将不越界的子弹留着
    }

    /** 检查游戏结束  */
    fun checkGameOverAction() {
        if (isGameOver()) {
            state = GAME_OVER // 改变状态
        }
    }

    /** 检查游戏是否结束  */
    fun isGameOver(): Boolean {

        mFlying.forEachIndexed { ii, flyingObject ->
            if (ii >= mFlying.size - 1) return@forEachIndexed
            var index = -1
            if (flyingObject != null && mHero.hit(flyingObject)) { // 检查英雄机与飞行物是否碰撞
                mHero.subtractLife() // 减命
                mHero.isDoubleFire = 0 // 双倍火力解除
                index = ii // 记录碰上的飞行物索引
            }
            if (index != -1) {
                mFlying[index] = mFlying[mFlying.size - 1]
                mFlying[mFlying.size - 1] = flyingObject // 碰上的与最后一个飞行物交换
                mFlying = mFlying.copyOf(mFlying.size - 1) // 删除碰上的飞行物
            }
        }

        return mHero.life <= 0
    }

    /**
     * 随机生成飞行物
     *
     * @return 飞行物对象
     */
    fun nextOne(): FlyingObject {
        val random = Random()
        val type = random.nextInt(20) // [0,20)
        return if (type == 0) {
//            if (screenWidth!=null && screenHeight!=null){

            Bee(screenWidth!!, screenHeight!!, mBee)
//            }
        } else {
            Airplane(screenWidth!!, screenHeight!!, mAirPlane)
        }
    }

    /** 子弹和飞行物之间的碰撞检查  */
    fun bang(bullet: Bullet) {
        var index = -1 // 击中的飞行物索引
        for (i in mFlying.withIndex()) {
            val obj = i.value
            if (obj != null) {
                if (obj.shootBy(bullet)) { // 判断是否击中
                    index = i.index // 记录被击中的飞行物的索引
                    break
                }
            }
        }
        if (index != -1) { // 有击中的飞行物
            val one = mFlying[index] // 记录被击中的飞行物

            val temp = mFlying[index] // 被击中的飞行物与最后一个飞行物交换
            mFlying[index] = mFlying[mFlying.size - 1]
            mFlying[mFlying.size - 1] = temp

            mFlying = mFlying.copyOf(mFlying.size - 1) // 删除最后一个飞行物(即被击中的)

            // 检查one的类型(敌人加分，奖励获取)
            if (one is Enemy) { // 检查类型，是敌人，则加分
                val e = one as Enemy // 强制类型转换
                score += e.score // 加分
            } else if (one is Award) { // 若为奖励，设置奖励
                val a = one as Award
                val type = a.getType() // 获取奖励类型
                when (type) {
                    Award.DOUBLE_FIRE -> mHero.addDoubleFire() // 设置双倍火力
                    Award.LIFE -> mHero.addLife() // 设置加命
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                when (state) {
                    START -> state = RUNNING // 启动状态下运行
                // 游戏结束，清理现场
                    GAME_OVER -> {
                        mBullets = arrayOfNulls(0)// 清空子弹
                        mFlying = arrayOfNulls(0) // 清空飞行物
                        mHero = Hero(mHero0, mHero1, mBullet) // 重新创建英雄机
                        score = 0 // 清空成绩
                        state = START // 状态设置为启动
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (state == RUNNING) { // 运行状态下移动英雄机--随鼠标位置
                    val x = event.x
                    val y = event.y
                    mHero.moveTo(x.toInt(), y.toInt())
                }
            }
        }
        return true
    }

    private fun drawImage() {
        try {

            mCanvas = mSurfaceHolder.lockCanvas()
            /** 画背景 */
            mCanvas.drawBitmap(mBackGround, 0f, 0f, mPaint)
//            mCanvas.drawBitmap(mAirPlane,mAirPlane.width.toFloat(),mAirPlane.height.toFloat(),mPaint)
            /** 画英雄机 */
            mCanvas.drawBitmap(mHero.image, mHero.x.toFloat(), mHero.y.toFloat(), mPaint)
            /** 画子弹 */
            for (bb in mBullets) {
                bb?.let {
                    mCanvas.drawBitmap(bb.image, (bb.x.minus(bb.width / 2)).toFloat(),
                            bb.y.toFloat(), mPaint)
                }
            }
            /** 画飞行物 */
            for (ff in mFlying) {
                ff?.let {
                    mCanvas.drawBitmap(ff.image, ff.x.toFloat(), ff.y.toFloat(), mPaint)
                }
            }

            /** 画分数 */
            val x = 20 // x坐标
            var y = 35// y坐标

            mPaint?.textSize = 34f

            mPaint?.color = Color.parseColor("#FFFFFFFF")

            mCanvas.drawText("SCORE:$score", x.toFloat(), y.toFloat(), mPaint) // 画分数
            y += 40 // y坐标增20
            mCanvas.drawText("LIFE:" + mHero.life, x.toFloat(), y.toFloat(), mPaint) // 画命

            paintState()


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas)//保证每次都将绘图的内容提交
            } catch (e: Exception) {
            }
        }

    }

    /** 画游戏状态  */
    fun paintState() {
        when (state) {
            START // 启动状态
            -> mCanvas.drawBitmap(mStart, 0f, 0f, mPaint)
            PAUSE // 暂停状态
            -> mCanvas.drawBitmap(mPause, 0f, 0f, mPaint)
            GAME_OVER // 游戏终止状态
            -> mCanvas.drawBitmap(mGameOver, 0f, 0f, mPaint)
        }
    }

    fun release() {
        mSurfaceHolder.removeCallback(this)//界面销毁的时候注销回调
    }


}