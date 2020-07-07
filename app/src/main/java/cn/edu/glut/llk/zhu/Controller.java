package cn.edu.glut.llk.zhu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.glut.llk.MainLogical;
import cn.edu.glut.llk.wang.UserService;

import static android.content.Context.MODE_PRIVATE;

public class Controller {
    private final  UserService UserService;
    private WeakReference<MainLogical> ref;
    SharedPreferences sp ;
    public static  int RecordScore;
    public String CurrentUser;//登录注册用而已
    public Controller(MainLogical mainLogical, Activity activity) {
            this.ref = new WeakReference<>(mainLogical);
            this.UserService=new UserService(activity);
            this.sp=activity.getPreferences(MODE_PRIVATE);
            this.CurrentUser=sp.getString("CurrentUser",null);//默认为null
    }

    public boolean Login(String username,String password ) {
        MainLogical mainLogical = ref.get();
        if(mainLogical==null)throw new NullPointerException();
        /*=======为空效验=======*/
        if(username==null||username.equals("")) {mainLogical.setText("NoLoggedIn","用户名为空");return false;}
        if(password==null||password.equals("")){mainLogical.setText("NoLoggedIn","密码为空");return false;}
        /*========正则匹配============*/

        if (!Pattern.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$",username)) {mainLogical.setText("NoLoggedIn", "用户名（汉字、字母、数字）格式不对");return false;}

        if(!Pattern.matches("[a-zA-Z0-9]{1,16}",password)){mainLogical.setText("NoLoggedIn","密码（字母、数字）格式不对");return false;}

        if (UserService.CheckUsername(username)) {//用户名存在
            if (UserService.login(username, password))
           { mainLogical.setText("inputFrame","登录成功");
            rememberMe(username);//下一次不登录
            this.CurrentUser=username;
            return  true;}
            else
                {mainLogical.setText("inputFrame","密码错误");return false;}
        }
        else {
            UserService.register(username,password);
            mainLogical.setText("inputFrame","注册-登录成功");
            rememberMe(username);//下一次不登录
            this.CurrentUser=username;
            return true;
        }
    }

public void rememberMe(String RememberUser){
    SharedPreferences.Editor ed = sp.edit();
    ed.putString("CurrentUser",RememberUser);//修改数据,当前用户
    ed.apply();
}

    @TargetApi(Build.VERSION_CODES.N)
    public void sortRank() {
        MainLogical mainLogical = ref.get();
        if (mainLogical == null) {
            Log.e("mainlogical", "为空");
            return;
        }

            Map<String, String> getdata = UserService.getdata();
            AtomicInteger i = new AtomicInteger(1);
            getdata.forEach((k, v) -> {
                if (i.get() <= 5) {
                    mainLogical.setText("Rank" + i, "用户："+k+"  分数："+ v);
                    i.getAndIncrement();
                }
            });
        }


    public  void writeScore(){
        Log.i("写入分数","writeScore");
        //写入分数?只保存最高分？
        String Curruser = sp.getString("CurrentUser", null);

        if (Curruser!=null) { //已登录状态
           String maxScore=UserService.getdata().get(Curruser);
//            if(maxScore !=null)
                if(maxScore==null|| Integer.parseInt(maxScore)<RecordScore)//记录最高分
                UserService.savedata(Curruser,String.valueOf(RecordScore));
        }
    }

    public void   logout(){//注销
        this.CurrentUser=null;//置空
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();//清除所有
        editor.apply();//提交更改
        //删除SharedPreferences

    }
    /*===========计量=========*/
    public static class MyHandler extends Handler {
        public static int GameTime = 1000 * 60 * 2;//默认2分钟
        static int PauseTime=-1;
        private WeakReference<MainLogical> ref;
        private int ProgressBarTime= 1000 * 60 ;//默认1分钟;

        public MyHandler(MainLogical mainLogical) {
            this.ref = new WeakReference<>(mainLogical);
        }

        @Override
        public void handleMessage(final android.os.Message msg) {
            MainLogical mainLogical = ref.get();

            if (mainLogical != null) {


                switch (msg.what) {
                    case 0:
//                        // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                        this.removeMessages(0);
//                        // app的功能逻辑处理
                        if(System.currentTimeMillis()-PauseTime<1){ this.sendEmptyMessageDelayed(0, 800);break;}//防止故意的
                        if (GameTime >= 1000) {
                            int remain = GameTime / 1000;
                            GameTime -= 1000;
                            mainLogical.setProgressBar(ProgressBarTime,GameTime);
                            // 再次发出msg，循环更新
                            remain -= 1;
                            mainLogical.setText("gameTime", Integer.toString(remain));
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
                    case 2:
                        this.removeMessages(2);
//                    if(mainLogical.getObjectById("yun").getX()<game.getWidth())
//                        game.getGameObject().getElementById("yun").setX(game.getGameObject().getElementById("yun").getX()+game.getWidth()/1000);//移动
//                    else game.getGameObject().getElementById("yun").setX(-game.getGameObject().getElementById("yun").getW());//重回
                        this.sendEmptyMessageDelayed(2, 50);
                        break;
                    default:
                        break;
                }
            }
        }

        public void starGameTimeCount(int gameTime) {
            GameTime = gameTime;
            ProgressBarTime=gameTime;
            sendEmptyMessage(0);// 0 消息会减秒娄直到少于1000毫秒
        }

        public void PauseGameCountDown() {
            sendEmptyMessage(1);//清空0消息，不会执行case0了
        }

        public void ContinueGame() {
            sendEmptyMessage(0);//发送0消息，继续
        }

        public  void gameOver() {
            MainLogical mainLogical = ref.get();
            if (mainLogical != null) {
                sendEmptyMessage(1);//清空0消息
                mainLogical.setText("gameTime", "GameOver");
                mainLogical.GameOver();//通知结束
               mainLogical.writeScore();//记录分数
            }
        }
    }
}
