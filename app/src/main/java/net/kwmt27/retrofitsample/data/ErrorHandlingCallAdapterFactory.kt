package net.kwmt27.retrofitsample.data

import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

// https://github.com/square/retrofit/blob/master/samples/src/main/java/com/example/retrofit/ErrorHandlingAdapter.java
class ErrorHandlingCallAdapterFactory : Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Call must have generic type (e.g., Call<ResponseBody>)"
            )
        }
        val responseType = getParameterUpperBound(0, returnType)
        return ErrorHandlingCallAdapter<Any>(responseType = responseType)
    }

    // CallからCallに変換しているが、変換中にエラーハンドリングを行っている
    private class ErrorHandlingCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Call<R>> {
        override fun responseType() = responseType

        override fun adapt(call: Call<R>): Call<R> {
            return object : Call<R> {
                override fun isExecuted(): Boolean {
                    return call.isExecuted
                }

                override fun clone(): Call<R> {
                    return call.clone()
                }

                override fun isCanceled(): Boolean {
                    return call.isCanceled
                }

                override fun cancel() {
                    call.cancel()
                }

                override fun execute(): Response<R> {
                    return call.execute()
                }

                override fun request(): Request {
                    return call.request()
                }

                override fun enqueue(callback: Callback<R>) {

                    call.enqueue(object : Callback<R> {
                        override fun onFailure(call: Call<R>, t: Throwable) {
                            callback.onFailure(call, t)
                        }

                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            callback.onResponse(call, response)
                        }
                    })
                }
            }
        }

//        private val errorResponseJsonAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
//        private val errorMessagesResponseJsonAdapter = Moshi.Builder().build().adapter(ErrorResponseMessages::class.java)
//
//        // ErrorResponse
//        private fun Response<R>.toErrorResponse(): ErrorResponse? {
//            val source = this.errorBody()?.source() ?: return null
//            return errorResponseJsonAdapter.fromJson(source)
//        }
//
//        private fun Response<R>.toMessages(): List<String> {
//            return this.toErrorResponse()?.data ?: emptyList()
//        }
//
//        // ErrorResponseMessages
//        private fun Response<R>.toErrorResponseMessage(): ErrorResponseMessages? {
//            val source = this.errorBody()?.source() ?: return null
//            return errorMessagesResponseJsonAdapter.fromJson(source)
//        }
//
//        private fun Response<R>.toErrorMessage(): List<String> {
//            return this.toErrorResponseMessage()?.errorMessage?.let {
//                it
//            } ?: emptyList()
//        }
    }
}