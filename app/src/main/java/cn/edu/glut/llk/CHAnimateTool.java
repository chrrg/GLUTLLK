package cn.edu.glut.llk;

import android.util.Log;

/*
* 动画工具
* @Author: CH
*
* */
interface animateToolCallback{
    void finish();
}
class CHAnimateTool {
    private animateToolCallback fn=null;
    /*
    * 渐变显示
    * */
    void fadeIn(GameAnimation ga) {
        fadeIn(ga,null);
    }
    void fadeIn(GameAnimation ga,animateToolCallback fn){
        GameObject o=(GameObject)ga.getObj();
        ga.next(() -> {
            o.setDisplay(true);
        });
        ga.run(200, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject o=(GameObject)ob;
                o.getPaint().setAlpha(0);
                o.updateView();
                return 0;
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject o=(GameObject)ob;
                o.getPaint().setAlpha(time*255/200);
                o.updateView();
            }
            @Override
            public void afterAnimate(Object ob) {}
        });
        ga.next(() -> {
            if(fn!=null)fn.finish();
        });
    }
    /*
    * 渐变隐藏
    * */
    void fadeOut(GameAnimation ga) {
        fadeOut(ga,null);
    }
    void fadeOut(GameAnimation ga,animateToolCallback fn){
        GameObject o=(GameObject)ga.getObj();
        ga.run(200, new AnimateCallback() {
            @Override
            public int beforeAnimate(Object ob) {
                GameObject o=(GameObject)ob;
                o.getPaint().setAlpha(255);
                o.updateView();
                return 0;
            }
            @Override
            public void callback(Object ob, int old, int time) {
                GameObject o=(GameObject)ob;
                o.getPaint().setAlpha(255-time*255/200);
                o.updateView();
            }
            @Override
            public void afterAnimate(Object ob) {}
        });
        ga.next(() -> {
            o.setDisplay(false);
            if(fn!=null)fn.finish();
        });
    }
//    void rightIn(GameAnimation ga) {
//        rightIn(ga,null);
//    }
//    void rightIn(GameAnimation ga,animateToolCallback fn){
//        GameObject o=(GameObject)ga.getObj();
//        ga.run(500, new AnimateCallback() {
//            @Override
//            public int beforeAnimate(Object ob) {
//                return (o.getGame().getWidth()-o.getW())/2;//target
//            }
//            @Override
//            public void callback(Object ob, int old, int time) {
//                o.moveX((int)((old-o.getX())*((float)time/500)));
//            }
//            @Override
//            public void afterAnimate(Object ob) {
//
//            }
//        });
//        ga.next(() -> {
//            if(fn!=null)fn.finish();
//        });
//    }
//    void rightOut(GameAnimation ga) {
//        rightOut(ga,null);
//    }
//    void rightOut(GameAnimation ga,animateToolCallback fn){
//        GameObject o=(GameObject)ga.getObj();
//        final int[] target = {0};
//        ga.next(() -> {
//            target[0]=o.getX();
//        });
//        ga.run(500, new AnimateCallback() {
//            @Override
//            public int beforeAnimate(Object ob) {
//                return o.getX();//target
//            }
//            @Override
//            public void callback(Object ob, int old, int time) {
//
////                int needMove=
////                o.setX();
//            }
//            @Override
//            public void afterAnimate(Object ob) {
//            }
//        });
//        ga.next(() -> {
//            if(fn!=null)fn.finish();
//        });
//    }
}
