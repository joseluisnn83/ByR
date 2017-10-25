package com.joseluisnn;

import android.app.Application;
import com.joseluisnn.handler.MyAsyncktaskManager;

/**
 * Created by joseluisnn83 on 29/06/15.
 */
public class TTApplication extends Application {

    private MyAsyncktaskManager mAsyncktaskManager;

    /**
     * The singleton instance of TT App
     */
    private static TTApplication sInstance = null;

    /**
     * App instance
     */
    public TTApplication() {
        sInstance = this;
    }

    /**
     * Get the instance of TTApplication
     */
    public static TTApplication getInstance() {
        return sInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mAsyncktaskManager = new MyAsyncktaskManager();
    }

    public MyAsyncktaskManager getMyAsyncktask(){
        return mAsyncktaskManager;
    }

}
