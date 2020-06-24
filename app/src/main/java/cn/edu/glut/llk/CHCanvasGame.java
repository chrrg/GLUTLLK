package cn.edu.glut.llk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_TEXTURE_2D;

interface GameObjectDraw{
    void onDraw(Canvas c,Paint paint);
}
interface GameInit{
    void onInit();
    void onSetGame(CHCanvasGame game);
}
interface ObjectStringChange{
    String onListener();
}
interface OnTouchListener{
    boolean onTouchStart(MotionEvent event);
    boolean onTouchMove(MotionEvent event);
    boolean onTouchEnd(MotionEvent event);
}
//class GameObject{
//    int x,y,w,h;
//    private int backColor=0;
//    private GameObjectDraw od=null;
//    private Bitmap baseBitmap=null;
//    private String text=null;
//    private ObjectStringChange textChange=null;
//    private Paint selfPaint;
//    private int index=100;
//    private Bitmap pic=null;
//    private Movie gif;
//    private long gifStart;
//    private Vector<GameObject> children;
//    OnTouchListener touch;
//    private boolean display=true;
//    void onTouch(OnTouchListener touch){
//        this.touch=touch;
//    }
//    void setDisplay(boolean display){
//        this.display=display;
//    }
//    boolean isIn(int touchX,int touchY){
//        return touchX>x&&touchX<x+w&&touchY>y&&touchY<y+h;
//    }
//    void setPic(Bitmap pic){
//        baseBitmap=null;
//        this.pic=pic;
//    }
//    void setGif(Movie gif){
//        baseBitmap=null;
//        gifStart=android.os.SystemClock.uptimeMillis();
//        this.gif=gif;
//    }
//    void setIndex(int i){
//        index=i;
//    }
//    int getIndex(){
//        return index;
//    }
//    public void clear(){
//        baseBitmap=null;
//    }
//    void setPaint(Paint paint){
//        selfPaint=paint;
//    }
//    void setString(String text){
//        baseBitmap=null;
//        this.text=text;
//    }
//    void setString(ObjectStringChange text){
//        baseBitmap=null;
//        textChange=text;
//    }
//    void setBackColor(int backColor) {
//        baseBitmap=null;
//        this.backColor=backColor;
//    }
//    void removeFromGame(CHCanvasGame game){
//        game.remove(this);
//    }
//    void draw(Canvas c,Paint paint2,GameCamera camera){
//        if(!display)return;//不显示的不渲染
//        if(textChange !=null){
//            String temp=textChange.onListener();
//            if(text==null||!text.equals(temp)){
//                text=temp;
//                baseBitmap=null;
//            }
//        }
//        if(gif !=null){
//            baseBitmap=null;
//        }
//        Paint paint;
//        if(selfPaint!=null)paint=selfPaint;else paint=paint2;
//        if(baseBitmap==null) {
//            baseBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(baseBitmap);
//            if(backColor!=0){
//                canvas.drawColor(backColor);
//            }
//            if (pic != null){
//                pic=Bitmap.createScaledBitmap(pic, this.w, this.h, true);
//                canvas.drawBitmap(pic,0,0,paint);
//            }
//            if (gif != null){
//                Bitmap tempBitmap = Bitmap.createBitmap(gif.width(),gif.height(), Bitmap.Config.ARGB_8888);
//                Canvas temp=new Canvas(tempBitmap);
//                int duration=gif.duration();
//                if(duration==0)duration=1000;
//                gif.setTime((int) ((android.os.SystemClock.uptimeMillis() - gifStart) % duration));
//                gif.draw(temp,0,0);
//                tempBitmap=Bitmap.createScaledBitmap(tempBitmap, this.w, this.h, true);
//                canvas.drawBitmap(tempBitmap,0,0,paint);
//            }
//            if (this.od != null){
//                this.od.onDraw(canvas, paint);
//            }
//            if(text != null){
//                canvas.drawText(text, 0,this.h,paint);
//            }
//        }
//        c.drawBitmap(baseBitmap,this.x-camera.getX(),this.y-camera.getY(),paint);
//    }
//    void set(int x, int y, int w, int h) {
//        this.x=x;
//        this.y=y;
//        this.w=w;
//        this.h=h;
//    }
//    void setDraw(GameObjectDraw od){
//        this.od=od;
//    }
//}
class GameObject{
    private int x,y,w,h;
    private String Tag="obj";
    private Vector<GameObject> children= new Vector<>();
    private GameObject parent=null;
    private boolean change=true;//false 说明没有改动 true 需要重绘
    private Bitmap buffer=null;
    private CHCanvasGame game;
    private Paint paint=null;
    private String text=null;
    void addChild(GameObject o){
        if(o==null)return;
        children.add(o);
    }
    GameObject(CHCanvasGame game){
        this.game=game;
    }
    GameObject(CHCanvasGame game,GameObject parent){
        this.game=game;
        this.parent=parent;
    }
    private void setPaint(Paint paint){
        this.paint=paint;
    }
    private Paint getPaint(){
        if(paint==null)
            if(parent==null)paint=new Paint();else paint=parent.getPaint();
        return paint;
    }
    private Bitmap getBitmap(){
        if(change){
//            draw();
        }
        return buffer;
    }
//    private void updateView(int x,int y){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
//        change=true;
//        if(parent!=null)parent.updateView();
//    }
    private void updateView(){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
        change=true;
        if(parent!=null)parent.updateView();
    }
//    private void change(){
//        updateView();
//    }

//    private Bitmap doDraw(Bitmap temp){
//        for(GameObject ob:children){
//            ob.doDraw();
//        }
//        Bitmap temp = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);//重绘当前组件
//        Canvas canvas = new Canvas(temp);
//        canvas.drawColor(Color.RED);
//        buffer=temp;//缓存这个已绘制的视图
//        change=false;//绘制完成，清除这个组件的状态 修改为无改变状态
//    }
//    private void draw(){
////
////        doDraw();
////    }
    void useCamera(Canvas canvas) {//使用摄像机，在画布上对这个对象进行渲染
        GameCamera camera=game.getCamera();
        camera.fixCamera();//平滑相机移动
        if(change){//若有改变或相机改变 需要更新
//            this.doDraw();
            Rect srcRect=new Rect(camera.getX(),camera.getY(),game.getWidth(),game.getHeight());
            Rect dstRect=new Rect(0,0,game.getWidth(),game.getHeight());
            canvas.drawBitmap(buffer,srcRect,dstRect,getPaint());
        }
//        return buffer;
    }
    public String getTag() {
        return Tag;
    }
    public void setTag(String tag) {
        Tag = tag;
    }
    public void setText(String text) {
        if(!text.equals(this.text))this.text=text;
        updateView();//通知需要更新
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
class GameCamera{
    private int x=0;//当前摄像机位置
    private int y=0;
    private int cameraX;//平滑摄像机预计到达的位置
    private int cameraY;
    private CHCanvasGame game;
    private void setX(int x){
        this.x=x;
        cameraX=x;
    }
    private void setY(int y){
        this.y=y;
        cameraY=y;
    }
    int getCameraX(){
        return cameraX;
    }
    int getCameraY(){
        return cameraY;
    }
    int getX(){
        return x;
    }
    int getY(){
        return y;
    }
    void moveX(int x){
        this.x+=x;
        cameraX=this.x;
    }
    void moveY(int y){
        this.y+=y;
        cameraY=this.y;
    }
    void setCameraX(int x){
        cameraX=x;
    }
    void setCameraY(int y){
        cameraY=y;
    }
    boolean fixCamera(){//平滑相机
        if(cameraX==x&&cameraY==y)return false;
        if(cameraX!=x)x=(int)(x+(cameraX-x)*0.15);
        if(cameraY!=y)y=(int)(y+(cameraY-y)*0.15);
        return true;
    }
    GameCamera(CHCanvasGame game){
        this.game=game;
    }
    GameCamera(CHCanvasGame game,int x,int y){
        this.game=game;
        setX(x);
        setY(y);
    }
}
class CHOpenGL implements GLSurfaceView.Renderer{
    private int maxFps = 50;//设置帧率
    private long curfpsTime=0;
    private int fps=0;
    private int curfps=0;
    CHCanvasGame game=null;
    CHOpenGL(CHCanvasGame game){
        this.game=game;
    }
    void setMaxFps(int maxFps){
        this.maxFps=maxFps;
    }
    int getMaxFps(){
        return maxFps;
    }
    public int getFps() {
        return fps;
    }
    // 原始的矩形区域的顶点坐标
    private static final float CUBE[] = {
            -1.0f, -1.0f,0.0f, // v1
            1.0f, -1.0f, 0.0f, // v2
            -1.0f, 1.0f,0.0f,  // v3
            1.0f, 1.0f,  0.0f, // v4
    };
    // 纹理坐标，每个坐标的纹理采样对应上面顶点坐标。
    // 纹理为0 ~ 2，会有四分屏
    private static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f, // v1
            1.0f, 1.0f, // v2
            0.0f, 0.0f, // v3
            1.0f, 0.0f, // v4
    };
    //设置颜色
    private static final float COLORS[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f
    };
    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    // 初始化buffer
    private static FloatBuffer initBuffer(float[] buffers) {
        // 先初始化buffer,数组的长度*4,因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(buffers.length * 4);
        // 数组排列用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mbb.asFloatBuffer();
        floatBuffer.put(buffers);
        floatBuffer.flip();
        return floatBuffer;
    }
    private FloatBuffer mCubeBuffer;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mMatrixBuffer;
    // 加载Buffer
    private void loadBuffer() {
        mCubeBuffer = initBuffer(CUBE);
        mTextureBuffer = initBuffer(TEXTURE_NO_ROTATION);
        mColorBuffer = initBuffer(COLORS);
        mMatrixBuffer = initBuffer(mMVPMatrix);
    }
    int mPositionHandle;
    int mColorHandle;
    int mTextureHandle;
    int mMvpMatrixHandle;
    int mGLUniformTexture;
    int mGLTextureId;  // 纹理ID
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    Bitmap b;
    ByteBuffer mBuf;
    private int assembleProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        if (program == 0) throw new RuntimeException("Cannot create GL program: " + GLES20.glGetError());
        GLES20.glAttachShader(program, vertexShader);//将顶点着色器加入到程序
        GLES20.glAttachShader(program, fragmentShader);//将片元着色器加入到程序中
        GLES20.glLinkProgram(program);//连接到着色器程序
        int[] mLinkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, mLinkStatus, 0);
        if (mLinkStatus[0] != GLES20.GL_TRUE) {// 获取program的链接情况
            Log.e("Linking Failed", "Could not link program: " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//                surfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        String VERTEX_SHADER = "" +
                "precision mediump float;\n" +
                "attribute vec4 position;\n" +               // 顶点着色器的顶点坐标,由外部程序传入
                "attribute vec2 inputTextureCoordinate;\n" + // 传入的纹理坐标
                "attribute vec4 aColor;\n" +
                "varying vec4 mColor;\n" +                    // 传入的纹理坐标
                "uniform mat4 transform;" +                   // 变换矩阵
                "varying vec2 textureCoordinate;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = transform*position;\n" +
                "    mColor = aColor;\n" +
                "    textureCoordinate = inputTextureCoordinate;\n" + // 最终顶点位置
                "}";
        // 光栅化后产生了多少个片段，就会插值计算出多少个varying变量，同时渲染管线就会调用多少次片段着色器
        String FRAGMENT_SHADER =
                "precision mediump float;\n" +
                        "varying vec2 textureCoordinate;\n" + // 最终顶点位置，上面顶点着色器的varying变量会传递到这里
                        "uniform sampler2D vTexture;\n" + // 外部传入的图片纹理 即代表整张图片的数据
                        "varying vec4 mColor;\n" + // 传入的纹理坐标
                        "void main()\n" +
                        "{" +
                        "gl_FragColor =mix ( texture2D(vTexture,vec2(1.0-textureCoordinate.x,textureCoordinate.y)) , mColor,0.2);" + // 增加1.0 - ，为了使图像反转
                        "}";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);//加载顶点
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);//加载
        int mProgram=assembleProgram(vertexShader,fragmentShader);
        GLES20.glUseProgram(mProgram);//必须 使用

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "transform");
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        mGLTextureId = -1;
        int[] textures = new int[1];
        // 加载纹理
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //纹理也有坐标系，称UV坐标，或者ST坐标
        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        b = game.getImage("1.png");
        mBuf = ByteBuffer.allocate(b.getWidth()*b.getHeight()*4);
        b.copyPixelsToBuffer(mBuf);
        GLES20.glTexImage2D(GL_TEXTURE_2D,0,GLES20.GL_RGBA,b.getWidth(),b.getHeight(),0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE, null);
//                GLES20.glUniform1i(mGLUniformTexture, 1); // 设置第一层纹理
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0+1);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId);

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("TAG", "glCheckFramebufferStatus error");
        } else {
            mGLTextureId = textures[0];
            GLES20.glUniform1i(mGLUniformTexture, 1); // 设置第一层纹理
        }
//                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0,0,0,b);

//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);// S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);// T轴的拉伸方式为重复
//                GLES20.glGenTextures(1,);
    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // 设置窗口大小
        Log.e("TAG", "onSurfaceChanged: width=" + width + " height=" + height);
        int w = b.getWidth();
        int h = b.getHeight();

        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
        loadBuffer();
    }
    @Override
    public void onDrawFrame(GL10 gl10) {

//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        long startMs = System.currentTimeMillis();
        curfps++;
        if(curfpsTime==0){
            curfpsTime=System.currentTimeMillis();
        }
        if(startMs>curfpsTime+1000){
            Log.e("当前FPS:", String.valueOf(curfps));
            curfpsTime=startMs;
            fps=curfps;
            curfps=0;
        }

        // 顶点
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mCubeBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 顶点着色器的纹理坐标
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 8, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        // 传入的图片纹理
        if (mGLTextureId != -1) {
//                    Log.e("TAG", "onDrawFrame: mGLTextureId=" + mGLTextureId);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GL_TEXTURE_2D, mGLTextureId);
//            GLES20.glTexSubImage2D();
//            Log.i("allocate", String.valueOf(b.getWidth()*b.getHeight()*4));

//            GLES11Ext.glEGLImageTargetTexture2DOES(GL_TEXTURE_2D,mBuf);
            GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0,0, b);
        }
        // 变换矩阵
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMatrixBuffer);
        //获取片元着色器的vColor成员的句柄

        //设置绘制三角形的颜色
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glVertexAttribPointer(mColorHandle, 4,
//                GLES20.GL_FLOAT, false,
//                4, mColorBuffer);
        // 绘制顶点 ，方式有顶点法和索引法
        // GLES20.GL_TRIANGLE_STRIP即每相邻三个顶点组成一个三角形，为一系列相接三角形构成
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 顶点法，按照传入渲染管线的顶点顺序及采用的绘制方式将顶点组成图元进行绘制
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
//        GLES20.glDisableVertexAttribArray(mTextureHandle);
//        GLES20.glDisableVertexAttribArray(mColorHandle);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        canvas = surfaceholder.lockHardwareCanvas();
//                    }else{
//                        canvas = surfaceholder.lockCanvas();
//                    }
//                    if(canvas!=null&&camera!=null) {
//                        drawOnce(canvas, paint);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (canvas != null) {
//                        surfaceholder.unlockCanvasAndPost(canvas);
//                    }
//                }
        long endMs = System.currentTimeMillis();
        long needTime = 1000 / maxFps;
        long usedTime = endMs - startMs;
        if (usedTime < needTime) {
            try {
                Thread.sleep(needTime - usedTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class CHCanvasGame {
    private GameObject root=null;
//    private Vector<GameObject> obj=new Vector<>();
    private GameCamera camera;
    private Canvas canvas;
    private Paint paint;
    private GLSurfaceView surfaceview;
    private CHOpenGL openGL;
//    private SurfaceHolder surfaceholder;
    private int w,h;
    private boolean isRunning;

    private int backGroundColor=0;
    private long startTime=System.currentTimeMillis();
    private Activity activity;
    long getTime(){
        return System.currentTimeMillis()-startTime;
    }
    void showInputMethod(){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(surfaceview, 0);//InputMethodManager.SHOW_FORCED 表示强制显示
        }
    }
    void hideInputMethod(){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(surfaceview.getWindowToken(), 0);//InputMethodManager.HIDE_NOT_ALWAYS
        }
    }

    CHCanvasGame(){

    }
    Activity getActivity(){
        return activity;
    }

    Bitmap getImage(String filename){
        AssetManager am=activity.getAssets();
        try {
            return BitmapFactory.decodeStream(am.open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    Movie getGif(String filename){
        AssetManager am=activity.getAssets();
        try {
            return Movie.decodeStream(am.open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void setBackGroundColor(int color){
        backGroundColor=color;
    }
    void setMaxFPS(int fps){
        openGL.setMaxFps(fps);
    }
    int getFPS(){
        return openGL.getFps();
    }
    int getWidth(){
        return this.w;
    }
    int getHeight(){
        return this.h;
    }
    void setCamera(GameCamera c){this.camera=c;}
    GameCamera getCamera() {return camera;}
    private static int parseInt(String s) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException x) {
            return 0;
        }
    }
    private static int parseInt(String s, int defaultValue) {
        if (s == null) return defaultValue;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException x) {
            return defaultValue;
        }
    }
    private int dpx(String in){
        if(in.endsWith("%"))return parseInt(in.split("%")[0])/100*getWidth();
        return parseInt(in);
    }
    private int dpy(String in){
        if(in.endsWith("%"))return parseInt(in.split("%")[0])/100*getHeight();
        return parseInt(in);
    }
    private GameObject parseElement(Element r, GameObject parent){
        if(r==null)return null;
        GameObject r2=new GameObject(this,parent);
        String tagName=r.getTagName();
        r2.setTag(tagName);
        NamedNodeMap attris = r.getAttributes();
        for (int i = 0; i < attris.getLength(); i++) {
            Attr attr = (Attr) attris.item(i);
            String attrName=attr.getName();
            String value=attr.getValue();
            switch(attrName){
                case "w":
                    r2.setW(dpx(value));
                    break;
                case "h":
                    r2.setH(dpy(value));
                    break;
                case "x":
                    r2.setX(dpx(value));
                    break;
                case "y":
                    r2.setY(dpy(value));
                    break;
                case "r":
                    r2.setW(getWidth()+r2.getX()-dpx(value));
                    break;
                case "b":
                    r2.setH(getHeight()+r2.getY()-dpy(value));
                    break;
                case "text":
                    r2.setText(value);
                    break;
                case "background":
                    Color.parseColor(value);
                    break;
                case "src":
                    if("gif".equals(tagName)||value.endsWith(".gif")){
                        //gif

                    }else{
                        //图片

                    }
//                    r2.setW(dpx(r.getAttribute("w")));
//                    break;
                    break;


            }
//            System.out.print(" " +  + "=\"" + attr.getValue() + "\"");
        }
        NodeList nodeList = r.getChildNodes();
        Node childNode;
        int num = nodeList.getLength();
        for (int temp = 0; temp < num; temp++) {
            childNode = nodeList.item(temp);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) // 判断是否属于节点
                r2.addChild(parseElement((Element) childNode,r2));
        }
        return r2;
    }
    GameObject getGameObjectFromXML(String filename){
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        try {
            AssetManager am=activity.getAssets();
            DocumentBuilder db = xml.newDocumentBuilder();
            Document doc = db.parse(am.open(filename));
            Element r = doc.getDocumentElement();//根目录
            return parseElement(r,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void sort(){
//        Collections.sort(obj, new Comparator<GameObject>() {
//            @Override
//            public int compare(GameObject o1, GameObject o2) {
//                if (o1 != null && o2 != null)return Integer.compare(o1.getIndex(), o2.getIndex());
//                return 0;
//            }
//        });
    }
    private void drawOnce(Canvas c,Paint paint){
        if(root==null)return;//没有根对象

//        camera.fixCamera();
//        if(backGroundColor!=0)canvas.drawColor(backGroundColor);

//        sort();
        root.useCamera(c);//对游戏对象进行更新

//        for(GameObject ob:obj){
//            ob.draw(c,paint,camera);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    CHCanvasGame (Activity activity, int id, final GameInit init){
        surfaceview=new GLSurfaceView(activity);
        init.onSetGame(this);
        this.activity=activity;
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
//        surfaceview = activity.findViewById(id);
        final ArrayList<GameObject> kv = new ArrayList<>();
//        surfaceview.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                int x = (int)event.getX()+camera.getX();
//                int y = (int)event.getY()+camera.getY();
//                boolean result=false;
//                Log.i("event.getAction()", String.valueOf(event.getActionIndex())+"|"+String.valueOf(event.getActionMasked()));
//                int action=event.getActionMasked();
//                switch(action){
//                    case MotionEvent.ACTION_DOWN://一个点按下
//                        Log.i("一个点按下","一个点按下");
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN://多个点按下
//                        Log.i("多个点按下","多个点按下");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Log.i("移动","移动");
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP:
//                        Log.i("松开了某一个","松开了某一个");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        Log.i("全部松开了","全部松开了");
//                        break;
//                }
//                return true;
//                synchronized(obj) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//ACTION_MOVE ACTION_UP
//                        //                    sort();
//                        for (GameObject ob : obj) {
//                            if (ob.isIn(x, y)) {
//                                kv.add(ob);
//                                Log.i("event.getActionIndex()", String.valueOf(event.getActionIndex()));
////                                kv.put(event.getActionIndex(),ob);
//                                if (ob.touch != null) result = ob.touch.onTouchStart(event);
//                                if (result) return true;//处理完成
//                            }
//                        }
//                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                        //                    sort();
//                        for (GameObject ob : obj) {
//                            if (ob.isIn(x, y)) {
//                                if (ob.touch != null) result = ob.touch.onTouchMove(event);
//                                if (result) return true;//处理完成
//                            }
//                        }
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        //                    sort();
//                        for (GameObject ob : obj) {
//                            if (ob.isIn(x, y)) {
//                                if (ob.touch != null) result = ob.touch.onTouchEnd(event);
//                                if (result) return true;//处理完成
//                            }
//                        }
//                    }
//                }
//                return true;
//            }
//        });
        surfaceview.setEGLContextClientVersion(2);
        openGL=new CHOpenGL(this);
        surfaceview.setRenderer(openGL);
//        surfaceholder = surfaceview.getHolder();
//        surfaceholder.addCallback(new SurfaceHolder.Callback(){
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                isRunning = true;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (isRunning) {
//
////                                try {
////                                    Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
//
//                        }
//                    }
//                }).start();
//            }
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {isRunning=false;}
//        });
        surfaceview.post(new Runnable() {
            @Override
            public void run() {
                w=surfaceview.getWidth();
                h=surfaceview.getHeight();
                init.onInit();
            }
        });
    }
    void setGameObject(GameObject root) {
        this.root=root;
    }
    GameObject getGameObject() {
        return root;
    }
    GLSurfaceView getContentView(){
        return surfaceview;
    }
//    void remove(GameObject gameObject) {
//        obj.remove(gameObject);
//    }
}
