package mypractice.com.example.gsoft2_3.myapplication0701;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class SwitchTestActivity extends AppCompatActivity implements SwitchView.Callback {

    private static final String TAG = "SwitchTestActivity";
    private SwitchView mSwitchView;
    private View mCoverView;

    private RelativeLayout relLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        getSupportActionBar().hide();// 隐藏ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        setContentView(R.layout.activity_switch_test);

        mSwitchView = (SwitchView)findViewById(R.id.switch_view);
        mCoverView = (View)findViewById(R.id.cover_view);
        relLayout = (RelativeLayout) findViewById(R.id.activity_switch_test);

        mSwitchView.setPreviewCover(mCoverView);
        mSwitchView.setCallback(this);

        relLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG,"onTouch");
                mSwitchView.onTouch(event);
                return false;
            }
        });
    }

    @Override
    public void OnCameraStartSwitch() {
        //可以做相机摄像头切换动作

    }

    @Override
    public void onAnimaStart() {

    }

    @Override
    public void onAnimaFinish() {

    }
}
