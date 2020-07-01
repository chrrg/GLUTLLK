package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logical {
    private String UsernameTest;
    private String PasswordTest;

    private int MusicSwitch=1;//1为开
    private int Sound=1;
    Bitmap SoundOn;
    Bitmap SoundOff;
    CHCanvasGame game;
    private AnimateLib anLib;
    private MyHandler myHandler;
    private Myobserver myobserver;
    Logical(CHCanvasGame game) {
        this.myobserver =new Myobserver();
        this.anLib = new AnimateLib();
        this.game=game;
        this.myHandler=new MyHandler(this.game);
       this.SoundOn = game.getImage("SoundOn.png");
      this.SoundOff= game.getImage("SoundOff.png");
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
    public GameObject getObjectById(String id){return game.getGameObject().getElementById(id);}
    public void setText(String id,String text){game.getGameObject().getElementById(id).setText(text);}
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
        myHandler.sendEmptyMessage(2);//云漂浮
//        List ids= Arrays.asList(
////                                "RankPage",
//                "inputFrame",
////                                "submenu",
//                "gameBarrier"
//        );
//        ids.forEach(e->CreateLister(e.toString(),()->{}));//notCanTouch 背景层空响应 由背景监听拦截
    }

    public void setGameStartUI() {
//设置默认显示方式
        Map<String, Boolean> setShow = new HashMap<>();
        setShow.put("submenu", false);
        setShow.put("RankPage", false);
        setShow.put("gameBarrier", false);
        setShow.put("inputFrame", false);//输入重用框？？？
        setDisplay(setShow);



        getObjectById("yun").setX(-40);

    }

    //增加监听事件及逻辑，及处理
    public void addListerLogic() {
        addAudioLogic();// 增加音量开关图片切换
        addAccountListerLogic();//登录逻辑
        addRankListerLogic();//排行逻辑
        addMainListerLogic();
        //游戏关卡逻辑
        addGameExitSwitch();
        addGamePuaseHandle();//暂停处理
        addGameContinue();//继续游戏处理
        addGameTimeHandle();
    }

    private void addGameTimeHandle() {

    }

    private void addGameContinue() {
        CreateLister("gameContinue", () -> {
            setDisplay("gamePauseMaskLayer", false);
            setCanTouch("gameBarrierMenu", true);//激活可选种顶部和底部菜单
            myHandler.ContinueGame();//继续倒计时。
        });
    }

    private void addGamePuaseHandle() {
        //时间暂停
        CreateLister("gamePause", () -> {
            setDisplay("gamePauseMaskLayer", true);
            setCanTouch("gameBarrierMenu", false);//顶部和底部菜单不能被选中
            myHandler.PauseGameCountDown();//暂停倒计时
        });

    }

    private void addGameExitSwitch() {
        CreateLister("gameExit",()->{
            setDisplay("gameBarrier",false);
            setDisplay("menu",true);//回主菜单
            getObjectById("gameBlock").parentNode.removeChild(getObjectById("gameBlock"));//移除gameBlock
        });
    }

    private void addMainListerLogic() {
        //menu start
        CreateLister("menu1", () -> {
//            anLib.PopUpAnimate(game, "submenu", true, 400, 400);//添加弹窗效果
            anLib.fadeIn(game,"submenu");
//            setDisplay("submenu", true);// 显示二级菜单

            setCanTouch("menu0",false);
            setCanTouch("BottomButton",false);

        });

        CreateLister("submenuClose", () -> {
            anLib.fadeOut(game,"submenu");
//            setDisplay("submenu", false);

            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);
        });// 关闭二级菜单
        //二级菜单
        //startGame
        CreateLister("startGame", () -> {
            setDisplay("menu", false);// 关闭menu菜单

            startGame1(game);//开始游戏
        });
        CreateLister("EndlessMenu",()->{

            setDisplay("menu", false);// 关闭menu菜单
            startGame2(game);
        });
//menu end


    }

    private void addRankListerLogic() {
        //排行start
        CreateLister("Rank", () -> {
            anLib.PopUpAnimate(game, "RankPage", true, 400, 400);//添加弹窗效果
            setDisplay("RankPage", true);// 显示排行

            setCanTouch( "menu0",false);
            setCanTouch("BottomButton",false);
        });

        CreateLister("RankPageClose", () -> {
            anLib.fadeOut(game,"RankPage");
//            setDisplay("RankPage", false);
            //恢复
            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);
        });// 关闭排行
        //排行end

    }

    private void addAccountListerLogic() {
        // 输入框start
        CreateLister("Account", () -> {
            setDisplay("inputFrame", true);
            setCanTouch("menu0",false);
            setCanTouch("BottomButton",false);//不允许底部
        });//显示输入框
        getObjectById("inputUsername").onTouchStart(event -> {
            Log.e("账号点击了！", "2");
            game.getInput(inputText -> {
                if (inputText == null) Log.i("输入框", "没有输入内容");
                else {
                    Log.e("输入了", inputText);
                    UsernameTest = inputText;
                }
                getObjectById("inputUsername").setText(inputText);
            });
        });
        getObjectById("inputPass").onTouchStart(event -> {
            Log.e("点击了密码框", "2");
            game.getInput(inputText -> {
                if (inputText == null) Log.i("输入框", "没有输入内容");
                else {
                    Log.e("输入了", inputText);
                    PasswordTest = inputText;
                    getObjectById("inputPass").setText(inputText);
                }
            });
        });
        CreateLister("Cancel", () -> {
            setDisplay("inputFrame", false);
            setCanTouch("menu0",true);
            setCanTouch("BottomButton",true);//激活底部

        });//不登录
        CreateLister("Submit", () -> {
            Log.i("提交登录", "登录判断待TODO");
            if (UsernameTest.equals("user1") && PasswordTest.equals("123456")) {
                Log.i("登录成功", UsernameTest + PasswordTest);
                setDisplay("inputFrame", false);//登录判断，成功则关闭登录框
                setCanTouch("menu",true);//退出登录框后可触摸
                setCanTouch("BottomButton",true);//激活底部

            }
        });
        // 输入框end
    }

    public  void addAudioLogic(){
        //音乐声音开关start
        CreateLister("Music", () -> {
            if (MusicSwitch == 1) {
                setText("Music","音乐关");
                MusicSwitch = 0;
            } else {
                setText("Music","音乐开");
                MusicSwitch = 1;
            }
        });

        CreateLister("Sound", () -> {
            if (Sound == 1) {
                setText("Sound","声音开");
                Sound = 0;
                getObjectById("Sound").setPic(SoundOn);
            } else {
                setText("Sound","声音关");
                Sound = 1;
                getObjectById("Sound").setPic(SoundOff);
            }
        });
    }

    private void startGame1(CHCanvasGame game) {

        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层

        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        anLib.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,myobserver);//Endless 普通模式
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间

    }

    private void startGame2(CHCanvasGame game) {
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层

        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        anLib.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,true, myobserver);//Endless 无尽模式
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间
//状态转换
    }
    private  void ResetSomeSceneState(){
//        恢复场景初始状态，比如 退出游戏 再进
    }

}
