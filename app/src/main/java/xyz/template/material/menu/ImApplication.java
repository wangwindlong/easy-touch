package xyz.template.material.menu;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.yzxIM.IMManager;
import com.yzxtcp.UCSManager;

import xyz.template.material.menu.volley.BitmapLruCache;


/**
 * Created by super on 15-9-2.
 */
public class ImApplication extends Application{
    private static ImApplication sInstance;

    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;


    static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init(this);
        UCSManager.init(this);
        IMManager.getInstance(this);
    }

    public static ImApplication getInstance(Context c) {
        if (sInstance == null) {
            sInstance = (ImApplication) c.getApplicationContext();
        }
        return sInstance;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }


    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache} which effectively means
     * that no memory caching is used. This is useful for images that you know that will be show
     * only once.
     *
     * @return
     */
    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }
}
