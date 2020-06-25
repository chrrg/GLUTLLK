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
import android.graphics.Rect;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_TEXTURE_2D;

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
class GameObject{
    private int x,y,w,h;
    private String Tag="obj";
    private Vector<GameObject> children= new Vector<>();
    private GameObject parent=null;
    private boolean change=true;//false 说明没有改动 true 需要重绘
    private Bitmap buffer=null;
    private CHCanvasGame game;
    private Paint paint=null;
    private String text=null;
    void addChild(GameObject o){
        if(o==null)return;
        children.add(o);
    }
    GameObject(CHCanvasGame game){
        this.game=game;
    }
    GameObject(CHCanvasGame game,GameObject parent){
        this.game=game;
        this.parent=parent;
    }
    private void setPaint(Paint paint){
        this.paint=paint;
    }
    private Paint getPaint(){
        if(paint==null)
            if(parent==null)paint=new Paint();else paint=parent.getPaint();
        return paint;
    }
    private Bitmap getBitmap(){
        if(change){
//            draw();
        }
        return buffer;
    }
//    private void updateView(int x,int y){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
//        change=true;
//        if(parent!=null)parent.updateView();
//    }
    private void updateView(){//需要更新试图 有改动 通知父级有改动，下一次渲染需要重新渲染部分试图
        change=true;
        if(parent!=null)parent.updateView();
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
    void useCamera(Canvas canvas) {//使用摄像机，在画布上对这个对象进行渲染
        GameCamera camera=game.getCamera();
        camera.fixCamera();//平滑相机移动
        if(change){//若有改变或相机改变 需要更新
//            this.doDraw();
            Rect srcRect=new Rect(camera.getX(),camera.getY(),game.getWidth(),game.getHeight());
            Rect dstRect=new Rect(0,0,game.getWidth(),game.getHeight());
            canvas.drawBitmap(buffer,srcRect,dstRect,getPaint());
        }
//        return buffer;
    }
    public String getTag() {
        return Tag;
    }
    public void setTag(String tag) {
        Tag = tag;
    }
    public void setText(String text) {
        if(!text.equals(this.text))this.text=text;
        updateView();//通知需要更新
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
class GameCamera{
    private int x=0;//当前摄像机位置
    private int y=0;
    private int cameraX;//平滑摄像机预计到达的位置
    private int cameraY;
    private CHCanvasGame game;
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
    boolean fixCamera(){//平滑相机
        if(cameraX==x&&cameraY==y)return false;
        if(cameraX!=x)x=(int)(x+(cameraX-x)*0.15);
        if(cameraY!=y)y=(int)(y+(cameraY-y)*0.15);
        return true;
    }
    GameCamera(CHCanvasGame game){
        this.game=game;
    }
    GameCamera(CHCanvasGame game,int x,int y){
        this.game=game;
        setX(x);
        setY(y);
    }
}
class CHCanvasGame {
    private GameObject root=null;
//    private Vector<GameObject> obj=new Vector<>();
    private GameCamera camera;
    private Canvas canvas;
    private Paint paint;
    private GLSurfaceView surfaceview;
    private CHOpenGL openGL;
//    private SurfaceHolder surfaceholder;
    private int w,h;
    private boolean isRunning;

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
    void setCamera(GameCamera c){this.camera=c;}
    GameCamera getCamera() {return camera;}
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
    private int dpx(String in){
        if(in.endsWith("%"))return parseInt(in.split("%")[0])/100*getWidth();
        return parseInt(in);
    }
    private int dpy(String in){
        if(in.endsWith("%"))return parseInt(in.split("%")[0])/100*getHeight();
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
                case "w":
                    r2.setW(dpx(value));
                    break;
                case "h":
                    r2.setH(dpy(value));
                    break;
                case "x":
                    r2.setX(dpx(value));
                    break;
                case "y":
                    r2.setY(dpy(value));
                    break;
                case "r":
                    r2.setW(getWidth()+r2.getX()-dpx(value));
                    break;
                case "b":
                    r2.setH(getHeight()+r2.getY()-dpy(value));
                    break;
                case "text":
                    r2.setText(value);
                    break;
                case "background":
                    Color.parseColor(value);
                    break;
                case "src":
                    if("gif".equals(tagName)||value.endsWith(".gif")){
                        //gif

                    }else{
                        //图片

                    }
//                    r2.setW(dpx(r.getAttribute("w")));
//                    break;
                    break;


            }
//            System.out.print(" " +  + "=\"" + attr.getValue() + "\"");
        }
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
    private void sort(){
//        Collections.sort(obj, new Comparator<GameObject>() {
//            @Override
//            public int compare(GameObject o1, GameObject o2) {
//                if (o1 != null && o2 != null)return Integer.compare(o1.getIndex(), o2.getIndex());
//                return 0;
//            }
//        });
    }
    private void drawOnce(Canvas c,Paint paint){
        if(root==null)return;//没有根对象

//        camera.fixCamera();
//        if(backGroundColor!=0)canvas.drawColor(backGroundColor);

//        sort();
        root.useCamera(c);//对游戏对象进行更新

//        for(GameObject ob:obj){
//            ob.draw(c,paint,camera);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    CHCanvasGame (Activity activity, int id, final GameInit init){
        surfaceview=new GLSurfaceView(activity);

        init.onSetGame(this);
        this.activity=activity;
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
//        surfaceview = activity.findViewById(id);
        final ArrayList<GameObject> kv = new ArrayList<>();
//        surfaceview.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                int x = (int)event.getX()+camera.getX();
//                int y = (int)event.getY()+camera.getY();
//                boolean result=false;
//                Log.i("event.getAction()", String.valueOf(event.getActionIndex())+"|"+String.valueOf(event.getActionMasked()));
//                int action=event.getActionMasked();
//                switch(action){
//                    case MotionEvent.ACTION_DOWN://一个点按下
//                        Log.i("一个点按下","一个点按下");
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN://多个点按下
//                        Log.i("多个点按下","多个点按下");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Log.i("移动","移动");
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP:
//                        Log.i("松开了某一个","松开了某一个");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        Log.i("全部松开了","全部松开了");
//                        break;
//                }
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
//                return true;
//            }
//        });
        surfaceview.setEGLContextClientVersion(2);
        openGL=new CHOpenGL(this);
        surfaceview.setRenderer(openGL);
//        surfaceholder = surfaceview.getHolder();
//        surfaceholder.addCallback(new SurfaceHolder.Callback(){
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                isRunning = true;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (isRunning) {
//
////                                try {
////                                    Thread.sleep(1); // 这个就相当于帧频了，数值越小画面就越流畅
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
//
//                        }
//                    }
//                }).start();
//            }
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {isRunning=false;}
//        });
        surfaceview.post(new Runnable() {
            @Override
            public void run() {
                w=surfaceview.getWidth();
                h=surfaceview.getHeight();
                init.onInit();
            }
        });
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
    void pause() {
        surfaceview.onPause();
    }
    void resume() {
        surfaceview.onResume();
    }
}
