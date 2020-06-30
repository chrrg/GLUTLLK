package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

class AnimateLib {
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

   
    public void fadeIn(CHCanvasGame game,String id){
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().getElementById(id).animate(true);
        tool.fadeIn(ani);
        ani.next(() -> {
            Log.e("ok","okfadeIn");
        });
    }
    public void fadeOut(CHCanvasGame game,String id){
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().getElementById(id).animate(true);
        tool.fadeOut(ani);
        ani.next(() -> {
            Log.e("ok","okfadeIn");
        });
    }
    public void  fade(CHCanvasGame game){
        game.getGameObject().setDisplay(false);
        CHAnimateTool tool=new CHAnimateTool();
        GameAnimation ani = game.getGameObject().animate(true).delay(1000);

        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);
        tool.fadeOut(ani);
        tool.fadeIn(ani);

        ani.next(() -> {
            Log.e("ok","ok");
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

      void   GenerateGameBlock(CHCanvasGame game, GameObject Node, int row, int column, List<Integer> EmptyColumn, int Squares, Myobserver myobserver){ /* n 为 空第几列  squares 为正方形  从一开始函数*/
          GameObject Canvas = new GameObject(game);
          int BlockWidthAndHeight= (int)(game.getWidth()-game.getWidth()*0.05)/column;//宽//默认正方形
          int CanvasWidth=game.getWidth();//默认100%
          int CanvasHeight=(int)(game.getHeight()-game.getHeight()*0.2);//默认80%
           CanvasHeight-= game.getHeight() /10;//再距顶10%

          if (row*BlockWidthAndHeight>CanvasHeight)Log.e("创建方块矩阵","长度不够，行太多，");
          //高度够的情况下：使Canvas高度合适 游戏块的大小
          CanvasHeight=row*BlockWidthAndHeight;
          //考虑不要Canvas?

          Canvas.setId("gameBlock");
          Canvas.setW(CanvasWidth);
          Canvas.setH(CanvasHeight);
          Canvas.setY(game.getHeight()/10*2);// 乘二 是 与上面的 距顶10% 相关联
          Canvas.setText("游戏区域");
          Canvas.setStyleText("fontSize:5vh;color:#FFFAFA;textY:bottom;backColor:#CC0000FF;");
          Node.appendChild(Canvas);//加在关卡一下

          HashMap<GameObject,String> idAndGameObject=new HashMap<>();
          for (int j=0;j<column;j++){
            /*生成列*/
           if( EmptyColumn.contains(j+1))continue;//如果此列为空，则不生成此列

          GameObject a = new GameObject(game);
          a.setId("Column"+j);
          a.setW(BlockWidthAndHeight);
          a.setH(CanvasHeight);//与
          a.setText("col"+j);
          a.setX((int)(game.getWidth()*0.025+j*BlockWidthAndHeight));
          a.setY(Canvas.getY());//距Canvas 0%
          a.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#0000FF;");
          if(j%2==0)a.setStyle("backColor","#CC7FFF00");
          else a.setStyle("backColor","#CCFFFF00");//颜色区分
          Canvas.appendChild(a);

          for (int i=0;i<row;i++){
              GameObject b=new GameObject(game);
              b.setId("Block"+i+j);//id为block
              b.setW(BlockWidthAndHeight);
              b.setH(BlockWidthAndHeight);
              b.setX((int)(game.getWidth()*0.025)+j*BlockWidthAndHeight);//与列相同
              b.setY(Canvas.getY()+i*BlockWidthAndHeight);
              b.setText(String.valueOf(i)+ j);
              b.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#CCD2691E;");
              if(i%2==0)//偶数
              b.setStyle("backColor","#CCD2691E");
              else b.setStyle("backColor","#CC556B2F");
              a.appendChild(b);
              // 事件
              idAndGameObject.put(b,String.valueOf(i)+j);
              b.onTouchStart(event ->myobserver.BlockTouch(game,b)).onClick(event -> myobserver.BlockOnclick(game,b));
          }
      }
          myobserver.setData(idAndGameObject);
     }
}

 class MyHandler extends Handler {

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
            case 2:
                this.removeMessages(2);
                if(game.getGameObject().getElementById("yun").getX()<game.getWidth())
                game.getGameObject().getElementById("yun").setX(game.getGameObject().getElementById("yun").getX()+game.getWidth()/1000);//移动
                else game.getGameObject().getElementById("yun").setX(-game.getGameObject().getElementById("yun").getW());//重回
                this.sendEmptyMessageDelayed(2,50);
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
class Myobserver extends Observable{


     //点击事件，销毁事件，平移事件，退出事件，暂停事件。触摸移动事件
    //点击事件全部注册
   private HashMap<GameObject, String> data;

    public void setData(HashMap<GameObject, String> data){
        this.data = data;

    }
    public void BlockOnclick(CHCanvasGame game, GameObject b){
     game.getGameObject().getElementById("gameScore").setText("ClickBlock"+data.get(b));
     Remove(b);//点击消除，测试用
     PingYi(game,1);//测试用
    }
    public  void BlockTouch(CHCanvasGame game, GameObject b){
        game.getGameObject().getElementById("gameScore").setText("TouchBlock"+data.get(b));
    }
    public void  BlockMove(){}
    @TargetApi(Build.VERSION_CODES.N)
    public void PingYi(CHCanvasGame game, int number){
        data.forEach((k,v)->  {
            k.setX(k.getX()+ k.getW());//平移一个Block单位 //不用平移 块所属的列，因为，xml里还是是从属关系
            if(k.getX()+k.getW()>game.getWidth())
                {
//                    Remove(k.parentNode);//移除父列  不用了
                    k.setX((int)(game.getWidth()*0.025+k.getW()));//重用，回左边
//                    k.parentNode.setDisplay(true);//重新显示这一列,没事，多true几次而已，相当每一个列块元素都显示
                    k.setDisplay(true);//统一true
                    //k.setText();也可以在这里，初始化随机性
                }
        });
        //                    onInit() 重新初始化这一列
        TellAlgorithmsMatrixPermutation();
    }
    public  void TellAlgorithmsMatrixPermutation(){
//        重新初始化我的数据 如图片，能被消的属性
        //告诉矩阵转换最后一列到前面
    }
    public void TellAlgorithsMatrixRemoveBlock(GameObject b){
        //告诉矩阵消掉一个或者两个block
    }
    public void Remove(GameObject b){
        TellAlgorithsMatrixRemoveBlock(b);//在移除之前，告诉算法，消掉的块
//        data.remove(b);//更新数据//即然重用，就不能删除了
        b.setDisplay(false);
//        b.parentNode.removeChild(b);//即然重用，就不能删除了
    }
}