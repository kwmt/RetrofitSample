package net.kwmt27.retrofitsample

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import net.kwmt27.retrofitsample.data.model.Repo
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.BufferedReader
import java.lang.reflect.Type

class MyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return MyResponseBodyConverter()
    }

    class MyResponseBodyConverter : Converter<ResponseBody, List<Repo>> {
        @UnstableDefault
        override fun convert(value: ResponseBody): List<Repo>? {
            val reader = BufferedReader(value.byteStream().reader())
            val content = StringBuilder()
            reader.use {
                var line = it.readLine()
                while (line != null) {
                    content.append(line)
                    line = it.readLine()
                }
            }

            val json = Json { strictMode = false }
            return json.parse(Repo.serializer().list, content.toString())
        }
    }
}
