package cn.edu.glut.llk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;
class GameObject{
    public void setPic(){

    }
}
class GameCamera{
    private int x;
    private int y;
    int cameraX;
    int cameraY;
    GameCamera(){

    }
    GameCamera(int x,int y){
        this.x=x;
        this.y=y;
    }
}
class CHCanvasGame {
    private GameCamera camera;
    CHCanvasGame(){

    }
    void setCamera(GameCamera c){
        this.camera=c;
    }
    void init(Activity self, int id){
        final Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        final ImageView image = self.findViewById(id);
        image.post(new Runnable() {
            @Override
            public void run() {
                RectF r = new RectF(200, 200, 400, 500);
                Bitmap baseBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(baseBitmap);
                canvas.drawRect(r, paint);
            }
        });
    }

}
