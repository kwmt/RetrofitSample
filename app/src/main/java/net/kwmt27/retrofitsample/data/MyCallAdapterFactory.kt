package net.kwmt27.retrofitsample.data

import android.content.res.Resources
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MyCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val responseType =
            getParameterUpperBound(0, returnType as ParameterizedType)
        return MyCallAdapter<Any>(responseType)
    }
}

interface MyCall<R> {
    fun execute(): Response<R>
}

class MyCallAdapter<R>(
    private val responseType: Type
) : CallAdapter<R, MyCall<R>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): MyCall<R> {
        return object : MyCall<R> {
            override fun execute(): Response<R> {
                val response = call.execute()
                val code = response.code()
                // HTTPステータスコードでエラーハンドリングする
                return call.execute()
            }
        }
    }
}
