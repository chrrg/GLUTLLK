package cn.edu.glut.llk;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

public class LogicUtil {
    protected  AnimateLib anLib;

     LogicUtil() {
        this.anLib = new AnimateLib();
    }

    public  void setGameStartUI(final CHCanvasGame game){
//设置默认显示方式
        game.getGameObject().getElementById("menu10").setDisplay(false);
        game.getGameObject().getElementById("btn1").setDisplay(false);
        game.getGameObject().getElementById("gameBarrier").setDisplay(false);
    }

//增加监听事件及逻辑
    public  void addListerLogic(final CHCanvasGame game ){
//menu start
        game.getGameObject().getElementById("menu1").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu1","触摸开始");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu1","被点击");
                anLib.PopUpAnimate(game,"menu10",true,500,500);//添加弹窗效果
                game.getGameObject().getElementById("menu10").setDisplay(true);// 显示二级菜单
            }
        });

        game.getGameObject().getElementById("menu10Close").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu10Close","触摸开始");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu10Close","被点击");
                game.getGameObject().getElementById("menu10").setDisplay(false);// 关闭二级菜单
            }
        });
        //二级菜单
        //startGame
        game.getGameObject().getElementById("menu11").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu11","触摸开始");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("menu11","被点击");
                game.getGameObject().getElementById("menu").setDisplay(false);// 关闭menu菜单
                startGame1(game);//开始游戏
            }
        });

//menu end

//button start
        //排行start
        game.getGameObject().getElementById("button1").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button1","触摸button1");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button1","被点击");
                anLib.PopUpAnimate(game,"btn1",true,500,500);//添加弹窗效果
                game.getGameObject().getElementById("btn1").setDisplay(true);// 显示排行
            }
        });

        game.getGameObject().getElementById("btn1Close").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("btn1Close","btn1Close 触摸");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("btn1Close","被点击");
                game.getGameObject().getElementById("btn1").setDisplay(false);// 关闭排行
            }
        });
        //排行end
        //音乐声音开关start
        game.getGameObject().getElementById("button3").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button3","button3 触摸");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button3","被点击");
                if(  game.getGameObject().getElementById("button3").isDisplay())
                { game.getGameObject().getElementById("button3").setDisplay(false);game.getGameObject().getElementById("button30").setDisplay(true);}
                else { game.getGameObject().getElementById("button3").setDisplay(true);game.getGameObject().getElementById("button30").setDisplay(false);}
            }
        });
        game.getGameObject().getElementById("button4").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button4","button3 触摸");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button4","被点击");
                if(  game.getGameObject().getElementById("button4").isDisplay())
                { game.getGameObject().getElementById("button4").setDisplay(false);game.getGameObject().getElementById("button40").setDisplay(true);}
                else { game.getGameObject().getElementById("button4").setDisplay(true);game.getGameObject().getElementById("button40").setDisplay(false);}
            }
        });
        //音乐声音开关end

        //分享start
        game.getGameObject().getElementById("button2").onTouchStart(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button2","button2 触摸");
            }
        }).onClick(new OnTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.i("button2","被点击");
                //TODO 分享待实现
            }
        });
        //分享end
        // 输入框start
        game.getGameObject().getElementById("input").onTouchStart(new OnTouchListener(){
            @Override
            public void onTouchEvent(MotionEvent event) {
                Log.e("账号点击了！","2");
//                AlertDialog.Builder builder=;
                game.getInput(new GameInput(){
                    @Override
                    public void finish(String inputText) {
                        if(inputText==null)Log.i("输入框","没有输入内容");
                        else Log.e("输入了",inputText);
                    }
                });
            }
        });
        // 输入框end
//      button end
    }

    private void startGame1(CHCanvasGame game) {
        // 开始游戏
        game.getGameObject().getElementById("gameBarrier").setDisplay(true);

    }

    private void startGame2(CHCanvasGame game) {
        // 开始游戏
//        game.getGameObject().getElementById("gameBarrier").setDisplay(true);

    }
//AOP编程
}
