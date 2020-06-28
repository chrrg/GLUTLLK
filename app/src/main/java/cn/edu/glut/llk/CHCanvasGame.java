package cn.edu.glut.llk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
    void onTouchEvent(MotionEvent event);
//    boolean onTouchMove(MotionEvent event);
//    boolean onTouchEnd(MotionEvent event);
}
//class GameObject{
//    int x,y,w,h;
//    private int backColor=0;
//    private GameObjectDraw od=null;
//    private Bitmap baseBitmap=null;
//    private String text=null;
//    private ObjectStringChange textChange=null;
//    private Paint selfPaint;
//    private int index=100;
//    private Bitmap pic=null;
//    private Movie gif;
//    private long gifStart;
//    private Vector<GameObject> children;
//    OnTouchListener touch;
//    private boolean display=true;
//    void onTouch(OnTouchListener touch){
//        this.touch=touch;
//    }
//    void setDisplay(boolean display){
//        this.display=display;
//    }
//    boolean isIn(int touchX,int touchY){
//        return touchX>x&&touchX<x+w&&touchY>y&&touchY<y+h;
//    }
//    void setPic(Bitmap pic){
//        baseBitmap=null;
//        this.pic=pic;
//    }
//    void setGif(Movie gif){
//        baseBitmap=null;
//        gifStart=android.os.SystemClock.uptimeMillis();
//        this.gif=gif;
//    }
//    void setIndex(int i){
//        index=i;
//    }
//    int getIndex(){
//        return index;
//    }
//    public void clear(){
//        baseBitmap=null;
//    }
//    void setPaint(Paint paint){
//        selfPaint=paint;
//    }
//    void setString(String text){
//        baseBitmap=null;
//        this.text=text;
//    }
//    void setString(ObjectStringChange text){
//        baseBitmap=null;
//        textChange=text;
//    }
//    void setBackColor(int backColor) {
//        baseBitmap=null;
//        this.backColor=backColor;
//    }
//    void removeFromGame(CHCanvasGame game){
//        game.remove(this);
//    }
//    void draw(Canvas c,Paint paint2,GameCamera camera){
//        if(!display)return;//不显示的不渲染
//        if(textChange !=null){
//            String temp=textChange.onListener();
//            if(text==null||!text.equals(temp)){
//                text=temp;
//                baseBitmap=null;
//            }
//        }
//        if(gif !=null){
//            baseBitmap=null;
//        }
//        Paint paint;
//        if(selfPaint!=null)paint=selfPaint;else paint=paint2;
//        if(baseBitmap==null) {
//            baseBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(baseBitmap);
//            if(backColor!=0){
//                canvas.drawColor(backColor);
//            }
//            if (pic != null){
//                pic=Bitmap.createScaledBitmap(pic, this.w, this.h, true);
//                canvas.drawBitmap(pic,0,0,paint);
//            }
//            if (gif != null){
//                Bitmap tempBitmap = Bitmap.createBitmap(gif.width(),gif.height(), Bitmap.Config.ARGB_8888);
//                Canvas temp=new Canvas(tempBitmap);
//                int duration=gif.duration();
//                if(duration==0)duration=1000;
//                gif.setTime((int) ((android.os.SystemClock.uptimeMillis() - gifStart) % duration));
//                gif.draw(temp,0,0);
//                tempBitmap=Bitmap.createScaledBitmap(tempBitmap, this.w, this.h, true);
//                canvas.drawBitmap(tempBitmap,0,0,paint);
//            }
//            if (this.od != null){
//                this.od.onDraw(canvas, paint);
//            }
//            if(text != null){
//                canvas.drawText(text, 0,this.h,paint);
//            }
//        }
//        c.drawBitmap(baseBitmap,this.x-camera.getX(),this.y-camera.getY(),paint);
//    }
//    void set(int x, int y, int w, int h) {
//        this.x=x;
//        this.y=y;
//        this.w=w;
//        this.h=h;
//    }
//    void setDraw(GameObjectDraw od){
//        this.od=od;
//    }
//}
class Animation{
    private long duration=0;//持续时间
    private int type=0;//1 run 2 next 3delay
    private AnimateCallback animateCallback;
    private Map<Object,Integer> old=new HashMap<>();
    Animation(long duration){
        this.duration=duration;
    }
    Animation(long duration,AnimateCallback animateCallback){
        this.duration=duration;
        this.animateCallback=animateCallback;
    }
    int getType() {
        return type;
    }
    void setType(int type) {
        this.type = type;
    }
    long getDuration() {
        return duration;
    }
    AnimateCallback getAnimateCallback() {
        return animateCallback;
    }
    Map<Object,Integer> getOld() {
        return old;
    }
}
interface AnimateCallback{
    int beforeAnimate(Object ob);
    void callback(Object ob,int old,int time);
    void afterAnimate(Object ob);
}
class GameAnimation{
    private Vector<Animation> cur=new Vector<>();
    private Vector<Animation> animation=new Vector<>();
    private Object obj=null;
    private boolean all=false;
    private boolean isRun;
    private long time;
    private long setCurTime(long curTime, boolean isStop){
        long maxDuration=0;
        for(Animation ani:cur){
            int type=ani.getType();
            long duration=ani.getDuration();
            if(duration>maxDuration)maxDuration=duration;
            if(type==1||type==2){
                ani.getAnimateCallback().callback(obj,ani.getOld().get(obj),(int)curTime);
                if(all&&obj instanceof GameObject)//需要循环
                    for(GameObject ob:((GameObject) obj).getChildren())
                        ani.getAnimateCallback().callback(ob,ani.getOld().get(ob),(int)curTime);
            }else if(type==3&&!isStop){
                try {
                    Log.e("sleep",""+duration+"|"+cur.size());
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return maxDuration;
    }
    private void init(){
        isRun=true;
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(isRun){
                    if(cur.size()>0) {
                        //说明当前有动画队列
                        long maxDuration=setCurTime(android.os.SystemClock.uptimeMillis()-time,false);//sleep
                        long curTime=android.os.SystemClock.uptimeMillis()-time;
                        if(curTime>=maxDuration){//动画 结束了
                            setCurTime(curTime,true);//执行到结束
                            for(Animation ani:cur)
                                if(ani.getAnimateCallback()!=null)
                                    ani.getAnimateCallback().afterAnimate(obj);
                            cur.clear();
                        }
                    }else if(animation.size()>0){
                        while(true){
                            Animation ani = animation.get(0);
                            cur.add(ani);
                            if(ani.getAnimateCallback()!=null) {
                                ani.getOld().put(obj, ani.getAnimateCallback().beforeAnimate(obj));
                                if (all && obj instanceof GameObject)//需要循环
                                    for (GameObject ob : ((GameObject) obj).getChildren())
                                        ani.getOld().put(ob, ani.getAnimateCallback().beforeAnimate(ob));
                            }
                            animation.remove(0);
                            if(ani.getType()!=1)break;
                            if(animation.size()==0)break;
                            int type=animation.get(0).getType();
                            if(type!=1)break;
                        }
                        time=android.os.SystemClock.uptimeMillis();
                    }
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    GameAnimation(Object camera){
        this.obj=camera;
        init();
    }
    void destroy(){
        finalize();
    }
    protected void finalize(){
        isRun=false;
    }
    GameAnimation setAll(boolean isAll){
        this.all=isAll;
        return this;
    }
    GameAnimation run(int duration,AnimateCallback animate){//
        Animation anim=new Animation(duration,animate);
        anim.setType(1);
        animation.add(anim);
        return this;
    }
    GameAnimation next(int duration,AnimateCallback animate){
        Animation anim=new Animation(duration,animate);
        anim.setType(2);
        animation.add(anim);
        return this;
    }
    GameAnimation delay(long millis){
        Animation animate=new Animation(millis);
        animate.setType(3);
        animation.add(animate);
        return this;
    }
}
class GameObject{
    public boolean isCanTouch=true;
    private int x,y,z,w,h;
    private String id=null;
    private String Tag="obj";
    private Vector<GameObject> children= new Vector<>();
    private GameObject parent=null;
    private boolean change=true;//false 说明没有改动 true 需要重绘
    private Bitmap buffer=null;
    private CHCanvasGame game;
    private Paint paint=null;
    private String text=null;
    private Bitmap[] pic=null;
//    private Movie gif=null;
    private Map<String,String> style=new HashMap<>();
    private long startTime=android.os.SystemClock.uptimeMillis();
//    private int vertexShader;
//    private int fragmentShader;
    private int textureId=0;
    private int backColor=0;
    private boolean display=true;
    private float[] mModelMatrix;
    private float textX;
    private float textY;

    private GameObject(){
//        Matrix.setIdentityM(mModelMatrix,0);//把矩阵设为单位矩阵
        //1000
        //0100
        //0010
        //0001
    }
    Vector<GameObject> getChildren(){
        return children;
    }
    void init(){
        mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.scaleM(mModelMatrix,0,(float)w/game.getHeight(),(float)h/game.getHeight(),1);
        Matrix.translateM(mModelMatrix,0,1-(game.getWidth()-2*(float)x)/w,(game.getHeight()-2*(float)y)/h-1,0f);
    }
    OnTouchListener[] onTouch=new OnTouchListener[6];
    private GameObject setCanTouch(boolean is){
        isCanTouch=is;
        return this;
    }//设置是否可以响应触摸事件
    private boolean isCanTouch(){
        return isCanTouch;
    }
    GameObject onTouchStart(OnTouchListener onTouchEvent){
        onTouch[0]=onTouchEvent;return this;
    }
    GameObject onTouchMove(OnTouchListener onTouchEvent){
        onTouch[1]=onTouchEvent;return this;
    }
    GameObject onTouchEnd(OnTouchListener onTouchEvent){
        onTouch[2]=onTouchEvent;return this;
    }
    GameObject onTouchEnter(OnTouchListener onTouchEvent){
        onTouch[3]=onTouchEvent;return this;
    }
    GameObject onTouchLeave(OnTouchListener onTouchEvent){
        onTouch[4]=onTouchEvent;return this;
    }
    GameObject onClick(OnTouchListener onTouchEvent){
        onTouch[5]=onTouchEvent;return this;
    }
    void addChild(GameObject o){
        if(o==null)return;
        children.add(o);
    }
    private GameObject(CHCanvasGame game){
        this();
        this.game=game;
    }
    GameAnimation animate(){
        return new GameAnimation(this);
    }
    GameAnimation animate(boolean isAll){
        return new GameAnimation(this).setAll(isAll);
    }
    GameObject(CHCanvasGame game,GameObject parent){
        this(game);
        this.parent=parent;
    }
    private void setPaint(Paint paint){
        this.paint=paint;
    }
    Paint getPaint(){
        if(paint==null)
            if(parent==null)
                paint=new Paint();
            else
                paint=new Paint(parent.getPaint());
        return paint;
    }
    void draw(){
//        Matrix.rotateM(mModelMatrix, 0, 2, 0, 1, 0);
        //先绘制自己，再绘制子元素
        if(!display)return;//不显示，那么自己和子代都不显示
        if(w>0&&h>0) {
            updateTexture();//更新纹理
            game.openGL.drawObj(mModelMatrix, textureId);
        }
        for(GameObject ob:children){
            ob.draw();
        }
    }
    private void updateTexture(){
        if(change||(pic!=null&&pic.length>1)){
            Bitmap b=getBitmap();//画自己获得bitmap
            if(textureId==0)
                textureId=game.openGL.newTexture(b);
            else
                game.openGL.updateTexture(textureId,b);
        }
    }
    private Bitmap getBitmap(){
        if(mModelMatrix==null)init();
        Bitmap temp = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);//重绘当前组件
        Canvas canvas = new Canvas(temp);
        if(backColor!=0)canvas.drawColor(backColor);
        Paint paint=getPaint();
        if (pic != null&&pic.length>0) {
            int index;
            if(pic.length>1){//gif
                index=(int) ((android.os.SystemClock.uptimeMillis() - startTime) % pic.length);
            }else{
                index=0;
            }
            canvas.drawBitmap(pic[index], 0, 0, paint);
        }
        if(text!=null)canvas.drawText(text,textX,textY,paint);
        buffer=temp;
        change=false;
        return buffer;
    }
//    private void updateView(int x,int y){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
//        change=true;
////        if(parent!=null)parent.updateView();
//    }
    void updateView(){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
        change=true;
//        if(parent!=null)parent.updateView();
    }
//    private void change(){
//        updateView();
//    }

//    private Bitmap doDraw(Bitmap temp){
//        for(GameObject ob:children){
//            ob.doDraw();
//        }
//        Bitmap temp = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);//重绘当前组件
//        Canvas canvas = new Canvas(temp);
//        canvas.drawColor(Color.RED);
//        buffer=temp;//缓存这个已绘制的视图
//        change=false;//绘制完成，清除这个组件的状态 修改为无改变状态
//    }
//    private void draw(){
////
////        doDraw();
////    }
//    void useCamera(Canvas canvas) {//使用摄像机，在画布上对这个对象进行渲染
//        GameCamera camera=game.getCamera();
//        camera.fixCamera();//平滑相机移动
//        if(change){//若有改变或相机改变 需要更新
//            this.doDraw();
//            Rect srcRect=new Rect(camera.getX(),camera.getY(),game.getWidth(),game.getHeight());
//            Rect dstRect=new Rect(0,0,game.getWidth(),game.getHeight());
//            canvas.drawBitmap(buffer,srcRect,dstRect,getPaint());
//        }
//        return buffer;
//    }
    public String getTag() {
        return Tag;
    }
    void setTag(String tag) {
        Tag = tag;
    }
    void setText(String text) {
        if(text.equals(this.text))return;
        this.text = text;
        updateView();//通知需要更新
    }

    int getX() {
        return x;
    }

    void setX(int x) {
        if(mModelMatrix!=null)
            Matrix.translateM(mModelMatrix,0,2*((float)x-this.x)/w,0,0);
//        Matrix.translateM(mModelMatrix,0,((float)x-this.x)*2/game.getHeight()/((float)w/game.getHeight()),0,0f);
//        Log.e("test", String.valueOf(((float)x-this.x)/game.getHeight()/((float)w/game.getHeight())));
        this.x=x;
//        Matrix.scaleM(mModelMatrix,0,(float)w/game.getHeight(),(float)h/game.getHeight(),1);
//        Matrix.translateM(mModelMatrix,0,(float)(x-this.x)/((float)w/game.getHeight()),0,0f);
//        Log.e("test", String.valueOf((game.getWidth()-2*(float)(x-this.x))/w));
//        this.x = x;

    }
    void moveX(int x){
        if(mModelMatrix!=null)
            Matrix.translateM(mModelMatrix,0,2*(float)x/w,0,0);
        this.x+=x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        if(mModelMatrix!=null)
            Matrix.translateM(mModelMatrix,0,0,-2*((float)y-this.y)/h,0);
        this.y = y;
    }
    void moveY(int y){
        if(mModelMatrix!=null)
            Matrix.translateM(mModelMatrix,0,0,-2*(float)y/h,0);
        this.y += y;
    }
    int getW() {
        return w;
    }

    void setW(int w) {
        if(w<=0)w=1;
        if(mModelMatrix!=null) {
//        Matrix.scaleM(mModelMatrix,0,2,1,1);
            Matrix.scaleM(mModelMatrix, 0, (float) w / this.w, 1, 1);
//        Matrix.translateM(mModelMatrix,0,((float)w-this.w)/game.getHeight()/((float)w/game.getHeight()),0,0);
            Matrix.translateM(mModelMatrix, 0, 1 - (float) this.w / w, 0, 0);
        }
        this.w = w;
    }
    void moveW(int w){
        setW(this.w+w);
    }

    int getH() {
        return h;
    }

    void setH(int h) {
        if(h<=0)h=1;
        if(mModelMatrix!=null) {
            Matrix.scaleM(mModelMatrix, 0, 1, (float) h / this.h, 1);
            Matrix.translateM(mModelMatrix, 0, 0, (float) this.h / h - 1, 0);
        }
        this.h = h;
    }
    void moveH(int h){
        setH(this.h+h);
    }
    void setBackColor(int parseColor) {
        backColor=parseColor;
        updateView();
    }
    void setId(String value) {
        id=value;
    }
    GameObject getElementById(String id){
        GameObject res;
        if(id.equals(this.id))return this;
        for(GameObject ob:children){
            res=ob.getElementById(id);
            if(res!=null)return res;
        }
        return null;
    }

//    public Bitmap getPic() {
//        return pic;
//    }
    private void picStretch(){
        if("stretch".equals(getStyle("image")))//需要自动拉伸
            for(int index=0;index<pic.length;index++)
                pic[index] = Bitmap.createScaledBitmap(pic[index], w, h, false);
    }
    void setPic(Bitmap pic) {
        this.pic = new Bitmap[]{pic};
        picStretch();
    }
    void setPic(Bitmap[] pic) {
        this.pic = pic;
        picStretch();
    }

//    public Movie getGif() {
//        return gif;
//    }
//
//    void setGif(Movie gif) {
//        this.gif = gif;
//    }
    Map<String, String> getStyle() {
        return style;
    }
    String getStyle(String s) {
        return style.get(s);
    }
    boolean hasStyle(String s) {
        return style.containsKey(s);
    }
//    void setStyle(Map<String, String> style) {
//        this.style = style;
//    }
    void setStyle(String s1, String s2) {
        switch(s1){
            case "backColor":
                setBackColor(Color.parseColor(s2));
                updateView();
                break;
            case "color":
                getPaint().setColor(Color.parseColor(s2));
                updateView();
                break;
            case "fontSize":
                getPaint().setTextSize(game.dpx(this,s2));
                updateView();
                break;
            case "display":
                setDisplay(!"none".equals(s2));
                break;
            case "image":
                //加载中使用到，这里不用写东西
                break;
            case "textX":
                switch(s2){
                    case "left":
                        paint.setTextAlign(Paint.Align.LEFT);
                        textX=0;
                        break;
                    case "center":
                        paint.setTextAlign(Paint.Align.CENTER);
                        textX=(float)w/2;
                        break;
                    case "right":
                        paint.setTextAlign(Paint.Align.RIGHT);
                        textX=w;
                        break;
                    default:

                }
//                if("left".equals(s2)){
//
//                }
//                textX=0;
                updateView();
                break;
            case "textY":
                Paint.FontMetrics fontMetrics1 = paint.getFontMetrics();
                switch(s2){
                    case "top":
//                        Log.e("text", fontMetrics1.top+"|"+fontMetrics1.ascent+"|"+fontMetrics1.leading+"|"+fontMetrics1.bottom);
                        textY=fontMetrics1.descent-fontMetrics1.ascent;
                        break;
                    case "center":
                        textY=(h - fontMetrics1.top - fontMetrics1.bottom)/2;
                        break;
                    case "bottom":
                        textY=(float)h - fontMetrics1.bottom/2;
                        break;
                    default:
                }
                updateView();
                break;
            case "text":
                if("center".equals(s2)){
                    paint.setTextAlign(Paint.Align.CENTER);
                    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                    //fontMetrics.top为基线到字体上边框的距离
                    //fontMetrics.bottom为基线到字体下边框的距离
                    textX=(float)w/2;
                    textY=(h - fontMetrics.top - fontMetrics.bottom)/2;
                    updateView();
                }
        }
        style.put(s1,s2);
    }
//    boolean hasImage(String str){
//        return Arrays.asList(image).contains(str);
//        for(String s:image){
//            if(str.equals(s))return true;
//        }
//        return false;
//    }
    float[] getModelMatrix(){
        return mModelMatrix;
    }

    boolean isDisplay() {
        return display;
    }

    void setDisplay(boolean display) {
        this.display = display;
    }
    void show() {
        this.display = true;
    }
    void hide() {
        this.display = false;
    }

    void setStyleText(String styleText) {
        String[] s=styleText.split(";");
        for(String s1:s){
            if(!s1.contains(":"))continue;
            int s2=s1.indexOf(":");
            String key=s1.substring(0,s2).trim();
            String value=s1.substring(s2+1).trim();
            if("".equals(key))continue;
            setStyle(key,value);
        }
        updateView();
    }
    boolean hasParent(){
        return parent!=null;
    }
    GameObject getParent() {
        return parent;
    }
}
class GameCamera{
    private float[] pos={0,0,1,0,0,0};//当前摄像机位置

//    private int cameraX;//平滑摄像机预计到达的位置
//    private int cameraY;
    private CHCanvasGame game;
    private float[] mViewMatrix = new float[16];//摄像机位置朝向9參数矩阵
    GameCamera(){
        Matrix.setLookAtM(mViewMatrix, 0,pos[0],pos[1],pos[2],pos[3],pos[4],pos[5], 0f, 1f, 0f);
    }
    void setValue(int index,float value){
        pos[index]=value;
        Matrix.setLookAtM(mViewMatrix, 0,pos[0],pos[1],pos[2],pos[3],pos[4],pos[5], 0f, 1f, 0f);
    }
    float getValue(int index){
        return pos[index];
    }
    float[] getMViewMatrix(){
        return mViewMatrix;
    }
//    private void setX(int x){
//        this.x=x;
//        cameraX=x;
//    }
//    private void setY(int y){
//        this.y=y;
//        cameraY=y;
//    }
//    int getCameraX(){
//        return cameraX;
//    }
//    int getCameraY(){
//        return cameraY;
//    }
//    int getX(){
//        return x;
//    }
//    int getY(){
//        return y;
//    }
//    void moveX(int x){
//        this.x+=x;
//        cameraX=this.x;
//    }
//    void moveY(int y){
//        this.y+=y;
//        cameraY=this.y;
//    }
//    void setCameraX(int x){
//        cameraX=x;
//    }
//    void setCameraY(int y){
//        cameraY=y;
//    }
//    boolean fixCamera(){//平滑相机
//        if(cameraX==x&&cameraY==y)return false;
//        if(cameraX!=x)x=(int)(x+(cameraX-x)*0.15);
//        if(cameraY!=y)y=(int)(y+(cameraY-y)*0.15);
//        return true;
//    }
    GameCamera(CHCanvasGame game){
        this();
        this.game=game;
    }
//    GameCamera(CHCanvasGame game,int x,int y){
//        this.game=game;
//        setX(x);
//        setY(y);
//    }
    GameAnimation animate(){
        return new GameAnimation(this);
    }
}
class CHCanvasGame {
    private GameObject root=null;
//    private Vector<GameObject> obj=new Vector<>();
    private GameCamera camera;
    private Canvas canvas;
    private Paint paint;
    private GLSurfaceView surfaceview;
    CHOpenGL openGL;
//    private SurfaceHolder surfaceholder;
    private int w,h;
//    private boolean isRunning;

    private int backGroundColor=0;
    private long startTime=System.currentTimeMillis();
    private Activity activity;
    long getTime(){
        return System.currentTimeMillis()-startTime;
    }

    void showInputMethod(){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(surfaceview, 0);//InputMethodManager.SHOW_FORCED 表示强制显示
        }
    }
    void hideInputMethod(){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(surfaceview.getWindowToken(), 0);//InputMethodManager.HIDE_NOT_ALWAYS
        }
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
    Bitmap[] getGif(String filename){
        AssetManager am=activity.getAssets();
        try {
            Movie gif=Movie.decodeStream(am.open(filename));
            int duration=gif.duration();
            if(duration<=0)duration=1000;
            Bitmap[] frames = new Bitmap[duration];
            for(int i=0;i<duration;i++){
                Bitmap bmp = Bitmap.createBitmap(gif.width(), gif.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                gif.setTime(i);
                gif.draw(canvas, 0, 0);
                frames[i]=bmp;
            }
            return frames;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    void setBackGroundColor(int color){
        backGroundColor=color;
    }
    void setMaxFPS(int fps){ openGL.setMaxFps(fps); }
    int getFPS(){
        return openGL.getFps();
    }
    int getWidth(){
        return this.w;
    }
    int getHeight(){
        return this.h;
    }
    void setCamera(GameCamera c){
        this.camera=c;
        if(openGL!=null)openGL.setCametaMatrix(c.getMViewMatrix());
    }
    GameCamera getCamera() {return camera;}
    private static float parseFloat(String s) {
        if (s == null) return 0;
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException x) {
            return 0;
        }
    }
    private static int parseInt(String s) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException x) {
            return 0;
        }
    }
    private static int parseInt(String s, int defaultValue) {
        if (s == null) return defaultValue;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException x) {
            return defaultValue;
        }
    }
    private String dp(String in){
        if(in.endsWith("vw"))return String.valueOf((int)(parseFloat(in.split("vw")[0]) /100*getWidth()));
        if(in.endsWith("vh"))return String.valueOf((int)(parseFloat(in.split("vh")[0]) /100*getHeight()));
        if(in.endsWith("dp"))return String.valueOf((int)parseFloat(in.split("dp")[0]));
        if(in.endsWith("px"))return String.valueOf((int)parseFloat(in.split("px")[0]));
        return in;
    }
    int dpx(GameObject r,String in){
        in=dp(in);
        if(in.endsWith("%")) {
            int w;
            if(r.hasParent())w=r.getParent().getW();else w=getWidth();
            return (int) (parseFloat(in.split("%")[0]) / 100 * w);
        }
        return parseInt(in);
    }
    int dpy(GameObject r,String in){
        in=dp(in);
        if(in.endsWith("%")) {
            int h;
            if(r.hasParent())h=r.getParent().getH();else h=getHeight();
            return (int) (parseFloat(in.split("%")[0]) / 100 * h);
        }
        return parseInt(in);
    }
    private GameObject parseElement(Element r, GameObject parent){
        if(r==null)return null;
        GameObject r2=new GameObject(this,parent);
        String tagName=r.getTagName();
        r2.setTag(tagName);
        NamedNodeMap attris = r.getAttributes();
        for (int i = 0; i < attris.getLength(); i++) {
            Attr attr = (Attr) attris.item(i);
            String attrName=attr.getName();
            String value=attr.getValue();
            switch(attrName){
                case "width":
                case "w":
                    if(value.startsWith("+"))
                        value=String.valueOf(r2.getParent().getW()+dpx(r2,value.substring(1)));
                    if(value.startsWith("-"))
                        value=String.valueOf(r2.getParent().getW()-dpx(r2,value.substring(1)));
                    r2.setW(dpx(r2,value));
                    break;
                case "height":
                case "h":
                    if(value.startsWith("+"))
                        value=String.valueOf(r2.getParent().getH()+dpy(r2,value.substring(1)));
                    if(value.startsWith("-"))
                        value=String.valueOf(r2.getParent().getH()-dpy(r2,value.substring(1)));
                    r2.setH(dpy(r2,value));
                    break;
                case "left":
                case "l":
                case "x":
                    if(value.startsWith("+"))
                        value=String.valueOf(r2.getParent().getX()+dpx(r2,value.substring(1)));
                    if(value.startsWith("-"))
                        value=String.valueOf(r2.getParent().getX()-dpx(r2,value.substring(1)));
                    r2.setX(dpx(r2,value));
                    break;
                case "top":
                case "t":
                case "y":
                    if(value.startsWith("+"))
                        value=String.valueOf(r2.getParent().getY()+dpy(r2,value.substring(1)));
                    if(value.startsWith("-"))
                        value=String.valueOf(r2.getParent().getY()-dpy(r2,value.substring(1)));
                    r2.setY(dpy(r2,value));
                    break;
                case "right":
                case "r":
                    int right;
                    if(value.startsWith("+")) {
                        if(r2.getParent()!=null)
                            value = String.valueOf((getWidth() - r2.getParent().getX() - r2.getParent().getW()) + dpx(r2, value.substring(1)));
                        else
                            value = String.valueOf(dpx(r2, value.substring(1)));
                    }
                    if(value.startsWith("-"))
                        if(r2.getParent()!=null)
                            value=String.valueOf((getWidth()-r2.getParent().getX()-r2.getParent().getW())-dpx(r2,value.substring(1)));
                        else
                            value = String.valueOf(-dpx(r2, value.substring(1)));
                    right=getWidth()-r2.getX()-dpx(r2,value);
                    if(right<0)right=0;
                    r2.setW(right);
                    break;
                case "bottom":
                case "b":
                    int bottom;
                    if(value.startsWith("+"))
                        if(r2.getParent()!=null)
                            value=String.valueOf((getHeight()-r2.getParent().getY()-r2.getParent().getH())+dpy(r2,value.substring(1)));
                        else
                            value=String.valueOf(dpy(r2,value.substring(1)));
                    if(value.startsWith("-"))
                        if(r2.getParent()!=null)
                            value=String.valueOf((getHeight()-r2.getParent().getY()-r2.getParent().getH())-dpy(r2,value.substring(1)));
                        else
                            value=String.valueOf(-dpy(r2,value.substring(1)));
                    bottom=getHeight()-r2.getY()-dpy(r2,value);
                    if(bottom<0)bottom=0;
                    r2.setH(bottom);
                    break;
                case "text":
                    r2.setText(value);
                    break;
                case "gif":
                    r2.setPic(getGif(value));
                    break;
                case "src":
                    if("gif".equals(tagName)||value.endsWith(".gif"))//gif
                        r2.setPic(getGif(value));
                    else
                        r2.setPic(getImage(value));//图片
                    break;
                case "id":
                    r2.setId(value);
                    break;
                case "style":
                    r2.setStyleText(value);
                    break;
            }
        }
        r2.init();
        NodeList nodeList = r.getChildNodes();
        Node childNode;
        int num = nodeList.getLength();
        for (int temp = 0; temp < num; temp++) {
            childNode = nodeList.item(temp);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) // 判断是否属于节点
                r2.addChild(parseElement((Element) childNode,r2));
        }
        return r2;
    }
    GameObject getGameObjectFromXML(String filename){
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        try {
            AssetManager am=activity.getAssets();
            DocumentBuilder db = xml.newDocumentBuilder();
            Document doc = db.parse(am.open(filename));
            Element r = doc.getDocumentElement();//根目录
            return parseElement(r,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private final static int MAX_TOUCHPOINTS=10;
    private GameObject[] touchEvent=new GameObject[MAX_TOUCHPOINTS];
    private boolean[] touchInner=new boolean[MAX_TOUCHPOINTS];
    @SuppressLint("ClickableViewAccessibility")
    CHCanvasGame (Activity activity, final GameInit init){
        surfaceview=new GLSurfaceView(activity);

        init.onSetGame(this);
        this.activity=activity;
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        final ArrayList<GameObject> kv = new ArrayList<>();

        surfaceview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action=event.getActionMasked();
                int index=event.getActionIndex();
                int id=event.getPointerId(index);
                int pointerCount = event.getPointerCount();
//                Log.e("ts",index+"|"+id+"|"+pointerCount);
                if(pointerCount>MAX_TOUCHPOINTS)pointerCount=MAX_TOUCHPOINTS;
                GameObject touchObj;
                switch(action){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent[id]=objectTouch(event.getX(index),event.getY(index),getGameObject(),0,event,null);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < pointerCount; i++) {
                            id = event.getPointerId(i);
                            touchObj=touchEvent[id];
                            if(touchObj==null)break;
                            if(objectTouch(event.getX(i),event.getY(i),getGameObject(),1,event,touchObj)!=null) {
                                if (!touchInner[id]) {
                                    touchInner[id] = true;
                                    if (touchObj.onTouch[3] != null)
                                        touchObj.onTouch[3].onTouchEvent(event);//进入
                                }
                            }else {
                                if (touchInner[id]) {
                                    touchInner[id] = false;
                                    if (touchObj.onTouch[4] != null)
                                        touchObj.onTouch[4].onTouchEvent(event);//离开
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP://所有被释放
                        touchObj=touchEvent[id];
                        if(touchObj==null)break;
                        if(touchObj.onTouch[2]!=null)touchObj.onTouch[2].onTouchEvent(event);
                        if(touchInner[id]){
                            touchInner[id]=false;
                            if(touchObj.onTouch[4]!=null)touchObj.onTouch[4].onTouchEvent(event);//离开
                            if(touchObj.onTouch[5]!=null)touchObj.onTouch[5].onTouchEvent(event);//点击
                        }
                        touchEvent[id]=null;
                        break;
                }
                return true;
            }
        });
        surfaceview.setEGLContextClientVersion(3);
        openGL=new CHOpenGL(this);
        surfaceview.setRenderer(openGL);
        surfaceview.post(new Runnable() {
            @Override
            public void run() {
                w=surfaceview.getWidth();
                h=surfaceview.getHeight();
                init.onInit();
            }
        });
    }


    private GameObject objectTouch(float x, float y, GameObject gameObject,int index,MotionEvent event,GameObject target) {
        for(GameObject ob:gameObject.getChildren()) {
            GameObject o;
            o=objectTouch(x, y, ob, index, event,target);
            if (o != null)return o;
        }
        if(gameObject.isCanTouch&&gameObject.onTouch[index]!=null)//不为空 说明可以响应
            if(openGL.rayPicking(x,y,gameObject.getModelMatrix())) {//xy在对象范围内
                if (target!=null && target != gameObject) return null;
                gameObject.onTouch[index].onTouchEvent(event);//是否已处理 true为已处理即不继续传递
                return gameObject;
            }
        return null;//未处理
    }

    void setGameObject(GameObject root) {
        this.root=root;
    }
    GameObject getGameObject() {
        return root;
    }
    GLSurfaceView getContentView(){
        return surfaceview;
    }

    public String getAsset(String filename) {
        AssetManager am=activity.getAssets();
        try {
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            InputStream is = am.open(filename);
            int i;while ((i = is.read()) != -1)byteArr.write(i);
            return byteArr.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
