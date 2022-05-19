package cn.cd.dn.sdk.kexts

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 切换线程,子线程到主线程的切换(场景一般再网络请求)
 * @receiver Observable<T>
 * @return Observable<T>
 */
fun <T> Flowable<T>.ioToMainScheduler(): Flowable<T> =
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())