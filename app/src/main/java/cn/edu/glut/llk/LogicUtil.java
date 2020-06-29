package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicUtil {
    private AnimateLib anLib;
    CHCanvasGame game;

    private String UsernameTest;
    private String PasswordTest;

    private int MusicSwitch=1;//1为开
    private int Sound=1;

    private MyHandler myHandler;
    LogicUtil(CHCanvasGame game) {
        this.anLib = new AnimateLib();
        this.game=game;
       this.myHandler=new MyHandler(this.game);
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
        });
    }

    private void addMainListerLogic() {
        //menu start
        CreateLister("menu1", () -> {
            anLib.PopUpAnimate(game, "submenu", true, 500, 500);//添加弹窗效果
            setDisplay("submenu", true);// 显示二级菜单

            setCanTouch("menu0",false);
            setCanTouch("BottomButton",false);

        });

        CreateLister("submenuClose", () -> {
            setDisplay("submenu", false);

            setCanTouch("menu0", true);
            setCanTouch("BottomButton", true);
        });// 关闭二级菜单
        //二级菜单
        //startGame
        CreateLister("startGame", () -> {
            setDisplay("menu", false);// 关闭menu菜单

            startGame1(game);//开始游戏
        });
//menu end


    }

    private void addRankListerLogic() {
        //排行start
        CreateLister("Rank", () -> {
            anLib.PopUpAnimate(game, "RankPage", true, 500, 500);//添加弹窗效果
            setDisplay("RankPage", true);// 显示排行

           setCanTouch( "menu0",false);
            setCanTouch("BottomButton",false);
        });

        CreateLister("RankPageClose", () -> {
            setDisplay("RankPage", false);
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
            } else {
              setText("Sound","声音关");
                Sound = 1;
            }
        });
    }

    private void startGame1(CHCanvasGame game) {
        // 开始游戏
        setDisplay("gameBarrier", true);//显示游戏关卡
        setDisplay("gamePauseMaskLayer", false);//不显示暂停层

        myHandler.starGameTimeCount(1000*60);//开启定时器，1秒每步减少时间

    }

    private void startGame2(CHCanvasGame game) {
        // 开始游戏
//        game.getGameObject().getElementById("gameBarrier").setDisplay(true);
//状态转换
    }
 private  void ResetSomeSceneState(){
//        恢复场景初始状态，比如 退出游戏 再进
 }
   static class MyHandler extends  Handler{

        CHCanvasGame game;
        static int GameTime=1000*60*2;//默认2分钟

       public MyHandler(CHCanvasGame game) {
           this.game = game;
       }

       @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
//                        // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                        this.removeMessages(0);
//                        // app的功能逻辑处理
                        if(GameTime>=1000) {
                            int remain=GameTime/1000;
                            GameTime -= 1000;
                           // 再次发出msg，循环更新
                            remain-=1;
                            game.getGameObject().getElementById("gameTime").setText(Integer.toString(remain));
                            this.sendEmptyMessageDelayed(0, 1000);
                        } else {
                            gameOver();//不发送消息，同case:1效果，

                        }
                        break;

                    case 1:
                        // 直接移除，定时器停止
//
//                        removeMessages会将handler对应message queue里的消息清空，如果带了int参数则是对应的消息清空。队列里面没有消息则handler会不工作，但不表示handler会停止。当队列中有新的消息进来以后handler还是会处理。
//                        1、这个方法使用的前提是之前调用过sendEmptyMessageDelayed(0, time)，意思是延迟time执行handler中msg.what=0的方法；
//                        2、在延迟时间未到的前提下，执行removeMessages(0)，则上面的handler中msg.what=0的方法取消执行；
//                        3、在延迟时间已到，handler中msg.what=0的方法已执行，再执行removeMessages(0)，不起作用。
                        this.removeMessages(0);// 暂停游戏,移掉0消息，不会进0里。
                        break;

                    default:
                        break;
                }
            }
            public  void starGameTimeCount(int GameTime){
                MyHandler.GameTime =GameTime;
                sendEmptyMessage(0);// 0 消息会减秒娄直到少于1000毫秒
            }
            public  void  PauseGameCountDown(){
               sendEmptyMessage(1);//清空0消息，不会执行case0了
            }
            public void ContinueGame(){
                sendEmptyMessage(0);//发送0消息，继续
            }
            public void gameOver(){
                sendEmptyMessage(1);//清空0消息
                game.getGameObject().getElementById("gameTime").setText("GameOver");

            }
        }


}
