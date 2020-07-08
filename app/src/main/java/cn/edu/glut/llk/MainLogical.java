package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import cn.edu.glut.llk.Myobserver;
import cn.edu.glut.llk.zhu.Controller;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLogical {
    private final Controller MyController;

    private int Sound=1;//1为开
    Bitmap SoundOn;
    Bitmap SoundOff;
    CHCanvasGame game;
    private Controller.MyHandler myHandler;
    private cn.edu.glut.llk.Myobserver myobserver;
    private String Username;
    private String Password;
public static Bitmap[] bitLine;
public static Bitmap[] bitLine2;
    MainLogical(CHCanvasGame game) {
        this.myobserver =new Myobserver();
        this.game=game;
        this.myHandler=new Controller.MyHandler(this);
        this.MyController=new Controller(this,game.getActivity());
        this.SoundOn = game.getImage("SoundOn.png");
        this.SoundOff= game.getImage("SoundOff.png");
        bitLine=game.CutPic(game.getImage("闪电.png"),459,757/7,1,7);
        bitLine2=game.CutPic(game.getImage("闪电竖.png"),757/7,459,7,1);
    }

    public void setProgressBar(int progressBarTime, int gameTime) {
        getObjectById("ProgressBar2").setW(getObjectById("gameTime").getW()*gameTime/progressBarTime);
        getObjectById("ProgressBar").setX(getObjectById("ProgressBar2").getW()+getObjectById("ProgressBar2").getX());

    }

    public void writeScore() {
        MyController.writeScore();
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
    public int getYById(String id) {return game.getGameObject().getElementById(id).getY(); }
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
        setShow.put("MaskBlock",false);//物块点击遮罩
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
        addGameRestartGame();
        addGameReturnMenu();
    }
/*====================================================================================================*/
private  void addGameRestartGame(){
    CreateLister("RestartGame",()->{
        setDisplay("gamePauseMaskLayer", false);
        setCanTouch("gameBarrierMenu", true);//激活可选种顶部和底部菜单
        ReCreateChessboard();
        if(myHandler.EndlessTime)
        myHandler.starGameTimeCount(1000*60*2,true);//重新计时
        else       myHandler.starGameTimeCount(1000*60,true);//重新计时

    });
}
private void addGameReturnMenu(){
    CreateLister("gameReturnMenu",()->{
        setCanTouch("gameBarrierMenu", true);//顶部和底部菜单不能被选中
        setDisplay("gameBarrier",false);
        setDisplay("menu",true);//回主菜单
        removeGameBlockChessboard();//移动游戏块
    });
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
            if(myHandler.GameTime>0)setDisplay("gameContinue",true);
            else setDisplay("gameContinue",false);
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
//        CreateLister("FullCell",()->{
//            setDisplay("menu", false);// 关闭menu菜单
//            startGame3(game);

//        });
        CreateLister("About",()->{
              Uri uri = Uri.parse("https://www.baidu.com");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            game.getActivity().getApplicationContext().startActivity(intent);
        });
        CreateLister("Share",()->{
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
            Intent send=Intent.createChooser(textIntent, "分享");
            send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            game.getActivity().getApplicationContext().startActivity(send);
        });

//        CreateLister("NextBarrierTest", this::ReCreateChessboard);
        CreateLister("ExitGame",()->{
            System.exit(0);//正常退出
        });
//menu end


    }

    private void ReCreateChessboard() {

        removeGameBlockChessboard();//先移除原先的
        //生成新的棋盘
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,false,-1,myobserver);//Endless 普通模式 ,pathRanom物体类型

    }

    private void addRankListerLogic() {
        //排行start
        CreateLister("Rank", () -> {
//            AnimateLib.PopUpAnimate(game, "RankPage", true, 400, 400);//添加弹窗效果
            MyController.sortRank();
            setDisplay("RankPage", true);// 显示排行

            setCanTouch( "menu0",false);
            setCanTouch("BottomButton",false);
        });

        CreateLister("RankPage", () -> {//空白即关闭
//            AnimateLib.fadeOut(game,"RankPage");
            setDisplay("RankPage", false);
            //恢复
            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);
        });// 关闭排行
        CreateLister("RankPagepng",()->{});//空响应，拦截 rankpage
        //排行end

    }

    private void addAccountListerLogic() {
        // 输入框start
        CreateLister("inputFrame",()->{
            setDisplay("inputFrame",false);
            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);//允许底部
        });//遮罩层关闭，同时作为拦截响应
CreateLister("LoggedIn",()->{});
        CreateLister("Account", () -> {
            if(MyController.CurrentUser!=null&&!MyController.CurrentUser.equals("")){
                setDisplay("inputFrame", true);
                setDisplay("NoLoggedIn",false);
                setDisplay("LoggedIn",true);//显示已登录页
                setText("LoggedIn","用户："+MyController.CurrentUser);
                setCanTouch("menu0", false);
                setCanTouch("BottomButton", false);//不允许底部
            }else {
                setDisplay("inputFrame", true);
                setDisplay("NoLoggedIn",true);//显示登录层
                setDisplay("LoggedIn",false);//关闭已登录层
                setText("LoggedIn","登录页");
                setCanTouch("menu0", false);
                setCanTouch("BottomButton", false);//不允许底部
            }
        });//显示输入框
        CreateLister("logout",()->{
            MyController.logout();
            setDisplay("inputFrame",false);
            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);//允许底部
        });
        getObjectById("inputUsername").onTouchStart(event -> {
            Log.e("账号点击了！", "2");
            game.getInput(inputText -> {
                if (inputText == null || inputText.equals("") ) Log.i("输入框", "没有输入内容");
                else {
                    Log.e("输入了", inputText);
                    this.Username= inputText;
                }
                getObjectById("inputUsername").setText(inputText);
            },"用户名","汉字，字母，数字");
        });
        getObjectById("inputPass").onTouchStart(event -> {
            Log.e("点击了密码框", "2");
            game.getInput(inputText -> {
                if (inputText == null || inputText.equals("")) Log.i("输入框", "没有输入内容");
                else {
                    Log.e("输入了", inputText);
                    this.Password = inputText;
//                    getObjectById("inputPass").setText(inputText);
                }
                getObjectById("inputPass").setText(inputText);
            },"密码","字母，数字");
        });
        CreateLister("Cancel", () -> {
            setDisplay("inputFrame", false);
            setCanTouch("menu0",true);
            setCanTouch("BottomButton",true);//激活底部

        });//不登录
        CreateLister("Submit", () -> {
            Log.i("提交登录", "登录判断待TODO");
            if (MyController.Login(this.Username,this.Password)) {
//                Log.i("登录成功", UsernameTest + PasswordTest);
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
        myHandler.starGameTimeCount(1000*60,false);//开启定时器，1秒每步减少时间
        setDisplay("currentBarrier",false);//当前关卡不显示
    }

    private void startGame2(CHCanvasGame game) {
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层
        setDisplay("currentBarrier",true);//显示关卡层
        setText("currentBarrier","1");
        //生成游戏方块矩阵：
        List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
        GenerateChessboard.GenerateGameBlock(game,getObjectById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,true,1, myobserver);//重复模式
        myHandler.starGameTimeCount(1000*60*2,true);//开启定时器，1秒每步减少时间
//状态转换
    }


    public   void GameOver(){
        setDisplay("gamePauseMaskLayer", true);
        setCanTouch("gameBarrierMenu", false);//顶部和底部菜单不能被选中
        setDisplay("gameContinue",false);//不能继续
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
