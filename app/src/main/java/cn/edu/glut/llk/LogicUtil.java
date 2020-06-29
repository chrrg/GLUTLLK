package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.method.Touch;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicUtil {
    private AnimateLib anLib;
    private String UsernameTest;
    private String PasswordTest;
    CHCanvasGame game;

    LogicUtil(CHCanvasGame game) {
        this.anLib = new AnimateLib();
        this.game=game;
    }
    public interface ListerLogicCallBack {
        void ListerDoSomething();
    }//   接口

    //    共用逻辑      暂时使用共用逻辑，使用JDK1.8Lambda表达式 如果后期要再用回原来的，
    public void CreateLister( final String id, final ListerLogicCallBack callBack) {
        game.getGameObject().getElementById(id).onTouchStart(event -> Log.i(id, "触摸开始")).onClick(event -> {
            Log.i(id, "被点击");
            callBack.ListerDoSomething();//实现逻辑
        });
    }
    public  void setCanTouch(String id,boolean Touch){game.getGameObject().getElementById(id).setCanTouch(Touch);}
    public void setDisplay( String id, boolean display) {  game.getGameObject().getElementById(id).setDisplay(display); }
    @TargetApi(Build.VERSION_CODES.N)
    public void setDisplay(Map<String, Boolean> display) { display.forEach((id, b) -> game.getGameObject().getElementById(id).setDisplay(b));}
    /*================================================================================================*/
    //入口
    @TargetApi(Build.VERSION_CODES.N)
    void Init() {
        setGameStartUI();//设置开始界面
        addListerLogic();//增加监听逻辑

        List ids= Arrays.asList(
//                                "btn1",
                                "inputFrame",
//                                "menu10",
                                "gameBarrier"
        );
        ids.forEach(e->CreateLister(e.toString(),()->{}));//notCanTouch 背景层空响应 由背景监听拦截
    }

    public void setGameStartUI() {
//设置默认显示方式
        Map<String, Boolean> setShow = new HashMap<>();
        setShow.put("menu10", false);
        setShow.put("btn1", false);
        setShow.put("gameBarrier", false);
        setShow.put("inputFrame", false);//输入重用框？？？
        setDisplay(setShow);

    }

    //增加监听事件及逻辑
    public void addListerLogic() {
//menu start
        CreateLister("menu1", () -> {
            anLib.PopUpAnimate(game, "menu10", true, 500, 500);//添加弹窗效果
            setDisplay("menu10", true);// 显示二级菜单
        });

        CreateLister("menu10Close", () -> setDisplay("menu10", false));// 关闭二级菜单
        //二级菜单
        //startGame
        CreateLister("menu11", () -> {
            setDisplay("menu", false);// 关闭menu菜单
            startGame1(game);//开始游戏
        });
//menu end

//button start
        //排行start
        CreateLister("button1", () -> {
            anLib.PopUpAnimate(game, "btn1", true, 500, 500);//添加弹窗效果
            setDisplay("btn1", true);// 显示排行
        });

        CreateLister("btn1Close", () -> setDisplay("btn1", false));// 关闭排行
        //排行end
        //音乐声音开关start
        CreateLister("button3", () -> {
            if (game.getGameObject().getElementById("button3").isDisplay()) {
                setDisplay("button3", false);
                setDisplay("button30", true);
            } else {
                setDisplay("button3", true);
                setDisplay("button30", false);
            }
        });
        CreateLister("button4", () -> {
            if (game.getGameObject().getElementById("button4").isDisplay()) {
                setDisplay("button4", false);
                setDisplay("button40", true);
            } else {
                setDisplay("button4", true);
                setDisplay("button40", false);
            }
        });
        //音乐声音开关end

        // 输入框start
        CreateLister("button5", () -> setDisplay("inputFrame", true));//显示输入框
        game.getGameObject().getElementById("inputUsername").onTouchStart(event -> {
            Log.e("账号点击了！", "2");
            game.getInput(inputText -> {
                if (inputText == null) Log.i("输入框", "没有输入内容");
                else{ Log.e("输入了", inputText);UsernameTest=inputText;}
                game.getGameObject().getElementById("inputUsername").setText(inputText);
            });
        });
        game.getGameObject().getElementById("inputPass").onTouchStart(event -> {
            Log.e("点击了密码框", "2");
            game.getInput(inputText -> {
                if (inputText == null) Log.i("输入框", "没有输入内容");
                else {Log.e("输入了", inputText);PasswordTest=inputText;}
                game.getGameObject().getElementById("inputPass").setText(inputText);
            });
        });
        CreateLister("Cancel", () -> setDisplay("inputFrame", false));//不登录
        CreateLister("Submit", () -> {
            Log.i("提交登录", "登录判断待TODO");
            if (UsernameTest.equals("user1") && PasswordTest.equals("123456"))
                Log.i("登录成功", UsernameTest + PasswordTest);
                setDisplay("inputFrame",false);//登录判断，成功则关闭登录框
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
//状态转换
    }

}
