package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

public class CHCanvasGame {
//    private static CHCanvasGame self=null;
//    public static CHCanvasGame getInstance(){//得到单一实例
//        if(self==null)self=new CHCanvasGame();
//        return self;
//    }
    CHCanvasGame(){

    }
    void init(Activity self, int id){
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        ImageView image = self.findViewById(id);
        RectF r = new RectF(200, 200, 400, 500);
        Bitmap baseBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(baseBitmap);
        canvas.drawRect(r, paint);
    }

}
