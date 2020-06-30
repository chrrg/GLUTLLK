package cn.edu.glut.llk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.view.inputmethod.InputMethodManager;

class LLKGame extends TimerTask implements  GameInit {//桂工连连看 源码
    private CHCanvasGame game;
    private GameCamera camera;
    @Override
    public void onSetGame(CHCanvasGame game) {
        this.game=game;
        camera=new GameCamera(game);//新建摄像机
        game.setCamera(camera);//设置2d摄像机
    }
    @Override
    public void onInit() {
        game.setBackGroundColor(Color.GRAY);//设置游戏背景为灰色
        game.setMaxFPS(12);//高帧率模式 设置最大帧率 测试最高60 0为不限制
        game.setGameObject(game.getGameObjectFromXML("1.xml"));


        game.getGameObject().appendChild(game.getGameObjectFromXML("2.xml"));
        game.getGameObject().appendChild(game.getGameObjectFromXML("3.xml"));
//        game.getGameObject().parentNode.removeChild(game.getGameObject());
//        game.getGameObject().getChildren().add(game.getGameObjectFromXML("4.xml"));
//        game.getGameObject().getChildren().get(0).getChildren().add(new GameObject())

        Logical util = new Logical(game);
        util.Init();//显示游戏开始页面



//        game.getGameObject().getElementById("input").onTouchStart(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("点击了！","2");
////                AlertDialog.Builder builder=;
//                game.getInput(new GameInput(){
//                    @Override
//                    public void finish(String inputText) {
//                        Log.e("输入了",inputText);
//                    }
//                });
//            }
//        });

//        game.getGameObject().getElementById("fps").onTouchStart(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("开始了FPS","fps started");
//            }
//        }).onTouchMove(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("移动了FPS","fps moved");
//            }
//        }).onTouchEnd(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("松开了FPS","fps ended");
//            }
//        }).onTouchEnter(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("进入了FPS","fps entered");
//            }
//        }).onTouchLeave(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("退出了FPS","fps leaved");
//            }
//        }).onClick(new OnTouchListener(){
//            @Override
//            public void onTouchEvent(MotionEvent event) {
//                Log.e("点击了FPS","fps clicked");
//            }
//        });
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
//        game.getGameObject().animate(true).run(1000, new AnimateCallback() {
//            @Override
//            public int beforeAnimate(Object ob) {
//                GameObject gameObject=(GameObject)ob;
//                Log.d("animate Start","ok");
//                return gameObject.getY();
//            }
//            @Override
//            public void callback(Object ob, int old, int time) {
//                GameObject gameObject=(GameObject)ob;
//                gameObject.setY(old+time/5);
//            }
//            @Override
//            public void afterAnimate(Object ob) {
//                GameObject gameObject=(GameObject)ob;
//                Log.d("animate Finish","ok");
//            }
//        }).next(1000, new AnimateCallback() {
//            @Override
//            public int beforeAnimate(Object ob) {
//                GameObject gameObject=(GameObject)ob;
//                Log.d("animate Start","ok");
//                return gameObject.getY();
//            }
//            @Override
//            public void callback(Object ob, int old, int time) {
//                GameObject gameObject=(GameObject)ob;
//                gameObject.setY(old-time/5);
//            }
//            @Override
//            public void afterAnimate(Object ob) {
//                GameObject gameObject=(GameObject)ob;
//                Log.d("animate Finish","ok");
//            }
//        });
        new Timer().schedule(this, 0, 5);
    }
    public static int getRandomColor(){
        Random random=new Random();
        int r=0;
        int g=0;
        int b=0;
        for(int i=0;i<2;i++){
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
        InputMethodManager imm = (InputMethodManager) game.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!imm.isActive())imm.showSoftInput(game.getContentView(),0);
//        Log.e("asdasd",""+imm.isActive());
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
    @Override
    public void onPause() {
        super.onPause();
        game.pause();
    }
    @Override
    public void onResume() {
        super.onResume();
        game.resume();
    }
}