package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

class LLKGame extends TimerTask implements  GameInit {//桂工连连看 源码
    private CHCanvasGame game;
    private GameObject box;
    private GameCamera camera;
    private GameObject fps;
    @Override
    public void onSetGame(CHCanvasGame game) {
        this.game=game;
    }
    @Override
    public void onInit() {
        game.setBackGroundColor(Color.GRAY);//设置游戏背景为灰色
        camera=new GameCamera(game,-200,200);//新建摄像机
        game.setCamera(camera);//设置2d摄像机
        game.setMaxFPS(90);//高帧率模式 设置最大帧率 测试最高60
        game.setGameObject(game.getGameObjectFromXML("1.xml"));
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
//        new Timer().schedule(this, 0, 5);
    }
    @Override
    public void run() {//定时器 对象向下移动
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
        if(game==null) game = new CHCanvasGame(this, R.id.canvas, new LLKGame());//初始化游戏引擎
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