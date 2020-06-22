package cn.edu.glut.llk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

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
class GameObject{
    int x,y,w,h;
    private int backColor=0;
    private GameObjectDraw od=null;
    private Bitmap baseBitmap=null;
    private String text=null;
    private ObjectStringChange textChange=null;
    private Paint selfPaint;
    private int index=100;
    private Bitmap pic=null;
    private Movie gif;
    private long gifStart;
    void setPic(Bitmap pic){
        baseBitmap=null;
        this.pic=pic;
    }
    void setGif(Movie gif){
        baseBitmap=null;
        gifStart=android.os.SystemClock.uptimeMillis();
        this.gif=gif;
    }
    public void setIndex(int i){
        index=i;
    }
    int getIndex(){
        return index;
    }
    public void clear(){
        baseBitmap=null;
    }
    void setPaint(Paint paint){
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
    void removeFromGame(CHCanvasGame game){
        game.remove(this);
    }
    void draw(Canvas c,Paint paint2,GameCamera camera){
        if(textChange !=null){
            String temp=textChange.onListener();
            if(text==null||!text.equals(temp)){
                text=temp;
                baseBitmap=null;
            }
        }
        if(gif !=null){
            baseBitmap=null;
        }
        Paint paint;
        if(selfPaint!=null)paint=selfPaint;else paint=paint2;
        if(baseBitmap==null) {
            baseBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(baseBitmap);
            if(backColor!=0){
                canvas.drawColor(backColor);
            }
            if (pic != null){
                pic=Bitmap.createScaledBitmap(pic, this.w, this.h, true);
                canvas.drawBitmap(pic,0,0,paint);
            }
            if (gif != null){
                gif.setTime((int) ((android.os.SystemClock.uptimeMillis() - gifStart) % gif.duration()));
                gif.draw(canvas,0,0);
//                this.pic=Bitmap.createScaledBitmap(this.pic, this.w, this.h, true);
//                canvas.drawBitmap(this.pic,0,0,paint);
            }
            if (this.od != null){
                this.od.onDraw(canvas, paint);
            }
            if(text != null){
                canvas.drawText(text, 0,this.h,paint);
            }
        }
        c.drawBitmap(baseBitmap,this.x-camera.getX(),this.y-camera.getY(),paint);
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
    private int x=0;
    private int y=0;
    private int cameraX;
    private int cameraY;
    void setX(int x){
        this.x=x;
        cameraX=x;
    }
    void setY(int y){
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
        cameraX=x;
    }
    void moveY(int y){
        this.y+=y;
        cameraY=y;
    }
    void setCameraX(int x){
        cameraX=x;
    }
    void setCameraY(int y){
        cameraY=y;
    }
    void fixCamera(){//平滑相机
        x=(int)(x+(cameraX-x)*0.15);
        y=(int)(y+(cameraY-y)*0.15);
    }
    GameCamera(int x,int y){
        setX(x);
        setY(y);
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
    private int maxFps = 50;//设置帧率
    private long curfpsTime=0;
    private int fps=0;
    private int curfps=0;
    private int backGroundColor=0;
    private long startTime=System.currentTimeMillis();
    private Activity activity;
    long getTime(){
        return System.currentTimeMillis()-startTime;
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
        camera.fixCamera();
        Collections.sort(obj, new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1 != null && o2 != null)return Integer.compare(o1.getIndex(), o2.getIndex());
                return 0;
            }
        });
        for(GameObject ob:obj){
            ob.draw(c,paint,camera);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    CHCanvasGame init(Activity activity, int id, final GameInit init){
        init.onSetGame(this);
        this.activity=activity;
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        surfaceview = activity.findViewById(id);
        surfaceview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
//                Log.e("action", String.valueOf(event.getAction())+"|"+x+"|"+y);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//ACTION_MOVE ACTION_UP

                }
                return true;
            }
        });
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
                                if(backGroundColor!=0)canvas.drawColor(backGroundColor);
                                if(canvas!=null&&camera!=null) {
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
        return this;
    }
    void remove(GameObject gameObject) {
        obj.remove(gameObject);
    }
}
