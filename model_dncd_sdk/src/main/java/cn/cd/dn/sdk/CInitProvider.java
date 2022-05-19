package cn.cd.dn.sdk;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;

import cn.cd.dn.sdk.models.prints.DNPrint;
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
        //初始化服务器时间同步
        DNServiceTimeManager.Companion.getIns().updateLocalTime();
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
