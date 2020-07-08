package cn.edu.glut.llk;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
interface GameInit{
    void onInit();
    void onSetGame(CHCanvasGame game);
}
interface OnTouchListener{
    void onTouchEvent(MotionEvent event);
}
interface GameInput{
    void finish(String inputText);
}
class Animation{
    private long duration=0;//持续时间
    private int type=0;//1 run 2 next 3delay 4runcode
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
interface AnimateNextRun{
    void run();
}
interface AnimationIterator{
    void handle(Object obj);
}
class GameObject{
    private boolean isCanTouch=true;
    private int x,y,z,w,h;
    private String id=null;
    private String Tag="obj";
    private Vector<GameObject> children= new Vector<>();
    public GameObject parentNode=null;
    private boolean change=true;//false 说明没有改动 true 需要重绘
    private Bitmap buffer=null;
    private CHCanvasGame game;
    private Paint paint=null;
    private String text=null;
    private Bitmap[] pic=null;
    private Map<String,String> style=new HashMap<>();
    private long startTime=android.os.SystemClock.uptimeMillis();
    private int c=0;
    private int backColor=0;
    private boolean display=true;
    private float[] mModelMatrix;
    private float textX;
    private float textY;
    private int textureId=0;
    private GameObject(){

    }
    CHCanvasGame getGame(){
        return game;
    }
    Vector<GameObject> getChildren(){
        return children;
    }
    protected void finalize(){
        int[] a=new int[1];
        a[0]=textureId;
        GLES30.glDeleteTextures(1,a,0);
    }
    void Destory(){
        this.parentNode=null;
        synchronized (children){   for(GameObject obj:children){
            obj.Destory();
        }
//        GLES20.glDeleteTextures（1，&id）；//函数释放内存
    }
        children.clear();
        children=null;
    }
    void appendChild(GameObject obj){
        if(obj.parentNode!=null)obj.parentNode.children.remove(obj);
        obj.parentNode=this;
        synchronized (children) {
            children.add(obj);
        }
    }
    void removeChild(GameObject obj){
     synchronized (children){  children.remove(obj);}
    }
    void init(){
        mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.scaleM(mModelMatrix,0,(float)w/game.getHeight(),(float)h/game.getHeight(),1);
        Matrix.translateM(mModelMatrix,0,1-(game.getWidth()-2*(float)x)/w,(game.getHeight()-2*(float)y)/h-1,0f);
    }
    OnTouchListener[] onTouch=new OnTouchListener[6];
    GameObject setCanTouch(boolean is){
        isCanTouch=is;
        return this;
    }//设置是否可以响应触摸事件
    boolean isCanTouch(){
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
    GameObject(CHCanvasGame game){
        this();
        this.game=game;
    }
    GameAnimation animate(){
        return new GameAnimation(this);
    }
    GameAnimation animate(boolean isAll){
        return new GameAnimation(this).setAll(isAll);
    }
//    GameObject(CHCanvasGame game,GameObject parent){
//        this(game);
//        parentNode=parent;
//    }
    void setPaint(Paint paint){
        this.paint=paint;
    }
    Paint getPaint(){
        if(paint==null)
            if(parentNode==null)
                paint=new Paint();
            else
                paint=new Paint(parentNode.getPaint());
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
synchronized (children){
        for(GameObject ob:children){
            ob.draw();
        }}
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
    void updateView(){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
        change=true;
    }
    String getTag() {
        return Tag;
    }
    void setTag(String tag) {
        Tag = tag;
    }
    void setText(String text) {
        if(text==null)text="";
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
        this.x=x;
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
    Map<String, String> getStyle() {
        return style;
    }
    String getStyle(String s) {
        return style.get(s);
    }
    boolean hasStyle(String s) {
        return style.containsKey(s);
    }
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
                        textX=game.dpx(this,s2);
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
                        textY=game.dpy(this,s2);
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
                break;
        }
        style.put(s1,s2);
    }
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
        return parentNode!=null;
    }
    GameObject getParent() {
        return parentNode;
    }

    void setAlpha(int i) {
        Paint paint=getPaint();
        paint.setAlpha(i);
        updateView();
        for(GameObject ob:children)ob.setAlpha(i);
    }

     String getId() {
        return id;
    }

    public int getBackColor() {
        return backColor;
    }

    public Bitmap[] getPic() {
        return pic;
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
    GameCamera(CHCanvasGame game){
        this();
        this.game=game;
    }
    GameAnimation animate(){
        return new GameAnimation(this);
    }
}
class GameAnimation{
    private Vector<Animation> cur=new Vector<>();
    private Vector<Animation> animation=new Vector<>();
    private Object obj=null;
    private boolean all=false;
    private boolean isRun;
    private long time;
    private long setCurTime(final long curTime, boolean isStop){
        long maxDuration=0;
        for(final Animation ani:cur){
            int type=ani.getType();
            long duration=ani.getDuration();
            if(duration>maxDuration)maxDuration=duration;
            if(type==1||type==2){
                iteratorAll(obj,new AnimationIterator(){
                    @Override
                    public void handle(Object obj) {
                        long curT=curTime;
                        if(curT>ani.getDuration())curT=ani.getDuration();
                        ani.getAnimateCallback().callback(obj,ani.getOld().get(obj),(int)curT);
                    }
                });
//                ani.getAnimateCallback().callback(obj,ani.getOld().get(obj),(int)curTime);
//                if(all&&obj instanceof GameObject)//需要循环
//                    for(GameObject ob:((GameObject) obj).getChildren())
//                        ani.getAnimateCallback().callback(ob,ani.getOld().get(ob),(int)curTime);
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
    void iteratorAll(Object ob,AnimationIterator ai){
        ai.handle(ob);
        if (all && obj instanceof GameObject)
            for (GameObject obj : ((GameObject)ob).getChildren())iteratorAll(obj,ai);
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
                            continue;
                        }
                    }else if(animation.size()>0){
                        while(true){
                            final Animation ani = animation.get(0);
                            cur.add(ani);
                            if(ani.getAnimateCallback()!=null) {
                                iteratorAll(obj,new AnimationIterator(){
                                    @Override
                                    public void handle(Object ob1) {
                                        ani.getOld().put(ob1, ani.getAnimateCallback().beforeAnimate(obj));
                                    }
                                });
//                                ani.getOld().put(obj, ani.getAnimateCallback().beforeAnimate(obj));
//                                if (all && obj instanceof GameObject)//需要循环
//                                    for (GameObject ob : ((GameObject) obj).getChildren())
//                                        ani.getOld().put(ob, ani.getAnimateCallback().beforeAnimate(ob));
                            }
                            animation.remove(0);
                            if(ani.getType()!=1)break;
                            if(animation.size()==0)break;
                            int type=animation.get(0).getType();
                            if(type!=1)break;
                        }
                        time=android.os.SystemClock.uptimeMillis();
                    }else
                        isRun=false;
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    GameAnimation(Object object){
        this.obj=object;
        init();
    }
    Object getObj(){
        return obj;
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
    GameAnimation next(AnimateNextRun runCode){
        AnimateCallback animate = new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {return 0;}
            @Override
            public void callback(Object ob, int old, int time) {}
            @Override
            public void afterAnimate(Object ob) {
                runCode.run();
            }
        };
        Animation anim=new Animation(0,animate);
        anim.setType(4);
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
public class CHCanvasGame {
    private GameObject root=null;
    private GameCamera camera;
    private Canvas canvas;
    private Paint paint;
    private GLSurfaceView surfaceview;
    CHOpenGL openGL;
    private int w,h;
    private int backGroundColor=0;
    private long startTime=System.currentTimeMillis();
    private Activity activity;
    boolean isPause;
    long getTime(){
        return System.currentTimeMillis()-startTime;
    }
    void getInput(final GameInput input){
        final EditText edit=new EditText(getActivity());
        new AlertDialog.Builder(getActivity()).setTitle("输入框").setMessage("输入文本：").setView(edit).setPositiveButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                input.finish(edit.getText().toString());
            }
        }).setNegativeButton("取消",new DialogInterface.OnClickListener(){ //增加一个中间的按钮,并增加点击事件
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                input.finish(null);
            }
        }).create().show();
    }
    void getInput(final GameInput input, String title,String message){
        final EditText edit=new EditText(getActivity());
        //增加一个中间的按钮,并增加点击事件
        new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(message).setView(edit)
                .setPositiveButton("确定", (dialogInterface, i) -> input.finish(edit.getText().toString()))
                .setNegativeButton("取消", (dialogInterface, i) -> input.finish(null)).create().show();
    }
    Bitmap[] CutPic(Bitmap raw,int w,int h,int x,int y){
        Bitmap[] result=new Bitmap[x*y];
        int i=0,j=0;
        for(j=0;j<y;j++) {
            for (i = 0; i < x; i++) {
                result[y*x+x]=Bitmap.createBitmap(raw, x*w, y*h,w, h, null, false);
            }
        }
        return result;
    }
    CHCanvasGame(){

    }
    public Activity getActivity(){
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
            GifOpenHelper gHelper=new GifOpenHelper();
            gHelper.read(am.open(filename));
            int duration=gHelper.frameCount;
            Bitmap[] frames = new Bitmap[duration];
            for(int i=0;i<duration;i++)
                frames[i]=gHelper.nextBitmap();
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
        if(openGL!=null)openGL.setCameraMatrix(c.getMViewMatrix());
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
        GameObject r2=new GameObject(this);
        if(parent!=null)parent.appendChild(r2);
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
        init.onSetGame(this);
        if(camera==null)throw new RuntimeException("未设置摄像机，请在onSetGame设置摄像机！");
        surfaceview=new GLSurfaceView(activity);
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
                        touchEvent[id]=objectTouch(event.getX(index),event.getY(index),getGameObject(),0,event,null);//响应ontouchstart
                        if(touchEvent[id]==null)break;
                        touchInner[id] = true;
                        if (touchEvent[id].onTouch[3] != null)touchEvent[id].onTouch[3].onTouchEvent(event);//进入
                        break;
                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < pointerCount; i++) {
                            id = event.getPointerId(i);
                            touchObj=touchEvent[id];
                            if(touchObj==null)break;
                            if(objectTouch(event.getX(i),event.getY(i),getGameObject(),1,event,touchObj)!=null) {//响应ontouchmove
                                if (!touchInner[id]) {
                                    touchInner[id] = true;
                                    if (touchObj.onTouch[3] != null)
                                        touchObj.onTouch[3].onTouchEvent(event);//进入
                                }
                            }else{
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
        if(!gameObject.isDisplay()||!gameObject.isCanTouch())return null;
        for(GameObject ob:gameObject.getChildren()) {
            GameObject o;
            o=objectTouch(x, y, ob, index, event,target);
            if (o != null)return o;
        }
        if(target!=null){
//            if(gameObject.isCanTouch())//不为空 说明可以响应
                if(openGL.rayPicking(x,y,gameObject.getModelMatrix())) {//xy在对象范围内
                    if (target != gameObject) return null;
                    if(gameObject.onTouch[index]!=null)gameObject.onTouch[index].onTouchEvent(event);//是否已处理 true为已处理即不继续传递
                    return gameObject;
                }
        }else {
            if (gameObject.onTouch[index] != null)//不为空 说明可以响应
                if (openGL.rayPicking(x, y, gameObject.getModelMatrix())) {//xy在对象范围内
                    gameObject.onTouch[index].onTouchEvent(event);//是否已处理 true为已处理即不继续传递
                    return gameObject;
                }
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
    String getAsset(String filename) {
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
    void pause() {
        isPause=true;
    }
    void resume() {
        isPause=false;
    }
}