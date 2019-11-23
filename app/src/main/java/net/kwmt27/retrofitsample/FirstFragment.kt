package net.kwmt27.retrofitsample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import net.kwmt27.retrofitsample.data.GitHubService
import net.kwmt27.retrofitsample.data.model.Repo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.await
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultTextView = view.findViewById<TextView>(R.id.textview_first)

        view.findViewById<Button>(R.id.retrofit_button).setOnClickListener {
            val gitHubService = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(MyConverterFactory())
                .build()
                .create(GitHubService::class.java)

            lifecycleScope.launch(Dispatchers.IO) {
                val results = gitHubService.listRepos("kwmt").await()
                results.forEach {
                    Log.d(TAG, it.name)
                }
            }
//
//            repos.enqueue(object : Callback<List<Repo>> {
//                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
//                    Log.d(TAG, t.message)
//                }
//
//                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
//                    Log.d(TAG, response.toString())
//                }
//            })
        }

        view.findViewById<Button>(R.id.HttpURLConnection_button).setOnClickListener {
            val serviceWithoutRetrofit: GitHubServiceWithoutRetrofit = GitHubServiceImpl()
            lifecycleScope.launch {
                val results = serviceWithoutRetrofit.listRepos("kwmt")
                results.forEach {
                    Log.d(TAG, it.name)
                }
            }
        }
    }

    companion object {
        private const val TAG = "GitHubSample"
    }
}

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
