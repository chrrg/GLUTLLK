package cn.edu.glut.llk.zhu.suanfa;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.edu.glut.llk.CHCanvasGame;

public class main {//算法静态化，不用实例化
  private static Vector<ArrayList<Bitmap>> bitmaps;
  private static int blocksType;
   static ArrayList<Bitmap> getBlocksImage(CHCanvasGame game, int pathRandom) throws IOException {
        if(bitmaps==null ) {//单例

           Log.e("one","只运行一次");
            String[] pathName={"blocks","FlowerPattern","tree"};
            Vector<ArrayList<Bitmap>> vbs=new Vector<>();

            for(int x=0;x<pathName.length;x++){
                ArrayList<Bitmap>   bs = new ArrayList<>();
                String[] files = game.getActivity().getAssets().list(pathName[x]);
                assert files != null;
                Log.e("图片个数","长度为"+files.length);
                for (int i = 0; i < files.length; i++) {
                    Bitmap bitmap = BitmapFactory.decodeStream(game.getActivity().getAssets().open(pathName[x] + "/" + files[i]));
                    bs.add(bitmap);
                }
                vbs.add(bs);
            }

             bitmaps=vbs;//应该没问题
        }
        if(pathRandom<bitmaps.size())
       {    if(pathRandom==-1){
               blocksType=(int) (Math.random() *bitmaps.size());
               return bitmaps.get(blocksType);
            }//随机返回
           blocksType=pathRandom;
           return bitmaps.get(pathRandom);
       }
        else return null;
    }
    public static Item[][] init(CHCanvasGame game, int pathRandom, int Row, int Column, List<Integer> emptyColumn){
        //不要有重复的图片

        Row+=2;//行多两行

        Item[][] item= new Item[Row][Column];
        for(int i=0;i<Row;i++){
            for(int j=0;j<Column;j++){
                item[i][j]=new Item();//初始化
            }
        }
        item[0][0].emptyColumn=emptyColumn;
        try {
            LinkSearch.generateBoard(item, getBlocksImage(game,pathRandom),emptyColumn);//好像要自己清空格子，
        } catch (IOException e) {
            e.printStackTrace();
        }

        return item;
    }
public static void updateChessBoard(Item[][] items){
    Item[] temp = new Item[items[1].length];//长度一样的

    for (int j = 0; j < items[1].length; j++) {
        temp[j] = items[items.length - 2][j];//减二
        if (items[0][0].emptyColumn.contains(j + 1)) temp[j].setEmpty();
        else {
            temp[j].bitmap = bitmaps.get(blocksType).get((int) (Math.random() * bitmaps.size()));
            temp[j].setNonEmpty();//不为空

        }
    }

    for (int i = items.length - 2; i > 0; i--) {
        items[i] = items[i - 1];//下移
    }

    items[1] = temp;
}
}


