package cn.edu.glut.llk.zhu.suanfa;

import android.graphics.Bitmap;

import java.util.List;

public class  Item implements LinkInterface<Bitmap>{
    boolean Empty=true;
 public    Bitmap bitmap;
  public    int  blocksIDI;
  public   int blocksIDJ;
  public   List<Integer> emptyColumn;//更新棋盘的时候用
    public int getBlocksIDI() {
        return blocksIDI;
    }

    public void setBlocksIDI(int blocksIDI) {
        this.blocksIDI = blocksIDI;
    }

    public int getBlocksIDJ() {
        return blocksIDJ;
    }

    public void setBlocksIDJ(int blocksIDJ) {
        this.blocksIDJ = blocksIDJ;
    }



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
