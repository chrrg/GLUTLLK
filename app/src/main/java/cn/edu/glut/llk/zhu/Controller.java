package cn.edu.glut.llk.zhu;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.edu.glut.llk.CHCanvasGame;
import cn.edu.glut.llk.MainLogical;

public class Controller {



    public static class MyHandler extends Handler {
        static int GameTime = 1000 * 60 * 2;//默认2分钟
        static int PauseTime=-1;
        private WeakReference<MainLogical> ref;

        public MyHandler(MainLogical mainLogical) {
            this.ref = new WeakReference(mainLogical);
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
            sendEmptyMessage(0);// 0 消息会减秒娄直到少于1000毫秒
        }

        public void PauseGameCountDown() {
            sendEmptyMessage(1);//清空0消息，不会执行case0了
        }

        public void ContinueGame() {
            sendEmptyMessage(0);//发送0消息，继续
        }

        public void gameOver() {
            MainLogical mainLogical = ref.get();
            if (mainLogical != null) {
                sendEmptyMessage(1);//清空0消息
                mainLogical.setText("gameTime", "GameOver");
            }
        }
    }
}
