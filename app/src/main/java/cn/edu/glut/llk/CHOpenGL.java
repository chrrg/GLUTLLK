package cn.edu.glut.llk;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class CHOpenGL implements GLSurfaceView.Renderer{
    private int maxFps = 50;//设置帧率
    private long curfpsTime=0;
    private int fps=0;
    private int curfps=0;
    private CHCanvasGame game=null;
    CHOpenGL(CHCanvasGame game){
        this.game=game;
    }
    void setMaxFps(int maxFps){
        this.maxFps=maxFps;
    }
    int getMaxFps(){
        return maxFps;
    }
    int getFps() {
        return fps;
    }
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);//根据type创建顶点着色器或者片元着色器
        if (shader == 0)throw new RuntimeException("Error create shader.");
        GLES20.glShaderSource(shader, shaderCode);//将资源加入到着色器中，并编译
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compile shader: " + GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }
    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mMatrixHandle;
    private int mUniformTextureHandle;
//    private float[] mModelMatrix = new float[16];// 具体物体的3D变换矩阵，包含旋转、平移、缩放
    private float[] mViewMatrix = new float[16];//摄像机位置朝向9參数矩阵
    private float[] mProjectionMatrix = new float[16];// 4x4矩阵 投影用
    private float[] mMVPMatrix = new float[16];// 最后起作用的总变换矩阵
    private FloatBuffer textureBuffer;
    private FloatBuffer vertexBuffer;
//    private int tt1,tt2;
    private final  float[] vertexData={
//        -1f,-1f,0f,// bottom left
//        1f,-1f,0f,// bottom right
//        -1f,1f,0f,// top left
//        1f,1f,0f, // top right
        -1.0f,1.0f,0f,    //左上角
        -1.0f,-1.0f,0f,   //左下角
        1.0f,1.0f,0f,     //右上角
        1.0f,-1.0f,0f     //右下角
    };
//    private final  float[] vertexData={
//        0.0f,  0.5f,  0.0f,
//        0.0f,  0.0f,  0.5f,
//        0.5f,  0.0f,  0.0f,
//        -0.25f, 0.0f, -0.25f,
//        0.0f,  0.5f,  0.0f,
//        0.0f,  0.0f,  0.5f
//    };
    private final float[] textureData={
//        0f,1f,0f,// bottom left
//        1f,1f,0f,// bottom right
//        0f,0f,0f,// top left
//        0f,1f,0f, // top right
        0.0f,0.0f,0f,
        0.0f,1.0f,0f,
        1.0f,0.0f,0f,
        1.0f,1.0f,0f,
    };
    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexCount = vertexData.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;// 4 bytes per vertex
//    private short[] quadIndex = new short[] {
//        (short)(0), // Position 0
//        (short)(1), // Position 1
//        (short)(2), // Position 2
//        (short)(2), // Position 2
//        (short)(3), // Position 3
//        (short)(0), // Position 0
//    };
//    private ShortBuffer index;
    private int assembleProgram(int vertexShader, int fragmentShader) {
        int program = GLES20.glCreateProgram();
        if (program == 0) throw new RuntimeException("Cannot create GL program: " + GLES20.glGetError());
        GLES20.glAttachShader(program, vertexShader);//将顶点着色器加入到程序
        GLES20.glAttachShader(program, fragmentShader);//将片元着色器加入到程序中
        GLES20.glLinkProgram(program);//连接到着色器程序
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
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
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {//创建时
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, game.getAsset("OpenGL/1.vert"));//加载顶点着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, game.getAsset("OpenGL/1.frag"));//加载片元着色器
        mProgram=assembleProgram(vertexShader,fragmentShader);
        GLES20.glUseProgram(mProgram);
        Log.i("Program:", String.valueOf(mProgram));
        GLES20.glEnable(GLES20.GL_BLEND);GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);//开透明
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "mPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "mTexture");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//        mUniformTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");

//        GLES20.glEnable(GLES20.GL_CULL_FACE);//背面看不到
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);//启用深度测试    
        GLES20.glClearDepthf(1.0f);//设置深度缓存    
//        GLES20.glDepthFunc(GLES20.GL_LEQUAL);//所做深度测试的类型  
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);//   
//        GLES20.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glDisable(GL10.GL_CULL_FACE);
//        GLES20.glEnable(GL10.GL_CCW);
//        GLES20.glDepthMask(true);
//        GLES20.glEnable(GL10.GL_ALPHA_TEST);
//        Bitmap b = game.getImage("1.gif");
//        Bitmap b2=game.getImage("1.png");
//        Log.e("IMAGE WH:",b2.getWidth()+"|"+b2.getHeight());
//        tt1=newTexture(b);
//        tt2=newTexture(b2);
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
//        vertexBuffer.flip();
        vertexBuffer.position(0);
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(textureData);
//        textureBuffer.flip();
        textureBuffer.position(0);
    }
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {//改变时
        GLES20.glViewport(0, 0, width, height); // 设置窗口大小
        Log.e("TAG", "onSurfaceChanged: width=" + width + " height=" + height);
        float ratio = (float) width / height;
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//        Matrix.perspectiveM(mProjectionMatrix, 0, 45, ratio, 1, 10);
//        Matrix.setLookAtM(mViewMatrix, 0, 0f,0f, 10.8f, 0f, 0f, 0f, 0f, 1f, 0f);
//        Matrix.setIdentityM(mModelMatrix,0);//把矩阵设为单位矩阵
//        Matrix.setIdentityM(mViewMatrix,0);//把矩阵设为单位矩阵
//        Matrix.translateM(mViewMatrix,0,0f,0f,-2.5f);//把这个单位矩阵沿着z轴平移-2.5
//        Matrix.rotateM(mViewMatrix,0,-60f,1f,0f,0f);//把矩形绕x轴旋转60°
//        Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -10, 1, 0.1f, 100);//正交投影
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio*0.4f, ratio*0.4f, -1*0.4f, 1*0.4f, 1, 50);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45, ratio, 0.1f, 10f);
//        Matrix.setIdentityM(mProjectionMatrix,0);//把矩阵设为单位矩阵
        Matrix.setLookAtM(mViewMatrix, 0,0, 0, 5, 0f, 0f, 0f, 0f, 1f, 0f);
        //float cx, //摄像机位置x
        //float cy, //摄像机位置y
        //float cz, //摄像机位置z
        //float tx, //摄像机目标点x
        //float ty, //摄像机目标点y
        //float tz, //摄像机目标点z
        //float upx, //摄像机UP向量X分量
        //float upy, //摄像机UP向量Y分量
        //float upz //摄像机UP向量Z分量
    }
    void updateTexture(int id,Bitmap bmp){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0,0,bmp);
    }
    int newTexture(Bitmap bmp) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        return textures[0];
    }
    private void draw(int id){//画一个
        GLES20.glUseProgram(mProgram);
//        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,id);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glVertexAttribPointer(mTextureHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 8, GLES20.GL_UNSIGNED_SHORT, index);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }
    void drawObj(float[] mModelMatrix,int id){
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0, mMVPMatrix, 0, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, result, 0);
        draw(id);
    }
    private void doDraw(GameObject gameObject){//绘制

        GLES20.glUseProgram(mProgram);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        if(gameObject==null)return;

        gameObject.draw();
//        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);//利用颜色清屏
//        mMVPMatrix = new float[16];
//        Matrix.setRotateM(mModelMatrix, 0, 0, 0, 1, 0);// 初始化变换矩阵, 第二參数是矩阵起始位, 第三參数 旋转的角度, 四五六參数 旋转的轴
//        Matrix.rotateM(mModelMatrix, 0, 1, 0,1,0);// 设置绕y轴旋转
//        Matrix.translateM(mViewMatrix,0,0f,0f,0.01f);//把这个单位矩阵沿着z轴平移-2.5
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelMatrix, 0);
//        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix,0, mViewMatrix, 0);

//        Matrix.translateM(mViewMatrix,0,0f,0f,0.004f);
//        if(Math.random()<0.5)Matrix.translateM(mViewMatrix,0,0f,0f,-0.01f);
//        float[] mModelMatrix2 = new float[16];
//        System.arraycopy(mModelMatrix, 0, mModelMatrix2, 0, 16);
//        float[] result = new float[16];



//        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, result, 0);
//        GLES20.glUniform1i(mUniformTextureHandle, 0);
//        GLES20.glBindAttribLocation(mProgram, 0, "mPosition");
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        Log.i("tt2", tt2 +"|"+tt1);
//        draw(tt2);


//        Matrix.setIdentityM(mModelMatrix2,0);//把矩阵设为单位矩阵
//        Matrix.translateM(mModelMatrix2,0,0f,0f,0.1f);
//        Matrix.rotateM(mModelMatrix2, 0, 89, 0,1,0);// 设置绕y轴旋转
//        Matrix.multiplyMM(result, 0, mMVPMatrix, 0, mModelMatrix2 , 0);
//        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, result, 0);
//        draw(tt1);
    }
    @Override
    public void onDrawFrame(GL10 gl10){
        long startMs = System.currentTimeMillis();
        if(startMs>curfpsTime+1000){
            Log.e("当前FPS:", String.valueOf(curfps));
            curfpsTime=startMs;fps=curfps;curfps=0;
        }
        doDraw(game.getGameObject());
        curfps++;
        long time = 1000 / maxFps  + startMs - System.currentTimeMillis();
        if (time>0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}