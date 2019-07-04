package mypractice.com.example.gsoft2_3.myapplication0701;

/**
 * Created by gsoft2-3 on 19-7-2.
 */

public class LocationIconCallback {
    private static Callback mCallback;

    public interface Callback {
        //搜索中动画
        void startLocSeachIcon();
        //搜索到动画
        void SearchedLocIcon();
        //关闭动画
        void stopLocIcon();
    }

    public static void startLocSeachIcon(){
        mCallback.startLocSeachIcon();
    }

    public static void SearchedLocIcon() {
        mCallback.SearchedLocIcon();
    }

    public static void stopLocIcon(){
        mCallback.stopLocIcon();

    }

    public static void setCallback(Callback Callback) {
        mCallback = Callback;
    }

}
