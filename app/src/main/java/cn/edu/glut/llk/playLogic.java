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
           Canvas.setText("游戏区域");
           Canvas.setStyleText("fontSize:5vh;color:#FFFAFA;textY:bottom;backColor:#260000FF;");
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
               a.setText("col" + j);
               a.setX((int) (gameWidth * 0.025 + j * BlockWidthAndHeight));
               a.setY(Canvas.getY());//距Canvas 0%
               a.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#260000FF;");
               if (j % 2 == 0) a.setStyle("backColor", "#CC7FFF00");
               else a.setStyle("backColor", "#CCFFFF00");//颜色区分
               Canvas.appendChild(a);

               for (int i = 0; i < row; i++) {
                   GameObject b = new GameObject(game);
                   b.setId("Block" + i + j);//id为block
                   b.setW(BlockWidthAndHeight);
                   b.setH(BlockWidthAndHeight);
                   b.setX((int) (gameWidth * 0.025) + j * BlockWidthAndHeight);//与列相同
                   b.setY(CanvasY + i * BlockWidthAndHeight);
                   b.setText(String.valueOf(i) + j);
                   b.setStyleText("fontSize:1vh;color:#FFFAFA;textY:bottom;backColor:#26D2691E;image:stretch;");//设置图片之前设置图片样式
                   if (i % 2 == 0)//偶数
                       b.setStyle("backColor", "#CCD2691E");
                   else b.setStyle("backColor", "#CC556B2F");

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
            game.getGameObject().getElementById("gameScore").setText("分数：" + Controller.RecordScore);
            game.getGameObject().getElementById("CurrentScore").setText("当前分数：" + Controller.RecordScore);
            remaining-=2;//偶数减
            if(remaining==0 && !repeat) {//不是无尽模式下 才结束
//                Controller.RecordScore = score;
                Controller.MyHandler.GameTime=0;//触发 gameOver事件函数方法
                game.getGameObject().getElementById("CurrentScore").setText("win ! 分数：" + Controller.RecordScore);
            }
            if(remaining==0&&repeat)//无尽模式 下一关
            {       currentBarrier++;//关卡加一
                game.getGameObject().getElementById("currentBarrier").setText("当前关卡 第"+currentBarrier+"关");
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
 class Elimination{
    private final CHCanvasGame game;
    private final Item[][] item;
    private final int Column1Y;
    private final int Column1W;
    private final int LeftMargin;
    GameObject OneObj;
   WeakReference<GameObject>  MaskRef;

    public Elimination(CHCanvasGame game, Item[][] items) {
        this.game=game;
        this.item=items;
        Column1Y= game.getGameObject().getElementById("Column1").getY();
        Column1W= game.getGameObject().getElementById("Column1").getW();
        this.MaskRef=new WeakReference<>(game.getGameObject().getElementById("MaskBlock"));

        LeftMargin = (int) (game.getWidth() * 0.025);
    }

    public  boolean click(GameObject b,boolean Endless){
        //只有满的时候发送，并清空，先判断满没满先  只hold一个效果
        if (OneObj == null)
        {
            OneObj = b;
            hold(b);//点击效果
        }
        else if (OneObj == b) {
            sendNoHold(b);
            OneObj=null;//因为false了
            return false;
            } else {
                //逻辑判断
                if (TellAlgorithsMatrixRemoveBlock(OneObj, b, Endless)) {
                    OneObj.setDisplay(false);// 平不平移false就行了。平移的后面会重新true的。
                    b.setDisplay(false);
                    sendNoHold(OneObj); //成功关闭效果
                    OneObj=null;
                    return true; //不要向下执行了，因为下面会执行一个 返回 true 告诉物体可以被消，可以平移了
                }
                sendNoHold(OneObj);//不成功取消
                 OneObj=null;
            return false;//不能被消，不平移？
            }

        return  false;//还没点够两个物体
    }

     private void sendNoHold(GameObject b) {
         game.getGameObject().getElementById("MaskBlock").setDisplay(false);//不显示

         b.setY(b.getY()+20);
         b.setX(b.getX()+20);
     }


     private void hold(GameObject b) {
        Log.e("消块","进来多少次");//调试用
//        b.setBackColor(Color.RED);
//      synchronized ( game.getGameObject()) {
         GameObject Mask=MaskRef.get();
         if(Mask==null) throw new NullPointerException();
          Mask.setW(Column1W+40);
          Mask.setH(Column1W+35);
          Mask.setY(b.getY()-30);
          Mask.setX(b.getX()-30);

         b.setY(b.getY()-20);
         b.setX(b.getX()-20);
//      }
        Mask.setDisplay(true);//显示
//        Bitmap[] bitmap={ b.getPic()[0],game.getImage("输入框-透明底.png")};
//        b.setPic(bitmap);
    }
    @TargetApi(Build.VERSION_CODES.N)
    public boolean TellAlgorithsMatrixRemoveBlock(GameObject one, GameObject two, boolean Endless){
        //告诉矩阵消掉两个block
        if(Endless){//移动模式
            //id
            int srcI;int srcJ;int dirI;int dirJ;// 无尽模式。平移的话，id 是没有用的 。*/
            srcI=(one.getY()-Column1Y+Column1W/2)/Column1W;
            srcJ=(one.getX()-LeftMargin+Column1W/2)/Column1W;
            dirI=(two.getY()-Column1Y+Column1W/2)/Column1W;
            dirJ=(two.getX()-LeftMargin+Column1W/2)/Column1W;
            if( item[srcI+1][srcJ].bitmap==item[dirI+1][dirJ].bitmap)
            {

                List<Point> path= LinkSearch.MatchBolckTwo(item,new Point(srcI+1,srcJ),new Point(dirI+1,dirJ));
                if(path!=null)
                {
                    path.forEach((k)-> System.out.println("path为："+k.x+k.y));
                    item[srcI+1][srcJ].setEmpty();//设为没有被占领
                    item[dirI+1][dirJ].setEmpty();  // 还要清空格子为em
                    ToLine(path,srcI,srcJ,dirI,dirJ);//连线
                     main.updateChessBoard(item);  //平移要更新棋盘
                    for(int j=0;j< item[1].length;j++){
                        if(!item[1][j].isEmpty())
                            game.getGameObject().getElementById("Block"+item[1][j].blocksIDI+item[1][j].blocksIDJ).setPic(item[1][j].bitmap);
                    }
                    //记录分数
                    return  true;
                }
                else  return false;
            }
            else  return false;//两个图片不是同一个

        }

//       return Math.random() < 0.5;//  return result;
//        one.getPic();//返回的是一个数组
        if(!Endless){
            Pattern reg = Pattern.compile("(\\d)(\\d)");
            String O=one.getId();
           String T= two.getId();
            Matcher matcher = reg.matcher(O);
            Matcher matcher1Dir = reg.matcher(T);
            if(matcher.find()&& matcher1Dir.find()) System.out.println("找到正则id");//先查找到第一个匹配的。
            else throw new Resources.NotFoundException();//id 没有找到
            int srcI= Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            int srcJ= Integer.parseInt(Objects.requireNonNull(matcher.group(2)));


            int dirI= Integer.parseInt(Objects.requireNonNull(matcher1Dir.group(1)));
            int dirJ= Integer.parseInt(Objects.requireNonNull(matcher1Dir.group(2)));
           if( item[srcI+1][srcJ].bitmap==item[dirI+1][dirJ].bitmap)
           {

              List<Point> path= LinkSearch.MatchBolckTwo(item,new Point(srcI+1,srcJ),new Point(dirI+1,dirJ));
              if(path!=null)
                  {
                      path.forEach((k)-> System.out.println("path为："+k.x+k.y));
                      item[srcI+1][srcJ].setEmpty();//设为没有被占领
                      item[dirI+1][dirJ].setEmpty();  // 还要清空格子为em
                      ToLine(path,srcI,srcJ,dirI,dirJ);//连线
                      //记录分数
                      return  true;
                  }//可以 isempty??
               else  return false;
           }
           else  return false;//两个图片不是同一个
        }

        return false;
    }


    /*分段函数,递归
     * 数组 int[]
     * */
    static class ij{
        public int dir;

        public ij(int i, int j) {
            this.i = i;
            this.j = j;
            this.start= false;
            this.dir=-1;
        }
        boolean start;
        boolean first=false;
        boolean last=false;
        int i;int j;}

    @TargetApi(Build.VERSION_CODES.N)
    public void  ToLine(List<Point> points, int srcI, int srcJ, int dirI, int dirJ)  {

            // 不是null 才进来，最多 空集
//        if(!points.isEmpty())//null和空集合isEmpty不是同一个东西
            points.add(0,new Point(srcI+1,srcJ));//不是空集则 加上源消掉的物块
        //最后的dir考虑吗  paints不空则说明 至少一条直线 （一个格子算吗？）
        points.add(new Point(dirI+1,dirJ));

        GameObject line=new GameObject(game);
        line.setW(500);
        line.setH(500);//用绝对值 ？
        line.setX(12);
        line.setY(12);
        line.setId("nnn");
        GameObject LINES= game.getGameObject().getElementById("Line");//所有动态创建的gameobject 一定要设置w h x y 不然使用会报错
        LINES.setDisplay(true);
        LINES.appendChild(line);

       final  int WH=Column1W/2;//默认宽高1/2colw

   for (int i=1;i<points.size();i++) {
       Point point = points.get(i);
       GameObject LINE = new GameObject(game);
       LINE.setW(WH);
       LINE.setH(WH);
        
       int dir=0;
       if( point.y-srcJ>0)dir=1;//向右
       if(point.y-srcJ<0) dir=2;////向左
       if(srcI-(point.x-1)>0)dir=3;//向上
       if(srcI-(point.x-1)<0) dir=4;//向下
       //x*y*w应该是两点的距离
       switch (dir){
           case 1:
           {//横右
               LINE.setW(Math.abs(point.y - srcJ) * Column1W +WH/2);
               LINE.setX((int) ( game.getWidth() * 0.025)+srcJ * Column1W +WH);//直线结束端点在右,默认在 结束端点加长一点1/4

               LINE.setY(Column1Y +  (point.x-1) * Column1W+Column1W/4);//是加，横的时候，y 是不用拉伸的
               break;
           }
           case 2:{
               //横左
               LINE.setW(WH/2+Math.abs(point.y - srcJ) * Column1W);
               LINE.setX((int) (game.getWidth() * 0.025+point.y * Column1W+WH/2));//直线结束端点在左

               LINE.setY(Column1Y +  (point.x-1) * Column1W+Column1W/4);//是加，横的时候，y 是不用拉伸的
               break;
           }
           case 3:{
               //纵 上
               LINE.setH(WH+Math.abs( (point.x-1) - srcI) * Column1W);
               LINE.setY(Column1Y+ (point.x-1) * Column1W +WH/2);//直线结束端点在上

               LINE.setX((int) (game.getWidth() * 0.025+ point.y * Column1W +Column1W/4));
               break;
           }
           case 4:{
               //纵下
               LINE.setH(Math.abs( (point.x-1) - srcI) * Column1W+WH/2);
               LINE.setY( Column1Y+srcI * Column1W +WH);//直线结束端点在下

               LINE.setX((int) (game.getWidth() * 0.025+ point.y * Column1W +Column1W/4));
               break;
           }
           default:
               System.out.println("有问题"); break;
       }
       


//       LINE.setBackColor(Color.BLACK);
       LINE.setStyle("image","stretch");
       LINE.setPic( game.getGif("line.gif"));
       line.appendChild(LINE);

       //下次循环
       srcI =  (point.x-1);
       srcJ = point.y;
   }
        new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);//显示效果,不要让主线程睡眠，current之类的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("执行","synchronization");
                LINES.getChildren().remove(line);
                line.Destory();
            }
        }.run();

    }
   

}

