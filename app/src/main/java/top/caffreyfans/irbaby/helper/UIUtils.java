package top.caffreyfans.irbaby.helper;

import android.os.Handler;

import top.caffreyfans.irbaby.MainActivity;

public class UIUtils {

    public static int getMainThreadId() {
        return MainActivity.getMainThreadId();
    }

    public static Handler getHandler() {
        return MainActivity.getHandler();
    }

    // 判断是否是主线的方法
    public static boolean isRunInMainThread() {
        return getMainThreadId() == android.os.Process.myTid();
    }

    // 保证当前的UI操作在主线程里面运行
    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            // 如果现在就是在珠现场中，就直接运行run方法
            runnable.run();
        } else {
            // 否则将其传到主线程中运行
            getHandler().post(runnable);
        }
    }


}