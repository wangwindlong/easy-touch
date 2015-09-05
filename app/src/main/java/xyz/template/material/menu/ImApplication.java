package xyz.template.material.menu;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.yzxIM.IMManager;
import com.yzxtcp.UCSManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.LogManager;

import xyz.template.material.menu.volley.BitmapLruCache;

import static xyz.template.material.menu.utils.LogUtils.LOGD;
import static xyz.template.material.menu.utils.LogUtils.makeLogTag;


/**
 * Created by super on 15-9-2.
 */
public class ImApplication extends Application{
    private static final String TAG = makeLogTag(ImApplication.class);
    private static ImApplication sInstance;
    private final ArrayList<Object> registeredManagers;
    /**
     * Thread to execute tasks in background..
     */
    private final ExecutorService backgroundExecutor;
    /**
     * Handler to execute runnable in UI thread.
     */
    private final Handler handler;
    /**
     * Unmodifiable collections of managers that implement some common
     * interface.
     */
    private boolean serviceStarted;
    /**
     * Whether application was initialized.
     */
    private boolean initialized;
    /**
     * Whether user was notified about some action in contact list activity
     * after application initialization.
     */
    private boolean notified;
    /**
     * Whether application is to be closed.
     */
    private boolean closing;
    /**
     * Whether {@link #onServiceDestroy()} has been called.
     */
    private boolean closed;
    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        }

    };
    /**
     * Future for loading process.
     */
    private Future<Void> loadFuture;


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


    public ImApplication() {
        sInstance = this;
        serviceStarted = false;
        initialized = false;
        notified = false;
        closing = false;
        closed = false;
        registeredManagers = new ArrayList<>();

        handler = new Handler();
        backgroundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "Background executor service");
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    @Override
    public void onCreate() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        init(this);
        UCSManager.init(this);
        IMManager.getInstance(this);
        super.onCreate();
    }

    public static ImApplication getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException();
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



    /**
     * Whether application is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
}
