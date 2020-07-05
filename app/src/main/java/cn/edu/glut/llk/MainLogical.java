package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import cn.edu.glut.llk.GenerateChessboard;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLogical {
    private String UsernameTest;
    private String PasswordTest;

    private int MusicSwitch=1;//1为开
    private int Sound=1;
    Bitmap SoundOn;
    Bitmap SoundOff;
    CHCanvasGame game;
    private MyHandler myHandler;
    private Myobserver myobserver;
    MainLogical(CHCanvasGame game) {
        this.myobserver =new Myobserver();
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
//        myHandler.sendEmptyMessage(2);//云漂浮
    }

    public void setGameStartUI() {
//设置默认显示方式
        Map<String, Boolean> setShow = new HashMap<>();
        setShow.put("RankPage", false);
        setShow.put("gameBarrier", false);
        setShow.put("inputFrame", false);//输入重用框？？？
        setShow.put("Line",false);//line
        setDisplay(setShow);
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
    }
/*====================================================================================================*/

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
           removeGameBlockChessboard();//移动游戏块
        });
    }

    private void addMainListerLogic() {
        //menu start
        CreateLister("startGame", () -> {
            setDisplay("menu", false);// 关闭menu菜单
            startGame(game);//开始游戏
        });
        CreateLister("EndlessMenu",()->{
            setDisplay("menu", false);// 关闭menu菜单
            startGame2(game);
        });
        CreateLister("FullCell",()->{
            setDisplay("menu", false);// 关闭menu菜单
            startGame3(game);
        });
        CreateLister("PingYi",()->{
            setDisplay("menu", false);// 关闭menu菜单
            startGame4(game);
        });
        CreateLister("NextBarrierTest",()->{
            //先移除
            removeGameBlockChessboard();
            //生成新的棋盘
            List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
    GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,false,-1,myobserver);//Endless 普通模式 ,pathRanom物体类型
        });

CreateLister("testmem",()->{
    GameObject gameBlock = new GameObject(game);
    game.getGameObject().getElementById("gameBarrierMenu").appendChild(gameBlock);
    game.getGameObject().getElementById("gameBarrierMenu").removeChild(gameBlock);
    gameBlock.Destory();
});
//menu end


    }

    private void addRankListerLogic() {
        //排行start
        CreateLister("Rank", () -> {
            AnimateLib.PopUpAnimate(game, "RankPage", true, 400, 400);//添加弹窗效果
            setDisplay("RankPage", true);// 显示排行

            setCanTouch( "menu0",false);
            setCanTouch("BottomButton",false);
        });

        CreateLister("RankPageClose", () -> {
            AnimateLib.fadeOut(game,"RankPage");
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
/*====================starGamePlay=====================================*/
    private void startGame(CHCanvasGame game) {

        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层

        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
      GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,false,0, myobserver);
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间
        setDisplay("currentBarrier",false);//当前关卡不显示
    }

    private void startGame2(CHCanvasGame game) {
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层
        setDisplay("currentBarrier",true);//显示关卡层
        setText("currentBarrier","当前关卡第1关");
        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,true,1, myobserver);//重复模式
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间
//状态转换
    }
    private void startGame3(CHCanvasGame game){
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层
        setDisplay("currentBarrier",false);//当前关卡不显示

        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Collections.singletonList(-1);//从全满就行了
        GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,false,2, myobserver);//满格模式
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间
    }

    private void startGame4(CHCanvasGame game){
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层
        setDisplay("currentBarrier",false);//当前关卡不显示
        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),6,6,EmptyColumn,true,false,-1, myobserver);//Endless 平移模式
        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间
    }

    private  void ResetSomeSceneState(){
//        恢复场景初始状态，比如 退出游戏 再进
    }
    private void removeGameBlockChessboard(){
//      synchronized (GameObject.class){
        GameObject gameBlock = game.getGameObject().getElementById("gameBlock");
        game.getGameObject().getElementById("gameBarrierMenu").removeChild(gameBlock);
        gameBlock.Destory();
//        getObjectById("gameBlock").parentNode.removeChild(getObjectById("gameBlock"));//移除gameBlock
//    }
  }

}
