package cn.edu.glut.llk;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
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
    // 原始的矩形区域的顶点坐标
//    private static final float CUBE[] = {
//            -1.0f, -1.0f,0.0f, // v1
//            1.0f, -1.0f, 0.0f, // v2
//            -1.0f, 1.0f,0.0f,  // v3
//            1.0f, 1.0f,  0.0f, // v4
//    };
    // 纹理坐标，每个坐标的纹理采样对应上面顶点坐标。
    // 纹理为0 ~ 2，会有四分屏
//    private static final float TEXTURE_NO_ROTATION[] = {
//            0.0f, 1.0f, // v1
//            1.0f, 1.0f, // v2
//            0.0f, 0.0f, // v3
//            1.0f, 0.0f, // v4
//    };
    //设置颜色
//    private static final float COLORS[] = {
//            0.0f, 1.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 0.0f, 1.0f,
//            0.0f, 0.0f, 1.0f, 1.0f
//    };
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);//根据type创建顶点着色器或者片元着色器
        GLES20.glShaderSource(shader, shaderCode);//将资源加入到着色器中，并编译
        GLES20.glCompileShader(shader);
        return shader;
    }
    // 初始化buffer
//    private static FloatBuffer initBuffer(float[] buffers) {
//        ByteBuffer mbb = ByteBuffer.allocateDirect(buffers.length * 4);// 先初始化buffer,数组的长度*4,因为一个float占4个字节
//        mbb.order(ByteOrder.nativeOrder());// 数组排列用nativeOrder
//        FloatBuffer floatBuffer = mbb.asFloatBuffer();
//        floatBuffer.put(buffers);
//        floatBuffer.flip();
//        return floatBuffer;
//    }
    private int mProgram;
//    private FloatBuffer mCubeBuffer;
//    private FloatBuffer mTextureBuffer;
//    private FloatBuffer mColorBuffer;
//    private FloatBuffer mMatrixBuffer;
// 加载Buffer
//    private void loadBuffer() {
//        mCubeBuffer = initBuffer(CUBE);
//        mTextureBuffer = initBuffer(TEXTURE_NO_ROTATION);
//        mColorBuffer = initBuffer(COLORS);
//        mMatrixBuffer = initBuffer(mMVPMatrix);
//    }
    private int mPositionHandle;
    private int mTextureHandle;
    private int mMatrixHandle;
    private int mGLUniformTexture;
//    int mMvpMatrixHandle;

//    int mGLTextureId;  // 纹理ID
//    private float[] mViewMatrix = new float[16];
//    private float[] mProjectMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mCameraMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private Bitmap b;
//    ByteBuffer mBuf;
    private FloatBuffer textureBuffer;
    private FloatBuffer vertexBuffer;
    int tt1,tt2;
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
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {//创建时
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, game.getAsset("OpenGL/1.vert"));//加载顶点着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, game.getAsset("OpenGL/1.frag"));//加载片元着色器
        mProgram=assembleProgram(vertexShader,fragmentShader);
        Log.i("Program:", String.valueOf(mProgram));
        GLES20.glEnable(GLES20.GL_BLEND);GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);//开透明
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "mPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "mTexture");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        b=game.getImage("1.gif");
        Bitmap b2=game.getImage("1.jpg");
        tt1=genImage(b);
        tt2=genImage(b2);

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
//                surfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, game.getAsset("OpenGL/1.vert"));//加载顶点着色器
//        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, game.getAsset("OpenGL/1.frag"));//加载片元着色器
//        mProgram=assembleProgram(vertexShader,fragmentShader);
//        GLES20.glUseProgram(mProgram);//必须 使用
//
//        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "mPosition");
////        mColorHandle = GLES20.glGetAttribLocation(mProgram, "mColor");
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "v_texCoord");
////        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "transform");
////        mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
//        mGLTextureId = -1;
//        int[] textures = new int[1];
//        // 加载纹理
//        GLES20.glGenTextures(1, textures, 0);
//        GLES20.glBindTexture(GL_TEXTURE_2D, textures[0]);
//        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        //纹理也有坐标系，称UV坐标，或者ST坐标
//        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//        GLES20.glTexParameterf(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
////        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
//        b = game.getImage("1.png");
//        mBuf = ByteBuffer.allocate(b.getWidth()*b.getHeight()*4);
//        b.copyPixelsToBuffer(mBuf);
//        GLES20.glTexImage2D(GL_TEXTURE_2D,0,GLES20.GL_RGBA,b.getWidth(),b.getHeight(),0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE, null);
//                GLES20.glUniform1i(mGLUniformTexture, 1); // 设置第一层纹理
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0+1);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId);

//        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
//            Log.e("TAG", "glCheckFramebufferStatus error");
//        } else {
//            mGLTextureId = textures[0];
////            GLES20.glUniform1i(mGLUniformTexture, 1); // 设置第一层纹理
//        }
//                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0,0,0,b);

//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);// S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
//                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);// T轴的拉伸方式为重复
//                GLES20.glGenTextures(1,);
    }
    //定义一个16x16的透视矩阵

    //视图矩阵
//    private final float[] mVMatrix= new float[16];
    //透视矩阵与视图矩阵变换后的总矩阵
//    private final float[] mMVPMatrix= new float[16];
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {//改变时
        GLES20.glViewport(0, 0, width, height); // 设置窗口大小
        Log.e("TAG", "onSurfaceChanged: width=" + width + " height=" + height);
//        Matrix.perspectiveM(mProjectionMatrix,0,45,(float)width/(float)height,1f,10f);
        //相对于屏幕坐标系将摄像头固定在（0，0，-5）方向，看向屏幕正中点（0，0，0），以屏幕向上为正方向（0，1，0）
//        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//        int w = b.getWidth();
//        int h = b.getHeight();
//        float sWH = w / (float) h;
//        float sWidthHeight = width / (float) height;
//        if (width > height) {
//            if (sWH > sWidthHeight) {
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 7);
//            } else {
//                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 7);
//            }
//        } else {
//            if (sWH > sWidthHeight) {
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
//            } else {
//                Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
//            }
//        }
//        //设置相机位置
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        //计算变换矩阵
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
//        loadBuffer();
    }
//    public FloatBuffer setupTriangle(float[] verticesPosition) {
//        short[] indices = new short[]{0, 1, 2, 0, 2, 3}; // The order of vertexrendering.
//
//        // The vertex buffer.
//        ByteBuffer bb = ByteBuffer.allocateDirect(verticesPosition.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        FloatBuffer vertexBuffer = bb.asFloatBuffer();
//        vertexBuffer.put(verticesPosition);
//        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
//        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
//        dlb.order(ByteOrder.nativeOrder());
//        drawListBuffer = dlb.asShortBuffer();
//        drawListBuffer.put(indices);
//        drawListBuffer.position(0);
//        return vertexBuffer;
//    }
    private final  float[] vertexData={
            -1f,-1f,
            1f,-1f,
            -1f,1f,
            1f,1f
    };
    private final float[] textureData={
            0f,1f,
            1f,1f,
            0f,0f,
            0f,1f
    };
    private int genImage(Bitmap bmp) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        //纹理也有坐标系，称UV坐标，或者ST坐标
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        return textures[0];
    }
    void draw(int id){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,id);
        //顶点坐标
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //纹理坐标
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);

    }
//    public FloatBuffer setupImage(Bitmap bmp) {
//        float[] uvs = new float[]{
//            0.0f, 0.0f, 0.0f, 1.0f,
//            1.0f, 1.0f, 1.0f, 0.0f
//        };
//        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
//        bb.order(ByteOrder.nativeOrder());
//        FloatBuffer uvBuffer = bb.asFloatBuffer();
//        uvBuffer.put(uvs);
//        uvBuffer.position(0);
//        int[] texturenames = new int[1];
//        GLES20.glGenTextures(1, texturenames, 0);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glEnable(GLES20.GL_BLEND);
//        return uvBuffer;
//    }

//    private void drawBitmap(float[] m, int mPositionHandle, int mTexCoordLoc, FloatBuffer vertexBufferPosition, FloatBuffer uvBuffer1, short[] shortsIndices) {
//        // Enable generic vertex attribute array
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,0, vertexBufferPosition);
//        // Get handle to texture coordinates location
//        // Enable generic vertex attribute array
//        GLES20.glEnableVertexAttribArray(mTextureHandle);
//        // Prepare the texturecoordinates
//        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT,
//                false,
//                0, uvBuffer1);
//        // Get handle to shape's transformation matrix
//        int mtrxhandle1 = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");
//        // Apply the projection and view transformation
//        GLES20.glUniformMatrix4fv(mtrxhandle1, 1, false, m, 0);
//        // Get handle to textures locations
//        int mSamplerLoc1 = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "s_texture");
//        // Set the sampler texture unit to 0, where we have saved the texture.
//        GLES20.glUniform1i(mSamplerLoc1, 0);
//        // Draw the triangle
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, shortsIndices.length,
//                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
//        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(mPositionHandle);
//        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
//    }
    private void doDraw(){

        Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mCameraMatrix, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//清屏缓冲区
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);//利用颜色清屏

        GLES20.glUseProgram(mProgram);

//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,12, verticalsBuffer);
//        if(Math.random()<0.5)draw(tt2);
//        if(Math.random()<0.5)draw(tt1);
        draw(tt2);draw(tt1);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mGLUniformTexture, 0);
//        GLES20.glDisableVertexAttribArray(mPositionHandle);

//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//清空画布
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        // 顶点
//        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mCubeBuffer);
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 顶点着色器的纹理坐标
//        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 8, mTextureBuffer);
//        GLES20.glEnableVertexAttribArray(mTextureHandle);
        // 传入的图片纹理
//        if (mGLTextureId != -1) {
//                    Log.e("TAG", "onDrawFrame: mGLTextureId=" + mGLTextureId);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
//            GLES20.glBindTexture(GL_TEXTURE_2D, mGLTextureId);
//            GLES20.glTexSubImage2D();
//            Log.i("allocate", String.valueOf(b.getWidth()*b.getHeight()*4));

//            GLES11Ext.glEGLImageTargetTexture2DOES(GL_TEXTURE_2D,mBuf);
//            GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0,0, b);
//        }
        // 变换矩阵
//        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMatrixBuffer);
        //获取片元着色器的vColor成员的句柄

        //设置绘制三角形的颜色
//        GLES20.glEnableVertexAttribArray(mColorHandle);
//        GLES20.glVertexAttribPointer(mColorHandle, 4,
//                GLES20.GL_FLOAT, false,
//                4, mColorBuffer);
        // 绘制顶点 ，方式有顶点法和索引法
        // GLES20.GL_TRIANGLE_STRIP即每相邻三个顶点组成一个三角形，为一系列相接三角形构成
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4); // 顶点法，按照传入渲染管线的顶点顺序及采用的绘制方式将顶点组成图元进行绘制
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
    }
    @Override
    public void onDrawFrame(GL10 gl10){
        long startMs = System.currentTimeMillis();
        if(startMs>curfpsTime+1000){
            Log.e("当前FPS:", String.valueOf(curfps));
            curfpsTime=startMs;fps=curfps;curfps=0;
        }
        doDraw();
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