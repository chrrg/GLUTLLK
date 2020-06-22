package cn.edu.glut.llk;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){Window window = getWindow();window.setStatusBarColor(Color.WHITE);}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

            // activity 可视化 onCreate 之后执行
            super.onWindowFocusChanged(hasFocus);
            if (hasFocus) {
                ///////////////////////////////////////////////////////
                CHCanvasGame game = new CHCanvasGame();//初始化游戏引擎
                game.init(this, R.id.canvas);//设置参数
                Log.d("onWindowFocusChanged", "width : not return 0" );
            }

        }

}