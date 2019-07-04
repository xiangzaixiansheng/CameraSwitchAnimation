package mypractice.com.example.gsoft2_3.myapplication0701;

/**
 * Created by gsoft2-3 on 19-7-1.
 */

public class TestCallback {

    private Callback mCallback;

    public interface Callback {
        void onDoFirst();
        void onDoSecond();
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public void doSomething() {
        if (mCallback != null) {
            mCallback.onDoFirst();
            mCallback.onDoSecond();
        }
    }

}
