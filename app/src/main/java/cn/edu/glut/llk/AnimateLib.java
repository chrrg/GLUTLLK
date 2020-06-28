package cn.edu.glut.llk;

import android.util.Log;

public class AnimateLib {
    public void PopUpAnimate(CHCanvasGame game,String id,boolean isAll,int runDuration,int nextDuration){

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

    private void turnOverDrop(){
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
