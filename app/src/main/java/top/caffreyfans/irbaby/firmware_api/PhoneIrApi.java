package top.caffreyfans.irbaby.firmware_api;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.irext.webapi.WebAPIs;
import net.irext.webapi.bean.ACStatus;
import net.irext.webapi.model.RemoteIndex;
import net.irext.webapi.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.caffreyfans.irbaby.IRApplication;
import top.caffreyfans.irbaby.model.ApplianceInfo;

public class PhoneIrApi {

    public interface Callback {
        void onSuccess();
        void onError(String message);
    }

    private static final int DEFAULT_FREQUENCY = 38000;
    private static final int MIN_FREQUENCY = 20000;
    private static final int MAX_FREQUENCY = 60000;

    private final ConsumerIrManager mConsumerIrManager;
    private final WebAPIs mWebAPIs;
    private final Handler mMainHandler;
    private final Map<String, Integer> mRemoteIndexCache;

    public PhoneIrApi(Context context) {
        Context appContext = context.getApplicationContext();
        mConsumerIrManager = (ConsumerIrManager) appContext.getSystemService(Context.CONSUMER_IR_SERVICE);
        mWebAPIs = ((IRApplication) appContext).mWeAPIs;
        mMainHandler = new Handler(Looper.getMainLooper());
        mRemoteIndexCache = new HashMap<>();
    }

    public static boolean hasIrEmitter(Context context) {
        if (context == null) {
            return false;
        }
        ConsumerIrManager consumerIrManager = (ConsumerIrManager) context.getApplicationContext()
                .getSystemService(Context.CONSUMER_IR_SERVICE);
        return consumerIrManager != null && consumerIrManager.hasIrEmitter();
    }

    public boolean hasIrEmitter() {
        return mConsumerIrManager != null && mConsumerIrManager.hasIrEmitter();
    }

    public void sendAcSignal(final ApplianceInfo applianceInfo,
                             final ACStatus acStatus,
                             final Constants.ACFunction function,
                             final boolean changeWindDirection,
                             final Callback callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (!hasIrEmitter()) {
                    postError(callback, "当前手机不支持红外发射");
                    return;
                }
                if (applianceInfo == null || acStatus == null || function == null) {
                    postError(callback, "红外参数不完整");
                    return;
                }

                int remoteIndexId = resolveRemoteIndexId(applianceInfo);
                if (remoteIndexId <= 0) {
                    postError(callback, "未找到对应的空调码库");
                    return;
                }

                int[] decodedSignal = mWebAPIs.decodeIR(remoteIndexId, acStatus,
                        function.getValue(), changeWindDirection ? 1 : 0);
                IrCommand irCommand = toIrCommand(decodedSignal);
                if (irCommand == null) {
                    postError(callback, "空调红外码解码失败");
                    return;
                }

                try {
                    mConsumerIrManager.transmit(irCommand.frequency, irCommand.pattern);
                    postSuccess(callback);
                } catch (RuntimeException e) {
                    postError(callback, "手机红外发射失败");
                }
            }
        });
    }

    private int resolveRemoteIndexId(ApplianceInfo applianceInfo) {
        if (applianceInfo == null
                || applianceInfo.getCategory() <= 0
                || applianceInfo.getBrand() <= 0
                || TextUtils.isEmpty(applianceInfo.getFile())) {
            return -1;
        }

        String cacheKey = buildCacheKey(applianceInfo);
        Integer cachedId = mRemoteIndexCache.get(cacheKey);
        if (cachedId != null) {
            return cachedId;
        }

        List<RemoteIndex> remoteIndexes = mWebAPIs.listRemoteIndexes(applianceInfo.getCategory(),
                applianceInfo.getBrand(), null, null);
        if (remoteIndexes == null) {
            return -1;
        }

        for (RemoteIndex remoteIndex : remoteIndexes) {
            if (applianceInfo.getFile().equals(remoteIndex.getRemoteMap())) {
                mRemoteIndexCache.put(cacheKey, remoteIndex.getId());
                return remoteIndex.getId();
            }
        }
        return -1;
    }

    private String buildCacheKey(ApplianceInfo applianceInfo) {
        return applianceInfo.getCategory() + ":"
                + applianceInfo.getBrand() + ":"
                + applianceInfo.getFile();
    }

    private IrCommand toIrCommand(int[] decodedSignal) {
        if (decodedSignal == null || decodedSignal.length == 0) {
            return null;
        }

        int frequency = DEFAULT_FREQUENCY;
        int startIndex = 0;
        if (decodedSignal.length > 1
                && decodedSignal[0] >= MIN_FREQUENCY
                && decodedSignal[0] <= MAX_FREQUENCY) {
            frequency = decodedSignal[0];
            startIndex = 1;
        }

        if (decodedSignal.length - startIndex <= 0) {
            return null;
        }

        int[] pattern = new int[decodedSignal.length - startIndex];
        for (int i = startIndex; i < decodedSignal.length; i++) {
            if (decodedSignal[i] <= 0) {
                return null;
            }
            pattern[i - startIndex] = decodedSignal[i];
        }
        return new IrCommand(frequency, pattern);
    }

    private void postSuccess(final Callback callback) {
        if (callback == null) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess();
            }
        });
    }

    private void postError(final Callback callback, final String message) {
        if (callback == null) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(message);
            }
        });
    }

    private static class IrCommand {
        private final int frequency;
        private final int[] pattern;

        private IrCommand(int frequency, int[] pattern) {
            this.frequency = frequency;
            this.pattern = pattern;
        }
    }
}