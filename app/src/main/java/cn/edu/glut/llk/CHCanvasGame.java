package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Vector;

interface GameObjectDraw{
    void onDraw(Canvas c,Paint paint);
}
interface GameInit{
    void onInit();
}
interface ObjectStringChange{
    String onListener();
}
class GameObject{
    int x,y,w,h;
    private int backColor=0;
    private GameObjectDraw od=null;
    private Bitmap baseBitmap=null;
    private String text=null;
    private ObjectStringChange textChange=null;
    private Paint selfPaint;
    private int pic=0;
    public void setPic(int pic){
        baseBitmap=null;
        this.pic=pic;
    }
    public void clear(){
        baseBitmap=null;
    }
    public void setPaint(Paint paint){
        selfPaint=paint;
    }
    void setString(String text){
        baseBitmap=null;
        this.text=text;
    }
    void setString(ObjectStringChange text){
        baseBitmap=null;
        textChange=text;
    }
    void setBackColor(int backColor) {
        baseBitmap=null;
        this.backColor=backColor;
    }
    void draw(Canvas c,Paint paint2,GameCamera camera){
        if(textChange !=null){
            String temp=textChange.onListener();
            if(text==null||!text.equals(temp)){
                text=temp;
                baseBitmap=null;
            }
        }
        Paint paint;
        if(selfPaint!=null)paint=selfPaint;else paint=paint2;
        if(baseBitmap==null) {
            baseBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(baseBitmap);
            if(backColor!=0){
                canvas.drawColor(backColor);
            }
            if (this.od != null){
                this.od.onDraw(canvas, paint);
            }
            if(text != null){
                canvas.drawText(text, 0,this.h,paint);
            }
        }
        c.drawBitmap(baseBitmap,this.x-camera.x,this.y-camera.y,paint);
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
    int x=0;
    int y=0;
    int cameraX;
    int cameraY;
    GameCamera(){

    }
    void setX(int x){
        this.x=x;
    }
    void setY(int y){
        this.y=y;
    }
    void moveX(int x){
        this.x+=x;
    }
    void moveY(int y){
        this.y+=y;
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
    private boolean isRunning;
    private int maxFps = 30;//设置帧率
    private long curfpsTime=0;
    private int fps=0;
    private int curfps=0;
    CHCanvasGame(){

    }
    void setMaxFPS(int fps){
        maxFps=fps;
    }
    int getFPS(){
        return fps;
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
            ob.draw(c,paint,camera);
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
                isRunning = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isRunning) {
                            long startMs = System.currentTimeMillis();
                            curfps++;
                            if(curfpsTime==0){
                                curfpsTime=System.currentTimeMillis();
                            }
                            if(startMs>curfpsTime+1000){
                                curfpsTime=startMs;
                                fps=curfps;
                                curfps=0;
                            }
                            try {
                                canvas = surfaceholder.lockCanvas();
                                if(canvas!=null) {
                                    drawOnce(canvas, paint);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (canvas != null) {
                                    surfaceholder.unlockCanvasAndPost(canvas);
                                }
                            }
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
//                                try {
//                                    Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }

                        }
                    }
                }).start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {isRunning=false;}
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
