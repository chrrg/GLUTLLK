package cn.edu.glut.llk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

interface GameObjectDraw{
    void onDraw(Canvas c,Paint paint);
}
interface GameInit{
    void onInit();
}
class GameObject{
    int x,y,w,h;
    private int backColor=0;
    private GameObjectDraw od=null;
    private Bitmap baseBitmap=null;
    private int pic=0;
    public void setPic(int pic){
        this.pic=pic;
    }
    public void clear(){
        baseBitmap=null;
    }
    void setBackColor(int backColor) {
        this.backColor=backColor;
    }
    void draw(Canvas c,Paint paint){
        if(baseBitmap==null) {
            baseBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(baseBitmap);
            if(backColor!=0){
                canvas.drawColor(backColor);
            }else if (this.od != null){
                this.od.onDraw(canvas, paint);
            }else return;
        }
        c.drawBitmap(baseBitmap,this.x,this.y,paint);
    }
    void set(int x, int y, int w, int h) {
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }
    void setDraw(GameObjectDraw od){
        this.od=od;
    }
}

class GameCamera{
    private int x;
    private int y;
    int cameraX;
    int cameraY;
    GameCamera(){

    }
    GameCamera(int x,int y){
        this.x=x;
        this.y=y;
    }
}
class CHCanvasGame {
    private Vector<GameObject> obj=new Vector<>();
    private GameCamera camera;
    private Canvas canvas;
    private Paint paint;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private int w,h;
    CHCanvasGame(){

    }
    int getWidth(){
        return this.w;
    }
    int getHeight(){
        return this.h;
    }
    void setCamera(GameCamera c){
        this.camera=c;
    }
    void addGameObject(GameObject o){
        obj.add(o);
    }
    private void drawOnce(Canvas c,Paint paint){
        for(GameObject ob:obj){
            ob.draw(c,paint);
        }
    }
    void init(Activity activity, int id, final GameInit init){
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        surfaceview = activity.findViewById(id);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            canvas = surfaceholder.lockCanvas();
                            if(canvas!=null) {
                                drawOnce(canvas, paint);
                                surfaceholder.unlockCanvasAndPost(canvas);
                            }
                            try {
                                Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
        });
        surfaceview.post(new Runnable() {
            @Override
            public void run() {
                w=surfaceview.getWidth();
                h=surfaceview.getHeight();
                init.onInit();
            }
        });
    }

}
