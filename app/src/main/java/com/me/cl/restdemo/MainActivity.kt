package com.me.cl.restdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.me.cl.restdemo.download.DownloadProgressInterceptor
import com.me.cl.restdemo.upload.ProgressRequestBody
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Okio
import java.io.File


class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitBuilder(RetrofitBuilder.JSON_SERVER).Build()?.create(RestService::class.java)?.run {
            getGoods(1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe({
                        Log.e("test", it.toString())
                    }, ::print)
            //convert json string to json object
            postGoods(1, "test").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle()).subscribe()
            search(Gson().fromJson("{\"path\": \"\",\"query\": \"Mat\",\"start\": 0,\"max_results\": 100,\"mode\": \"filename\"}", JsonObject::class.java)).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::print, ::print)
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
            ), 2)
        } else {
            RetrofitBuilder(RetrofitBuilder.BINARY_SERVER).Build()?.create(RestService::class.java)?.run {
                //Upload file without progress
//                val file= File("/sdcard/Download/58client_v8.2.1_3.txt")
//                val requstBody= RequestBody.create(MediaType.parse(getMimeType(file.absolutePath)),file)
//                postFile2(MultipartBody.Part.createFormData("",file.name,requstBody),Gson().toJson(DropBoxArgs().apply {
//                    path="/58client"
//                })).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()).compose(bindToLifecycle())
//                        .subscribe(::print,::print)

                //Upload file with progress
                val file = File("/sdcard/Download/58client_v8.2.1_3.txt")
                val requestBody = ProgressRequestBody(this@MainActivity, "/sdcard/Download/download.txt") { progressInPercent, uploadedBytes, totalBytes ->
                    runOnUiThread {
                        Log.d("upload", "$progressInPercent% ${uploadedBytes / 1024 / 1024}MB/${totalBytes / 1024 / 1024}MB")
                        progressBar.progress = progressInPercent
                    }
                }
                postFile2(MultipartBody.Part.createFormData("", file.name, requestBody), Gson().toJson(UploadArgs().apply {
                    path = "/58client_test"
                })).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).compose(bindToLifecycle())
                        .subscribe(::print, ::print)

            }
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 3)
        } else {
            // download file with progress
            RetrofitBuilder(RetrofitBuilder.BINARY_SERVER).setDownloadProgressListener(DownloadProgressInterceptor.DownloadProgressListener { bytesRead, contentLength, done -> Log.e("download", "${bytesRead / 1024 / 1024}/${contentLength / 1024 / 1024}MB $done") })
                    .Build()?.create(RestService::class.java)?.run {
                        // dynamic construct json object
                        download(JsonObject().apply {
                            addProperty("path", "/58client")
                        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map {
                            Okio.buffer(Okio.sink(File("/sdcard/Download/download.txt"))).run {
                                writeAll(it.source())
                            }
                            true
                        }
                    }?.compose(bindToLifecycle())?.subscribe({ Log.e("dowload-end", "success") }, ::print)
        }
    }

    fun getMimeType(absolutePath: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(absolutePath)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                RetrofitBuilder(RetrofitBuilder.BINARY_SERVER).Build()?.create(RestService::class.java)?.run {
                    val file = File("/sdcard/Download/1200px-Macaca_sinica_-_01.jpg")
                    val requstBody = RequestBody.create(MediaType.parse(getMimeType(file.absolutePath)), file)
                    postFile2(MultipartBody.Part.createFormData("picture", file.name, requstBody), Gson().toJson(UploadArgs())).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe(::print, ::print)
                }
            } else {
                // Permission Denied
            }
        }
    }
}
