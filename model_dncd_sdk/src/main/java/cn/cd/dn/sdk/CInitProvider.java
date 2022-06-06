package cn.cd.dn.sdk;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chat_hook.HookMethodHelper;

import cn.cd.dn.sdk.models.events.DynamicEventsManager;
import cn.cd.dn.sdk.models.netowrks.DNOkHttpInit;
import cn.cd.dn.sdk.models.prints.DNPrint;
import cn.cd.dn.sdk.models.utils.ClassLoaderManager;
import cn.cd.dn.sdk.models.utils.DNServiceTimeManager;

/**
 * @author lcl
 * Date on 2022/3/19
 * Description:
 */
public class CInitProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        DNPrint.INSTANCE.logE(" ----> dn_model init start !!!!!!");
        // 初始化参数
        DNSdkConfig.Companion.getIns().appContext = getContext();
        DNSdkConfig.Companion.getIns().setAppIsDebug(AppUtils.isAppDebug());

        //初始化框架拦截相关的
        HookMethodHelper.INSTANCE.init(BuildConfig.DEBUG);
        // 竟可能早的初始化ClassLoader的处理逻辑
        ClassLoaderManager.Companion.getClassLoaderManager().initInterceptClassLoader(getContext());

        //初始化服务器时间同步
        DNServiceTimeManager.Companion.getIns().updateLocalTime();
        //执行需要关联上层的应用相关任务集合
        for (ISyncTheUpperTask iSyncTheUpperTask : DNSdkConfig.Companion.getIns().getSyncTheUpperTaskList()) {
            iSyncTheUpperTask.sync(DNSdkConfig.Companion.getIns().appContext);
        }
        DNPrint.INSTANCE.logE(" ----> dn_model init finish !!!!!!");
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
