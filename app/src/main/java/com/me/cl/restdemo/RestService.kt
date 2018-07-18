package com.me.cl.restdemo

import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by CL on 7/17/18.
 */
interface RestService {
    @GET("goods/{id}")
    fun getGoods(@Path("id") id:Int):Observable<List<Bean>>

    @FormUrlEncoded
    @POST("goods/{id}")
    fun postGoods(@Path("id") id:Int,@Field("test") test:String):Observable<Bean>
}