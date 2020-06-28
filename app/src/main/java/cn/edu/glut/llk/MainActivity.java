package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.opengl.Matrix;

import javax.security.auth.callback.Callback;

class LLKGame extends TimerTask implements  GameInit {//桂工连连看 源码
    private CHCanvasGame game;
//    private GameObject box;
    private GameCamera camera;
//    private GameObject fps;
    @Override
    public void onSetGame(CHCanvasGame game) {
        this.game=game;
    }
    @Override
    public void onInit() {
        game.setBackGroundColor(Color.GRAY);//设置游戏背景为灰色
        camera=new GameCamera(game);//新建摄像机
        game.setCamera(camera);//设置2d摄像机
        game.setMaxFPS(0);//高帧率模式 设置最大帧率 测试最高60 0为不限制
        game.setGameObject(game.getGameObjectFromXML("1.xml"));
        game.getGameObject().getElementById("fps").onTouchStart(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("开始了FPS","fps started");
            }
        }).onTouchMove(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("移动了FPS","fps moved");
            }
        }).onTouchEnd(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("松开了FPS","fps ended");
            }
        }).onTouchEnter(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("进入了FPS","fps entered");
            }
        }).onTouchLeave(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("退出了FPS","fps leaved");
            }
        });

        //.getElementById("fps")
//        camera.animate().run(1000, new AnimateCallback() {
//            @Override
//            public int beforeAnimate(Object ob) {
//                return (int) ((GameCamera) ob).getValue(0);
//            }
//            @Override
//            public void callback(Object ob, int old, int time) {
//                ((GameCamera) ob).setValue(0,(float)time/1000+old);
//            }
//            @Override
//            public void afterAnimate(Object ob) {
//                Log.e("摄像头转动完成","camera finish");
//            }
//        });
        game.getGameObject().animate(true).run(1000, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Start","ok");
                return gameObject.getY();
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject gameObject=(GameObject)ob;
                gameObject.setY(old+time/5);
            }
            @Override
            public void afterAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Finish","ok");
            }
        }).next(1000, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Start","ok");
                return gameObject.getY();
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject gameObject=(GameObject)ob;
                gameObject.setY(old-time/5);
            }
            @Override
            public void afterAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Finish","ok");
            }
        });
//        game.getGameObject().getElementById("fps").animate().next(1000,100,new AnimateCallback(int value){
//
//        }).next(new Animation(1000,100));

//        Log.e("debug", String.valueOf(game.getGameObject().getStyle()));
//        float[] a = game.getGameObject().getModelMatrix();
//        game.getGameObject().setX(0);
//        game.getGameObject().setY(200);
//        game.getGameObject().setW(500);
//        game.getGameObject().setH(500);
//        game.getGameObject().onTouch(new OnTouchListener(){
//            @Override
//            public boolean onTouchStart(MotionEvent event) {
//                Log.e("点击","你点击了1!");
//                return false;
//            }
//            @Override
//            public boolean onTouchMove(MotionEvent event) {
//                return false;
//            }
//            @Override
//            public boolean onTouchEnd(MotionEvent event) {
//                return false;
//            }
//        });
//        game.getGameObject().getElementById("box2").onTouch(new OnTouchListener(){
//
//            @Override
//            public boolean onTouchStart(MotionEvent event) {
//                Log.e("点击","你点击了2!");
////                return true;
//                return false;
//            }
//
//            @Override
//            public boolean onTouchMove(MotionEvent event) {
//                return false;
//            }
//
//            @Override
//            public boolean onTouchEnd(MotionEvent event) {
//                return false;
//            }
//        });
        //移到500*500
//        float x=500,y=500,w=500,h=500;
//        float radio=(float)game.getWidth()/game.getHeight();
//        Matrix.scaleM(a,0,w/game.getHeight(),h/game.getHeight(),1);
//        Matrix.translateM(a,0,0,0,0f);//z越小越远
//        Matrix.translateM(a,0,-((float)game.getWidth()/game.getHeight()-2*x/game.getHeight()-w/game.getHeight())/(w/game.getHeight()),(1-2*y/game.getHeight()-h/game.getHeight())/(h/game.getHeight()),0f);
//        Matrix.translateM(a,0,1-game.getWidth()/w,game.getHeight()/h-1,0f);
//        Matrix.translateM(a,0,1-(game.getWidth()-2*x)/w,(game.getHeight()-2*y)/h-1,0f);
//        Matrix.translateM(a,0,-(((float)game.getWidth()/game.getHeight())-w/game.getHeight())/(w/game.getHeight()),(1-h/game.getHeight())/(h/game.getHeight()),0f);//z越小越远
//        Matrix.scaleM(a,0,0.5f,0.5f,1);
//        Matrix.translateM(a,0,0f,2f,0f);//z越小越远
//        game.getHeight()
//        game.getWidth()
//        Matrix.translateM(a,0,0f,0f,0f);//z越小越远

//        game.getGameObject().getElementById("root").getPaint().setTextSize(80);
//        game.getGameObject().getElementById("root").setGif(game.getGif("1.gif"));
//        GameObject backGround = new GameObject();//新建一个背景
//        backGround.set(0, 0, game.getWidth(), game.getHeight());//相等于屏幕的宽高
//        backGround.setBackColor(Color.WHITE);//白色
//        game.addGameObject(backGround);//将这个新建的对象放入游戏中
//        //----------------------------------
//        fps = new GameObject();//新建一个背景
//        fps.setIndex(101);//层级设置更高 默认100
//        fps.set(0,200,game.getWidth(),80);
//        fps.setBackColor(Color.GREEN);
//        Paint fpsPaint=new Paint();
//        fpsPaint.setTextSize(80);
//        fps.setPaint(fpsPaint);
//        fps.setString(new ObjectStringChange(){
//            @Override
//            public String onListener(){
//                return "FPS: "+game.getFPS()+" Time:"+(float)game.getTime()/1000;
//            }
//        });
//        fps.onTouch(new OnTouchListener(){
//            @Override
//            public boolean onTouchStart(MotionEvent event) {
//                Log.i("onTouchStartfps","2");
//                return false;
//            }
//            @Override
//            public boolean onTouchMove(MotionEvent event) {
//                Log.i("onTouchMovefps","2");
//                box.y += 10;
//                camera.moveY(10);
//                return true;
//            }
//            @Override
//            public boolean onTouchEnd(MotionEvent event) {
//                Log.i("onTouchEndfps","2");
//                camera.setCameraY(-500);
//                return false;
//            }
//        });
//        game.addGameObject(fps);
//        //---------------------------------------
//        box = new GameObject();//新建一个游戏内对象
//        box.setBackColor(Color.YELLOW);
//        box.set(50, 50, 100, 100);//设置对象X
//        box.setDraw(new GameObjectDraw() {
//            @Override
//            public void onDraw(Canvas c, Paint paint) {
//                c.drawCircle(20, 20, 15, paint);
//            }
//        });
//        game.addGameObject(box);
//        //----------------------------------------
//        GameObject box2 = new GameObject();//新建一个游戏内对象
//        box2.set(110, 100, 500, 500);//设置对象X
////        box2.setBackColor(Color.BLUE);
//        box2.setPic(game.getImage("1.png"));
//        box2.onTouch(new OnTouchListener(){
//            @Override
//            public boolean onTouchStart(MotionEvent event) {
//                Log.i("touchbox2","1");
//                return false;
//            }
//            @Override
//            public boolean onTouchMove(MotionEvent event) {
//                Log.i("onTouchMovebox2","1");
//                return false;
//            }
//            @Override
//            public boolean onTouchEnd(MotionEvent event) {
//                Log.i("onTouchEndbox2","1");
//                return false;
//            }
//        });
//        box2.setGif(game.getGif("1.gif"));
//        game.addGameObject(box2);
        new Timer().schedule(this, 0, 5);
    }
    public static int getRandomColor(){
        Random random=new Random();
        int r=0;
        int g=0;
        int b=0;
        for(int i=0;i<2;i++){
            //       result=result*10+random.nextInt(10);
            int temp=random.nextInt(16);
            r=r*16+temp;
            temp=random.nextInt(16);
            g=g*16+temp;
            temp=random.nextInt(16);
            b=b*16+temp;
        }
        return Color.rgb(r,g,b);
    }
    @Override
    public void run() {//定时器 对象向下移动
        game.getGameObject().getElementById("fps").setText("Time: "+game.getTime()+" FPS: "+game.getFPS());

//        game.getGameObject().updateView();
//        game.getGameObject().setW((int) (Math.random()*game.getWidth()));
//        game.getGameObject().setW(500);
//        Matrix.scaleM(game.getGameObject().getModelMatrix(),0,1f,1f,0.00001f);
//        float[] translate = new float[16];
//        Matrix.setIdentityM(translate,0);
//        Matrix.translateM(translate,0,0.1f,0f,0f);//z越小越远
//        Matrix.multiplyMM(game.getGameObject().getModelMatrix(), 0, translate, 0, game.getGameObject().getModelMatrix(), 0);
//        Matrix.translateM(game.getGameObject().getModelMatrix(),0,0f,0.01f,0f);//z越小越远
//        game.getGameObject().getModelMatrix()[]-=0.01;
//        Matrix.translateM(game.getGameObject().getModelMatrix(),0,0.005f,0.001f,-0.01f);//z越小越远
//        Log.e("getModelMatrix", Arrays.toString(game.getGameObject().getModelMatrix()));
//        Matrix.translateM(game.getGameObject().getModelMatrix(),0,0f,0f,0.001f);//z越小越远
//        Matrix.rotateM(game.getGameObject().getModelMatrix(),0,1f,0,1,0);
//        game.getGameObject().getElementById("root").setBackColor(getRandomColor());
//        if(game.getTime()%2<1)
//            game.getGameObject().getElementById("root").setBackColor(Color.BLUE);
//        else
//            game.getGameObject().getElementById("root").setBackColor(Color.YELLOW);
//        game.getGameObject().getElementById("box1").setBackColor(Color.YELLOW);
//        Log.e("run","run");
//        box.y += 1;
//        fps.y= (int) (box.y+game.getTime()/10);
//        if(fps.y+fps.h>game.getHeight())fps.y=game.getHeight()-fps.h;
//        if(fps.y<camera.getY())fps.y=box.y-500;
//        camera.setCameraY(box.y-500);
//        if(game.getTime()>2000)fps.setDisplay(false);
    }
}
public class MainActivity extends Activity {
    CHCanvasGame game=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){Window window = getWindow();window.setStatusBarColor(Color.WHITE);}
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if(game==null) game = new CHCanvasGame(this, new LLKGame());//初始化游戏引擎
        setContentView(game.getContentView());
    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        game.pause();
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        game.resume();
//    }
}