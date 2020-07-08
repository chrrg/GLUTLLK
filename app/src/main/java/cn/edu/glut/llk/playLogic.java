package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.glut.llk.zhu.Controller;
import cn.edu.glut.llk.zhu.suanfa.*;

import cn.edu.glut.llk.zhu.suanfa.main;

class GenerateChessboard{
static Bitmap gameChessboardBackground;
 static    void   GenerateGameBlock(CHCanvasGame game, GameObject Node, int row, int column, List<Integer> EmptyColumn,boolean Endless, boolean repeat,int pathRandom ,Myobserver myobserver){ /* n 为 空第几列  squares 为正方形  从一开始函数*/
//       synchronized (GameObject.class) {
     game.getGameObject().getElementById("MaskBlock").setDisplay(false);
     if(gameChessboardBackground==null)gameChessboardBackground=game.getImage("gameChessboardBackground.png");
           GameObject Canvas = new GameObject(game);
           int BlockWidthAndHeight = (int) (game.getWidth() - game.getWidth() * 0.05) / column;//宽//默认正方形
           int CanvasWidth = game.getWidth();//默认100%
           int CanvasHeight = (int) (game.getHeight() - game.getHeight() * 0.2);//默认80%
           CanvasHeight -= game.getHeight() / 10;//再距顶10%

           if (row * BlockWidthAndHeight > CanvasHeight) Log.e("创建方块矩阵", "长度不够，行太多，");
           //高度够的情况下：使Canvas高度合适 游戏块的大小
           CanvasHeight = row * BlockWidthAndHeight;

           final int gameWidth = game.getWidth();// 全部定为int final ,不要动态去获取
           final int gameHeight = game.getHeight();// 全部定为int final ,不要动态去获取
           final int CanvasY = gameHeight / 10 * 2;

           Canvas.setId("gameBlock");
           Canvas.setW(CanvasWidth);
           Canvas.setH(CanvasHeight);
           Canvas.setY(gameHeight / 10 * 2);// 乘二 是 与上面的 距顶10% 相关联
//           Canvas.setText("游戏区域");
            Canvas.setStyleText("image:stretch;");//设置图片之前设置图片样式
//           Canvas.setStyleText("fontSize:5vh;color:#FFFAFA;textY:bottom;backColor:#260000FF;");
           Canvas.setPic(gameChessboardBackground);


           // 生成棋盘
           //静态方法，不用实例化类
           Item[][] items = main.init(game, pathRandom,row,column, EmptyColumn);//一个矩阵，包含了贴哪张图片,blocks是assets目录下的所有文件

           HashMap<String, String> idAndLocation = new HashMap<>();
           for (int j = 0; j < column; j++) {
               /*生成列*/
               if (EmptyColumn.contains(j + 1)) continue;//如果此列为空，则不生成此列

               GameObject a = new GameObject(game);
               a.setId("Column" + j);
               a.setW(BlockWidthAndHeight);
               a.setH(CanvasHeight);//与
//               a.setText("col" + j);
               a.setX((int) (gameWidth * 0.025 + j * BlockWidthAndHeight));
               a.setY(Canvas.getY());//距Canvas 0%
//               a.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#260000FF;");
//               if (j % 2 == 0) a.setStyle("backColor", "#CC7FFF00");
//               else a.setStyle("backColor", "#CCFFFF00");//颜色区分
               Canvas.appendChild(a);

               for (int i = 0; i < row; i++) {
                   GameObject b = new GameObject(game);
                   b.setId("Block" + i + j);//id为block
                   b.setW(BlockWidthAndHeight);
                   b.setH(BlockWidthAndHeight);
                   b.setX((int) (gameWidth * 0.025) + j * BlockWidthAndHeight);//与列相同
                   b.setY(CanvasY + i * BlockWidthAndHeight);
//                   b.setText(String.valueOf(i) + j);
                   b.setStyleText("image:stretch;");//设置图片之前设置图片样式
//                   b.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#26D2691E;image:stretch;");//设置图片之前设置图片样式
//                   if (i % 2 == 0)//偶数
//                       b.setStyle("backColor", "#CCD2691E");
//                   else b.setStyle("backColor", "#CC556B2F");

//                 设置图片
                   if(items[i+1][j].bitmap!=null)
                   b.setPic(items[i+1][j].bitmap);//外围多两行加一是多两层
               a.appendChild(b);
                   // 事件
                   items[i+1][j].setBlocksIDI(i);
                   items[i+1][j].setBlocksIDJ(j);
                   idAndLocation.put("Block" + i + j, "Block" + i + j);
                   b.onTouchStart(event -> myobserver.BlockTouch(game, b)).onClick(event -> myobserver.BlockOnclick(game, b));
               }
           }
//     AppWatcher.INSTANCE.getObjectWatcher().watch(Canvas);
           Node.appendChild(Canvas);//加在关卡一下
           myobserver.setData(game, idAndLocation, Endless,repeat, CanvasHeight, items);//GameOver时或者退出重进，必须重新初始化传这个过去，上面重新生成矩阵。一次游戏不会有问题 .Canvas上下平移用到高度
       } }
// }

class Myobserver {


    //点击事件，销毁事件，平移事件，退出事件，暂停事件。触摸移动事件 HashMap 为注册为观察者（） 暂时没有注册观察者，因为棋盘固定了。Remove 是隐藏
    //点击事件全部注册  复用 GameObjet 本身的点击事件接口 也作为 被观察者。
    private HashMap<String, String> data;
    private Elimination elimination;
    boolean Endless=false;//无尽模式 默认 false
    private int canvasH;
    private CHCanvasGame game;
    private Item[][] item;//这次游戏的图片属于哪个方块
    int currentBarrier=1;
    private int Column1W;
    private int remaining=-1;//剩余的数量
    private boolean repeat=false;
    private int LeftMargin;
    private int Column1Y;

    public void setData(CHCanvasGame game, HashMap<String, String> data, boolean Endless, boolean repeat,int canvasH, Item[][] items){
        this.data = data;
        this.elimination=new Elimination(game,items);// 消除块用
        this.Endless=Endless;//滚动模式
        this.canvasH=canvasH;
        this.game=game;
        this.item=items;//持有一个引用 ,
        this.repeat=repeat;
      if(!repeat) {currentBarrier=1; Controller.RecordScore=0;}// 不是重复模式 则重置分数
        this.remaining=data.size();// 剩余的数量

        Column1W= game.getGameObject().getElementById("Column1").getW();
        Column1Y= game.getGameObject().getElementById("Column1").getY();
        LeftMargin = (int) (game.getWidth() * 0.025);
    }
    public void BlockOnclick(CHCanvasGame game, GameObject b){
//        game.getGameObject().getElementById("gameScore").setText("ClickBlock"+b.getId());//测试用
        boolean  isEliminate= elimination.click(b,Endless);//告诉它有物体被click了,由它来显示效果,返回结果，告诉我是否能消掉

        if (isEliminate) {
            Controller.RecordScore+= 100;
            game.getGameObject().getElementById("gameScore").setText( String.valueOf(Controller.RecordScore));
            game.getGameObject().getElementById("CurrentScore").setText("当前分数：" + Controller.RecordScore);
            remaining-=2;//偶数减
            if(remaining==0 && !repeat) {//不是无尽模式下 才结束
                Controller.RecordScore +=(Controller.MyHandler.GameTime*500)/(1000*60) ;//300分的奖励分
                Controller.MyHandler.GameTime=0;//触发 gameOver事件函数方法
                game.getGameObject().getElementById("CurrentScore").setText("win ! 分数：" + Controller.RecordScore);
            }
            if(remaining==0&&repeat)//无尽模式 下一关
            {       currentBarrier++;//关卡加一
                game.getGameObject().getElementById("currentBarrier").setText(String.valueOf(currentBarrier));
                GameObject gameBlock = game.getGameObject().getElementById("gameBlock");
                game.getGameObject().getElementById("gameBarrierMenu").removeChild(gameBlock);
                gameBlock.Destory();
                List<Integer> EmptyColumn = Arrays.asList(4, 3);//从一开始
                GenerateChessboard.GenerateGameBlock(game,game.getGameObject().getElementById("gameBarrier").getChildren().get(0),8,6,EmptyColumn,false,true,-1, this);//Endless 无尽模式
            }
        }//消掉记录分数
        if(Endless==true && isEliminate) PingYi(game);//测试用 无尽模式并且两个物体可以消 则平移 ,
    }
    public  void BlockTouch(CHCanvasGame game, GameObject b){
//        game.getGameObject().getElementById("gameScore").setText("TouchBlock"+b.getId());//测试用
        //调用Elimination 进行效果显示，因为涉及到释放 两个
    }
    @TargetApi(Build.VERSION_CODES.N)
    public void PingYi(CHCanvasGame game){//上下移动

        data.forEach((k,v)->  {
            game.getGameObject().getElementById(k).setY(game.getGameObject().getElementById(k).getY()+ game.getGameObject().getElementById(k).getH());//平移一个Block单位 //不用平移 块所属的列，因为，xml里还是是从属关系
            if( game.getGameObject().getElementById(k).getY()+ game.getGameObject().getElementById(k).getH()>(game.getHeight() / 10 * 2+canvasH))//到底部了
            {
                game.getGameObject().getElementById(k).setY(game.getHeight() / 10 * 2);//重用，
                game.getGameObject().getElementById(k).setDisplay(true);//统一true 有依赖，这句不能删除
            }
        });
    }

    /*Block处理Hold状态 再点击取消hold状态， 下一个事件 然后通知算法 两个块 处理返回结果 不成功 取消Hold 状态 成功则连线消除 */
    /*队列 满的时候清空*/
}

