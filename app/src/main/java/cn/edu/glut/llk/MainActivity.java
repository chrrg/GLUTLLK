package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import java.util.Timer;
import java.util.TimerTask;

class LLKGame extends TimerTask implements  GameInit {//桂工连连看 源码
    private CHCanvasGame game;
    private GameObject box;
    private GameCamera camera;
    private GameObject fps;
    LLKGame(CHCanvasGame game){
        this.game=game;
    }
    @Override
    public void onInit() {
        game.setBackGroundColor(Color.BLACK);
        camera=new GameCamera(-100,200);
        game.setCamera(camera);//设置2d摄像机
        game.setMaxFPS(50);//高帧率模式 设置最大帧率 测试最高60
        GameObject backGround = new GameObject();//新建一个背景
        backGround.set(0, 0, game.getWidth(), game.getHeight());
        backGround.setBackColor(Color.WHITE);
        game.addGameObject(backGround);
//
        fps = new GameObject();//新建一个背景
        fps.set(0,200,game.getWidth(),80);
        fps.setBackColor(Color.GREEN);
        Paint fpsPaint=new Paint(                                                                                                                                                                                                                                                                                                                                                                                                                                                           );
        fpsPaint.setTextSize(80);
        fps.setPaint(fpsPaint);
        fps.setString(new ObjectStringChange(){
            @Override
            public String onListener() {
                return "FPS: "+game.getFPS()+" Time:"+(float)game.getTime()/1000;
            }
        });
        game.addGameObject(fps);
        box = new GameObject();//新建一个游戏内对象
        box.set(50, 50, 100, 100);//设置对象X
        box.setDraw(new GameObjectDraw() {
            @Override
            public void onDraw(Canvas c, Paint paint) {
                c.drawCircle(20, 20, 15, paint);
            }
        });
        game.addGameObject(box);
        GameObject box2 = new GameObject();//新建一个游戏内对象
        box2.set(110, 100, 150, 100);//设置对象X
        game.addGameObject(box2);
        new Timer().schedule(this, 0, 5);
    }
    @Override
    public void run() {//定时器 对象向下移动
        box.y += 1;
        fps.y= (int) (box.y+game.getTime()/10);
        if(fps.y+fps.h>game.getHeight())fps.y=game.getHeight()-fps.h;
        camera.moveCameraY(box.y-500);
    }
}
public class MainActivity extends Activity {
    CHCanvasGame game=null;
    GameCamera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){Window window = getWindow();window.setStatusBarColor(Color.WHITE);}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        if(game==null) {
            game = new CHCanvasGame();//初始化游戏引擎
            game.init(this, R.id.canvas, new LLKGame(game));//设置参数
        }
    }
}