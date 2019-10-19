package net.kwmt27.retrofitsample

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class MyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return MyResponseBodyConverter()
    }

    class MyResponseBodyConverter : Converter<ResponseBody, String> {
        override fun convert(value: ResponseBody): String? {
            return "test"
        }
    }
}
