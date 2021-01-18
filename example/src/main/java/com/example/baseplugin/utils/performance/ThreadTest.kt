package com.example.baseplugin.utils.performance

/**
 * 线程性能测试
 *
 */
class ThreadTest {


    fun test(){
        //
//        Thread{
//            while (true){
//                Performance.startCountTime("21")
//                Performance.startCountTime("23")
//                Observable.create<String>(object : ObservableOnSubscribe<String> {
//                    override fun subscribe(emitter: ObservableEmitter<String>?) {
//                        LogUtils.e("RxJava main-child 线程切换  ：  "+Performance.endCountTime("21"))
//                        Performance.startCountTime("22")
//                        emitter!!.onNext("")
//                    }
//                }).subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .subscribe(object : Observer<String> {
//                        override fun onSubscribe(d: Disposable?) {
//                        }
//
//                        override fun onNext(t: String?) {
//                            LogUtils.e("RxJava child-main 线程切换   ：  "+Performance.endCountTime("22"))
//                        }
//
//                        override fun onError(e: Throwable?) {
//                        }
//
//                        override fun onComplete() {
//                        }
//                    })
//                LogUtils.e("RxJava 创建线程  ：  "+Performance.endCountTime("23"))
//
//
//                Performance.startCountTime("01")
//                Performance.startCountTime("03")
//                CoroutineUtils.launchIO {
//                    LogUtils.e("协程 main-child 线程切换  ：  "+Performance.endCountTime("01"))
//
//                    Performance.startCountTime("02")
//                    CoroutineUtils.launchMain {
//                        LogUtils.e("协程 child-main 线程切换   ：  "+Performance.endCountTime("02"))
//                    }
//                }
//                LogUtils.e("协程 创建线程  ：  "+Performance.endCountTime("03"))
//                Thread.sleep(2000)
//            }
//        }.start()
//        Thread{
//            Looper.prepare()
//
//
//            Performance.startCountTime("11")
//            Performance.startCountTime("13")
//            Thread {
//                LogUtils.e("Thread main-child 线程切换    ：  "+Performance.endCountTime("11"))
//                var handle = object: Handler(Looper.getMainLooper(),object :Callback{
//                    override fun handleMessage(msg: Message): Boolean {
//                        LogUtils.e("Thread child-main 线程切换  ：  "+Performance.endCountTime("12"))
//                        return false
//                    }
//                }){
//                }
//                Performance.startCountTime("12")
//                handle.sendEmptyMessage(0)
//            }
//                .start()
//            LogUtils.e("Thread 创建线程  ：  "+Performance.endCountTime("13"))
//
//            Looper.loop()
//        }.start()
    }
}