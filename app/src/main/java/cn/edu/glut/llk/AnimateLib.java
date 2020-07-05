package cn.edu.glut.llk;


/*========================================= 动画========================================================*/

import android.util.Log;

import cn.edu.glut.llk.AnimateCallback;
import cn.edu.glut.llk.CHAnimateTool;
import cn.edu.glut.llk.CHCanvasGame;
import cn.edu.glut.llk.GameAnimation;
import cn.edu.glut.llk.GameObject;

class AnimateLib{
    public static void PopUpAnimate(CHCanvasGame game, String id, boolean isAll, int runDuration, int nextDuration){

        game.getGameObject().getElementById(id).animate(isAll).run(runDuration, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Start","ok");
                return gameObject.getY();
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject gameObject=(GameObject)ob;
                gameObject.setY(old-time/5);
            }
            @Override
            public void afterAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Finish","ok");
            }
        }).next(nextDuration, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Start","ok");
                return gameObject.getY();
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject gameObject=(GameObject)ob;
                gameObject.setY(old+time/5);
            }
            @Override
            public void afterAnimate(Object ob) {
                GameObject gameObject=(GameObject)ob;
                Log.d("animate Finish","ok");
            }
        });
    }
    public  static void fadeIn(CHCanvasGame game,String id){
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().getElementById(id).animate(true);
        tool.fadeIn(ani);
        ani.next(() -> {
            Log.e("ok","okfadeIn");
        });
    }
    public static void fadeOut(CHCanvasGame game,String id){
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().getElementById(id).animate(true);
        tool.fadeOut(ani);
        ani.next(() -> {
            Log.e("ok","okfadeIn");
        });
    }
    public static void  fade(CHCanvasGame game){
        game.getGameObject().setDisplay(false);
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().animate(true).delay(1000);

        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);

        ani.next(() -> {
            Log.e("ok","ok");
        });
    }
    private static void turnOverDrop(){
        //camara翻转180 向下 display

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
    }
}

//Gradle plugin  341  Gradle 4 0 0
