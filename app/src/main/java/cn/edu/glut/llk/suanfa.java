package cn.edu.glut.llk;
import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.List;

interface LinkInterface<T> {

    public boolean isEmpty();

    public void setEmpty();

    public void setNonEmpty();

    public T getContent();

    public void setContent(T content);
}

class Point {
    public int x;
    public int y;
    public Point(){}
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class  Item implements LinkInterface<Bitmap>{
    boolean Empty=true;
    Bitmap bitmap;
    @Override
    public boolean isEmpty() {
        return Empty;
    }

    @Override
    public void setEmpty() {
this.Empty=true;
    }

    @Override
    public void setNonEmpty() {
Empty=false;
    }

    @Override
    public Bitmap getContent() {
        return bitmap;
    }

    @Override
    public void setContent(Bitmap content) {
        this.bitmap=content;
    }

}
public class suanfa {
CHCanvasGame game;

    public suanfa(CHCanvasGame game) {
        this.game = game;
    }

    public Item[][] main(){
        Bitmap[] bitmaps={game.getImage("animal1.jpg"),
                game.getImage("animal1.jpg"),
                game.getImage("animal2.jpg"),
                game.getImage("animal3.jpg"),
                game.getImage("animal4.jpg"),
                game.getImage("animal5.jpg"),
                game.getImage("animal6.jpg"),
                game.getImage("animal7.jpg"),
                game.getImage("animal8.jpg"),
                game.getImage("animal9.jpg"),
                game.getImage("animal10.jpg"),
                game.getImage("animal11.jpg"),
                game.getImage("animal12.jpg"),
                game.getImage("animal13.jpg"),
                game.getImage("animal14.jpg"),
                game.getImage("animal15.jpg"),
                game.getImage("animal16.jpg"),
                game.getImage("animal17.jpg"),
                game.getImage("animal18.jpg"),
        };

        Item[][] item= new Item[8][6];
        for(int i=0;i<8;i++){
            for(int j=0;j<6;j++){
                item[i][j]=new Item();//初始化
            }
        }
        LinkSearch.generateBoard(item, bitmaps);//好像要自己清空格子，

        return item;
    }

}
//算法静态，不用实例化
class LinkSearch {
    private static <T>  boolean MatchBolck(LinkInterface[][] datas,
                                      final Point srcPt, final Point destPt) {

        // 如果不属于0折连接则返回false
        if(srcPt.x != destPt.x && srcPt.y != destPt.y)
            return false;

        int min, max;

        // 如果两点的x坐标相等，则在水平方向上扫描
        if(srcPt.x == destPt.x) {
            min = srcPt.y < destPt.y ? srcPt.y : destPt.y;
            max = srcPt.y > destPt.y ? srcPt.y : destPt.y;
            for(min++; min < max; min++) {
                if(!datas[srcPt.x][min].isEmpty())
                    return false;
            }
        }
        // 如果两点的y坐标相等，则在竖直方向上扫描
        else {
            min = srcPt.x < destPt.x ? srcPt.x : destPt.x;
            max = srcPt.x > destPt.x ? srcPt.x : destPt.x;
            for(min++; min < max; min++) {
                if(!datas[min][srcPt.y].isEmpty())
                    return false;
            }
        }
        return true;
    }

    private static <T> Point MatchBolckOne(LinkInterface[][] datas,
                                       final Point srcPt, final Point destPt) {
        // 如果不属于1折连接则返回null
        if(srcPt.x == destPt.x || srcPt.y == destPt.y)
            return null;

        // 测试对角点1
        Point pt = new Point(srcPt.x,destPt.y);

        if(datas[pt.x][pt.y].isEmpty()) {
            boolean stMatch = MatchBolck(datas, srcPt, pt);
            boolean tdMatch = stMatch ?
                    MatchBolck(datas, pt, destPt) : stMatch;
            if (stMatch && tdMatch) {
                return pt;
            }
        }

        // 测试对角点2
        pt = new Point(destPt.x, srcPt.y);

        if(datas[pt.x][pt.y].isEmpty()) {
            boolean stMatch = MatchBolck(datas, srcPt, pt);
            boolean tdMatch = stMatch ?
                    MatchBolck(datas, pt, destPt) : stMatch;
            if (stMatch && tdMatch) {
                return pt;
            }
        }
        return null;
    }

    public static <T> List<Point> MatchBolckTwo(LinkInterface[][] datas,
                                            final Point srcPt, final Point destPt) {
        if(datas == null || datas.length == 0)
            return null;

        if(srcPt.x < 0 || srcPt.x > datas.length)
            return null;

        if(srcPt.y < 0 || srcPt.y > datas[0].length)
            return null;

        if(destPt.x < 0 || destPt.x > datas.length)
            return null;

        if(destPt.y < 0 || destPt.y > datas[0].length)
            return null;

        // 判断0折连接
        if(MatchBolck(datas, srcPt, destPt)) {
            return new LinkedList<>();
        }

        List<Point> list = new LinkedList<Point>();
        Point point;

        // 判断1折连接
        if((point = MatchBolckOne(datas, srcPt, destPt)) != null) {
            list.add(point);
            return list;
        }

        // 判断2折连接
        int i;
        for(i = srcPt.y + 1; i < datas[srcPt.x].length; i++) {
            if(datas[srcPt.x][i].isEmpty()) {
                Point src = new Point(srcPt.x, i);
                Point dest = MatchBolckOne(datas, src, destPt);
                if(dest != null) {
                    list.add(src);
                    list.add(dest);
                    return list;
                }
            } else break;
        }

        for(i = srcPt.y - 1; i > -1; i--) {
            if(datas[srcPt.x][i].isEmpty()) {
                Point src = new Point(srcPt.x, i);
                Point dest = MatchBolckOne(datas, src, destPt);
                if(dest != null) {
                    list.add(src);
                    list.add(dest);
                    return list;
                }
            } else break;
        }

        for(i = srcPt.x + 1; i < datas.length; i++) {
            if(datas[i][srcPt.y].isEmpty()) {
                Point src = new Point(i, srcPt.y);
                Point dest = MatchBolckOne(datas, src, destPt);
                if(dest != null) {
                    list.add(src);
                    list.add(dest);
                    return list;
                }
            } else break;
        }

        for(i = srcPt.x - 1; i > -1; i--) {
            if(datas[i][srcPt.y].isEmpty()) {
                Point src = new Point(i, srcPt.y);
                Point dest = MatchBolckOne(datas, src, destPt);
                if(dest != null) {
                    list.add(src);
                    list.add(dest);
                    return list;
                }
            } else break;
        }
        return null;
    }

    public static <T> void generateBoard(LinkInterface<T>[][] datas, T[] optConts) {
        List<Point> list = new LinkedList<>();
        for(int i = 0; i < datas.length; i++) {
            for(int j = 0; j < datas[i].length; j++) {
                if(j==2||j==3)continue;//去掉两列
                list.add(new Point(i, j));//根据datas 的长度生成 列表list point类 为下面的随机做准备
            }
        }
//目前这个算法是全铺满的。先生成嘛，到时候手动 去掉两列               判断图片相同同点击时判断，而不是算法 拦截
        while (list.size() != 0) {
            Point srcPt = list.remove((int)(Math.random() * list.size()));//随机移去list 的一个位置，并返回什么移去的元素
            Point destPt = list.remove((int)(Math.random() * list.size()));//随机移动 所以 第循环一次，移去两个位置 ，填上两个相同的图片元素，保证，成双成对
            LinkInterface<T> src = datas[srcPt.x][srcPt.y];
            LinkInterface<T> dest = datas[destPt.x][destPt.y];//得到棋盘上的相应位置，
            src.setNonEmpty();//位置为不为空，默认是空
            dest.setNonEmpty();
            T t = optConts[(int)(Math.random() * optConts.length)];//填上图片
            src.setContent(t);
            dest.setContent(t);
        }
    }

}