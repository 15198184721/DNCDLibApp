package cn.cd.dn.sdk.kexts

import androidx.lifecycle.LifecycleOwner
import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 切换线程,发送=子线程，订阅=主线程的切换(场景一般再网络请求)
 * @receiver Observable<T>
 * @return Observable<T>
 */
fun <T> Observable<T>.ioToMainScheduler(): Observable<T> =
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

/**
 * 切换线程,发送=子线程，订阅=主线程的切换,并且绑定生命周期(场景一般再网络请求)
 * @receiver Observable<T>
 * @return Observable<T>
 */
fun <T> Observable<T>.ioToMainSchedulerBindLifecycle(owner: LifecycleOwner): Observable<T> =
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .compose(
            AndroidLifecycle.createLifecycleProvider(owner)
                .bindToLifecycle<T>() as ObservableTransformer<T, T>
        )

/**
 * 发起订阅
 */
fun <T> Observable<T>.subscribeHttp(succ: (T) -> Unit, err: (Throwable) -> Unit = {}) =
    subscribe({
        succ.invoke(it)
    }, {
        err.invoke(it)
    })


