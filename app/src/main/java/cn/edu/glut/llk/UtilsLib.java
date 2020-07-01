package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

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

      void   GenerateGameBlock(CHCanvasGame game, GameObject Node, int row, int column, List<Integer> EmptyColumn, boolean Endless, Myobserver myobserver){ /* n 为 空第几列  squares 为正方形  从一开始函数*/
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
          myobserver.setData(idAndGameObject,Endless);//GameOver时或者退出重进，必须重新初始化传这个过去，上面重新生成矩阵。一次游戏不会有问题
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
class Myobserver {


     //点击事件，销毁事件，平移事件，退出事件，暂停事件。触摸移动事件 HashMap 为注册为观察者（） 暂时没有注册观察者，因为棋盘固定了。Remove 是隐藏
    //点击事件全部注册  复用 GameObjet 本身的点击事件接口 也作为 被观察者。
   private HashMap<GameObject, String> data;
    private Elimination elimination;
    boolean Endless=false;//无尽模式 默认 false


    public void setData(HashMap<GameObject, String> data,boolean Endless){
        this.data = data;
        this.elimination=new Elimination();// 消除块用
        this.Endless=Endless;//无尽模式
    }
    public void BlockOnclick(CHCanvasGame game, GameObject b){
     game.getGameObject().getElementById("gameScore").setText("ClickBlock"+data.get(b));
          boolean  isCanPingYi= elimination.click(b,Endless);//告诉它有物体被click了,由它来显示效果,返回结果，告诉我是否要平移
//         Remove(b);//点击消除，测试用
        if(Endless==true && isCanPingYi) PingYi(game,1);//测试用 无尽模式并且两个物体可以消 则平移 ,
    }
    public  void BlockTouch(CHCanvasGame game, GameObject b){
        game.getGameObject().getElementById("gameScore").setText("TouchBlock"+data.get(b));//测试用
        //调用Elimination 进行效果显示，因为涉及到释放 两个
    }
    public void  BlockMove(){}
    @TargetApi(Build.VERSION_CODES.N)
    public void PingYi(CHCanvasGame game, int number){
        data.forEach((k,v)->  {
            k.setX(k.getX()+ k.getW());//平移一个Block单位 //不用平移 块所属的列，因为，xml里还是是从属关系
            if(k.getX()+k.getW()>game.getWidth())
                {
//                    Remove(k.parentNode);//移除父列  不用了
                    k.setX((int)(game.getWidth()*0.025));//重用，回左边
//                    k.parentNode.setDisplay(true);//重新显示这一列,没事，多true几次而已，相当每一个列块元素都显示 不行，
                    k.setDisplay(true);//统一true 有依赖，这句不能删除 ，列模式下是没有问题的。不只是出格的。 行模式下就有问题吗？

                    //k.setText();也可以在这里，初始化随机性

                }
        });
        //                    onInit() 重新初始化这一列
        TellAlgorithmsMatrixPermutation();
    }
    public  void TellAlgorithmsMatrixPermutation(){
//        重新初始化我的数据 如图片，能被消的属性
        //告诉矩阵转换最后一列到前面，并告诉它 转换的列新的随机初始化数据
    }
//    public void TellAlgorithsMatrixRemoveBlock(GameObject b){
//        //告诉矩阵消掉两个block
//         b.getId();//发送Id
//
//    }
  static   public void Remove(GameObject b){
//        TellAlgorithsMatrixRemoveBlock(b);//在移除之前，告诉算法，消掉的块
//        data.remove(b);//更新数据//即然重用，就不能删除了
        b.setDisplay(false);
//        b.parentNode.removeChild(b);//即然重用，就不能删除了
    }

    /*Block处理Hold状态 再点击取消hold状态， 下一个事件 然后通知算法 两个块 处理返回结果 不成功 取消Hold 状态 成功则连线消除 */
    /*队列 满的时候清空*/
}
class Elimination{
    boolean One=false;
    boolean Two=false;
    GameObject OneObj;
    GameObject TwoObj;
    int OneBackColor;
    int TwoBackColor;

     public  boolean click(GameObject b,boolean Endless){

         //只有满的时候发送，并清空，先判断满没满先
         if(One==true&&Two==true){
             if(OneObj==TwoObj){ Two=false;//清空一个
                 return false;}//自己不能点击同一个物体两次来消

             One=Two=false;//清空
             sendNoHold(OneObj,TwoObj);
             //逻辑判断
             if( TellAlgorithsMatrixRemoveBlock(OneObj,TwoObj,Endless))
             {
                 OneObj.setDisplay(false);// 平不平移false就行了。平移的后面会重新true的。
                 TwoObj.setDisplay(false);

                 return true; //不要向下执行了，因为下面会执行一个 返回 true 告诉物体可以被消，可以平移了
             }
            return  false;//不能被消，不平移？
         }
          if(One==true)//一位满，占二位
          { Two=true;
            TwoObj=b;
            TwoBackColor=b.getBackColor();//测试用，保存
            hold(b);//hold效果
          }
         else {
             One=true;//占一位
              OneObj=b;
              OneBackColor=b.getBackColor();//测试用
             hold(b);
         }
         //再判断一次满没满没满。因为进来了一个
         if(One==true&&Two==true){
             if(OneObj==TwoObj){ Two=false;//清空一个
                 return false;}//自己不能点击同一个物体两次来消

             One=Two=false;//清空
             sendNoHold(OneObj,TwoObj);
             //逻辑判断
             if( TellAlgorithsMatrixRemoveBlock(OneObj,TwoObj,Endless))
             {
                 OneObj.setDisplay(false);// 平不平移false就行了。平移的后面会重新true的。
                 TwoObj.setDisplay(false);
                 return true;//再判断一次
             }
         }
         return  false;//还没点够两个物体
     }

    private void hold(GameObject b) {
         b.setBackColor(Color.RED);
    }
    public boolean TellAlgorithsMatrixRemoveBlock(GameObject one, GameObject two, boolean Endless){
        //告诉矩阵消掉两个block
       /* if(!Endless)
            one.getId() to i,j
            two.getId() to i,j
                send(i,j;i,j)
        else
        // 无尽模式。平移的话，id 是没有用的 。*/
//       return Math.random() < 0.5;//  return result;
    return true;
    }

    private void sendNoHold(GameObject one, GameObject two) {
        one.setBackColor(OneBackColor);//恢复原来的颜色	测试用
        two .setBackColor(TwoBackColor);//恢复原来的颜色	测试用
    }


    public  void  ToDoLine(){
         /*路径  得到的只有 i,j 一对*/
/*        id 图片、画矩阵  转弯问题，
如何 i增，j 同向下。 i 相同 j增加 是 向右 。 i同 ，j减是向左。 i 增加 j 增加 是不可能的 只有同时减 是可能的（差一列下，向上）。 只有 一个在增加
        if i==nextpathi  j- left j+ right
        if i>nextpathi j- up
        if i<nextpath j+ down
  方法二：
 创建game 物体        得到的只有i,j 算出w h x y  w h 是已知的 i,j 是从0 开始的
    x=gameWidth*0.025+i*j
    y=CanvasHeight+h*j
    y=id(i,j).ParentNode.getY()
    Generated gameBlockLine
    for from path  extract i,j
    setID(id)
    saveID
    sleep(效果)
    end Loop
    removeDestroy forEach ID
 */
    }
}