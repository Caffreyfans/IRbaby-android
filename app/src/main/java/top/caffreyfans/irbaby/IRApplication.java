package top.caffreyfans.irbaby;

import com.activeandroid.ActiveAndroid;
import net.irext.webapi.WebAPIs;

import org.litepal.LitePal;

import top.caffreyfans.irbaby.helper.UdpReceiveThread;

public class IRApplication extends com.activeandroid.app.Application {

    private static final String TAG = IRApplication.class.getSimpleName();
    private static final String ADDRESS = "https://irext.net";
    private static final String APP_NAME = "/irext-server";

    public WebAPIs mWeAPIs = WebAPIs.getInstance(ADDRESS, APP_NAME);

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize ActiveAndroid
        ActiveAndroid.initialize(this);
        LitePal.initialize(this);


        // login with guest-admin account
        new Thread() {
            @Override
            public void run() {
                mWeAPIs.signIn(IRApplication.this);
            }
        }.start();

        new UdpReceiveThread().start();
    }
}

