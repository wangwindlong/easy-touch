package xyz.template.material.menu;

import android.app.Application;

import com.yzxIM.IMManager;
import com.yzxtcp.UCSManager;

/**
 * Created by super on 15-9-2.
 */
public class ImApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        UCSManager.init(this);
        IMManager.getInstance(this);
    }
}
