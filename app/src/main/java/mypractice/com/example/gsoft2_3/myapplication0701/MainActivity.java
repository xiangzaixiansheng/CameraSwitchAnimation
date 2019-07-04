package mypractice.com.example.gsoft2_3.myapplication0701;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements LocationIconCallback.Callback {

    private static final String TAG = "MainActivity";
    public ImageView locimg;
    public boolean status = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locimg = (ImageView) findViewById(R.id.loc_status_icon);

        LocationIconCallback.setCallback(this);

    }

    //点击开始动画
    public void onTouchCall(View v) {
        if (status == false){
            LocationIconCallback.startLocSeachIcon();
            status = true;
        } else {
            LocationIconCallback.SearchedLocIcon();
            status = false;
        }
    }

    //跳转第二个滑动测试Ａｃｔｉｖｉｔｙ
    public void onIntentSecActivity(View v) {

        Intent intent = new Intent(MainActivity.this, SwitchTestActivity.class);
        startActivity(intent);
    }

    @Override
    public void startLocSeachIcon() {
         locimg.setImageResource(R.drawable.location_loading);
         AnimationDrawable anim = (AnimationDrawable) locimg.getDrawable();
         anim.start();
    }

    @Override
    public void SearchedLocIcon() {
        locimg.setImageResource(R.drawable.location_loading_done);
        AnimationDrawable anim = (AnimationDrawable) locimg.getDrawable();
        anim.start();
    }

    @Override
    public void stopLocIcon() {

    }
}
