package com.me.cl.restdemo

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restService=RetrofitClient.instance.create(RestService::class.java)
        restService.run {
//            getGoods(1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                    .compose(bindToLifecycle())
//                    .subscribe({
//                Log.e("test",it.toString())
//            },::print)

            postGoods(1,"test").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle()).subscribe()
        }
    }
}
