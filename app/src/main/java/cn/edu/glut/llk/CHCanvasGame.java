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
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private Vector<GameObject> children;
    OnTouchListener touch;
    private boolean display=true;
    void onTouch(OnTouchListener touch){
        this.touch=touch;
    }
    void setDisplay(boolean display){
        this.display=display;
    }
    boolean isIn(int touchX,int touchY){
        return touchX>x&&touchX<x+w&&touchY>y&&touchY<y+h;
    }
    void setPic(Bitmap pic){
        baseBitmap=null;
        this.pic=pic;
    }
    void setGif(Movie gif){
        baseBitmap=null;
        gifStart=android.os.SystemClock.uptimeMillis();
        this.gif=gif;
    }
    void setIndex(int i){
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
        if(!display)return;//不显示的不渲染
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
                Bitmap tempBitmap = Bitmap.createBitmap(gif.width(),gif.height(), Bitmap.Config.ARGB_8888);
                Canvas temp=new Canvas(tempBitmap);
                int duration=gif.duration();
                if(duration==0)duration=1000;
                gif.setTime((int) ((android.os.SystemClock.uptimeMillis() - gifStart) % duration));
                gif.draw(temp,0,0);
                tempBitmap=Bitmap.createScaledBitmap(tempBitmap, this.w, this.h, true);
                canvas.drawBitmap(tempBitmap,0,0,paint);
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
    private int x=0;//当前摄像机位置
    private int y=0;
    private int cameraX;//平滑摄像机预计到达的位置
    private int cameraY;
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
    void setLayout(String filename){
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        try {
            AssetManager am=activity.getAssets();
            DocumentBuilder db = xml.newDocumentBuilder();
            Document doc = db.parse(am.open(filename));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sort(){
        Collections.sort(obj, new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1 != null && o2 != null)return Integer.compare(o1.getIndex(), o2.getIndex());
                return 0;
            }
        });
    }
    private void drawOnce(Canvas c,Paint paint){
        camera.fixCamera();
        if(backGroundColor!=0)canvas.drawColor(backGroundColor);
        sort();
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
        paint.setAntiAlias(true);
        paint.setDither(true);
        surfaceview = activity.findViewById(id);
        final ArrayList<GameObject> kv = new ArrayList<>();
        surfaceview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int)event.getX()+camera.getX();
                int y = (int)event.getY()+camera.getY();
                boolean result=false;
//                Log.i("event.getAction()", String.valueOf(event.getActionIndex())+"|"+String.valueOf(event.getActionMasked()));
                int action=event.getActionMasked();
                switch(action){
                    case MotionEvent.ACTION_DOWN://一个点按下
                        Log.i("一个点按下","一个点按下");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN://多个点按下
                        Log.i("多个点按下","多个点按下");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("移动","移动");
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.i("松开了某一个","松开了某一个");
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Log.i("全部松开了","全部松开了");
                        break;
                }
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

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    canvas = surfaceholder.lockHardwareCanvas();
                                }else{
                                    canvas = surfaceholder.lockCanvas();
                                }
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
