package cn.edu.glut.llk;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.glut.llk.zhu.suanfa.Item;
import cn.edu.glut.llk.zhu.suanfa.LinkSearch;
import cn.edu.glut.llk.zhu.suanfa.Point;
import cn.edu.glut.llk.zhu.suanfa.main;

public class Elimination{
   private final CHCanvasGame game;
   private final Item[][] item;
   private final int Column1Y;
   private final int Column1W;
   private final int LeftMargin;
   GameObject OneObj;
  WeakReference<GameObject> MaskRef;

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
                   if(MainLogical.SoundSwicth) game.playWav(game.getWav("消除.wav"));//消除音效
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
              LINE.setW(Math.abs(point.y - srcJ) * Column1W);
              LINE.setX((int) ( game.getWidth() * 0.025)+srcJ * Column1W +WH);//直线结束端点在右,默认在 结束端点加长一点1/4

              LINE.setY(Column1Y + (point.x-1) * Column1W+Column1W/4);//是加，横的时候，y 是不用拉伸的
              break;
          }
          case 2:{
              //横左
              LINE.setW(Math.abs(point.y - srcJ) * Column1W);
              LINE.setX((int) (game.getWidth() * 0.025+point.y * Column1W+WH));//直线结束端点在左

              LINE.setY(Column1Y +  (point.x-1) * Column1W+Column1W/4);//是加，横的时候，y 是不用拉伸的
              break;
          }
          case 3:{
              //纵 上
              LINE.setH(Math.abs( (point.x-1) - srcI) * Column1W);
              LINE.setY(Column1Y+ (point.x-1) * Column1W +WH);//直线结束端点在上

              LINE.setX((int) (game.getWidth() * 0.025+ point.y * Column1W +Column1W/4));
              break;
          }
          case 4:{
              //纵下
              LINE.setH(Math.abs( (point.x-1) - srcI) * Column1W);
              LINE.setY( Column1Y+srcI * Column1W +WH);//直线结束端点在下

              LINE.setX((int) (game.getWidth() * 0.025+ point.y * Column1W +Column1W/4));
              break;
          }
          default:
              System.out.println("有问题"); break;
      }



//       LINE.setBackColor(Color.BLACK);
      LINE.setStyle("image","stretch");
//       LINE.setPic( game.getGif("line.gif"));
      if(LINE.getW()>LINE.getH())
      LINE.setPic(MainLogical.bitLine);
     else LINE.setPic(MainLogical.bitLine2);
      line.appendChild(LINE);

      //下次循环
      srcI =  (point.x-1);
      srcJ = point.y;
  }
       new Runnable() {
           @Override
           public void run() {
               try {
                   Thread.sleep(400);//显示效果,不要让主线程睡眠，current之类的
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
