package net.kwmt27.retrofitsample.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import net.kwmt27.retrofitsample.data.model.Repo
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

interface GitHubServiceWithoutRetrofit {
    suspend fun listRepos(user: String): List<Repo>
}

class GitHubServiceImpl : GitHubServiceWithoutRetrofit {
    override suspend fun listRepos(user: String): List<Repo> {
        return withContext(Dispatchers.IO) {
            val url = URL("https://api.github.com/users/$user/repos")
            val urlConnection = url.openConnection() as HttpURLConnection
            val result: List<Repo>
            try {
                val inputStream = BufferedInputStream(urlConnection.inputStream)
                result = readStream(inputStream)
            } finally {
                urlConnection.disconnect()
            }
            return@withContext result
        }
    }

    private fun readStream(inputStream: BufferedInputStream): List<Repo> {
        return inputStream.bufferedReader().use {
            convert(it)
        }
    }

    private fun convert(reader: BufferedReader): List<Repo> {
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