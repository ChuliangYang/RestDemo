package com.me.cl.restdemo

import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by CL on 7/17/18.
 */
interface RestService {
    @GET("goods/{id}")
    fun getGoods(@Path("id") id:Int):Observable<List<POJO>>

    @FormUrlEncoded
    @POST("goods/{id}")
    fun postGoods(@Path("id") id:Int,@Field("test") test:String):Observable<POJO>

//    @Headers(
//            "Authorization: Bearer FW_IUZfXnpAAAAAAAAAAUQiEsRgQfye3IUlzcigbFed8p_-oe27d2QotMAnAhaO4",
//            "Dropbox-API-Arg: {\"close\": false}",
//            "Content-Type: application/octet-stream"
//    )
//    @Multipart
//    @POST("https://content.dropboxapi.com/2/files/upload_session/start")
//    fun postFile(@Part file1:  MultipartBody.Part):Observable<ResponseBody>

    @Headers(
            "Authorization: Bearer FW_IUZfXnpAAAAAAAAAAUQiEsRgQfye3IUlzcigbFed8p_-oe27d2QotMAnAhaO4"
    )
    @POST("https://api.dropboxapi.com/2/files/search")
    fun search(@Body json: JsonObject):Observable<ResponseBody>

    @Headers(
            "Content-Type: application/octet-stream"
    )
    @Multipart
    @POST("https://content.dropboxapi.com/2/files/upload")
    fun postFile2(@Part file1:  MultipartBody.Part, @Header("Dropbox-API-Arg") header:String):Observable<ResponseBody>

//    @Headers(
//            "Dropbox-API-Arg: {\"path\": \"/58client\"}"
//    )
    @POST("2/files/download")
    @Streaming
    fun download(@Header("Dropbox-API-Arg") header:JsonObject):Observable<ResponseBody>


}