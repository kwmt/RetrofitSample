package net.kwmt27.retrofitsample

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.kwmt27.retrofitsample.data.GitHubService
import net.kwmt27.retrofitsample.data.model.Repo
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.BufferedInputStream
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
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(MyConverterFactory())
                .build()

            val gitHubService = retrofit.create(GitHubService::class.java)
            val repos: Call<List<Repo>> = gitHubService.listRepos("kwmt")

            repos.enqueue(object : retrofit2.Callback<List<Repo>> {
                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    Log.d(TAG, t.message)
                }

                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    Log.d(TAG, response.toString())
                }
            })
        }

        view.findViewById<Button>(R.id.HttpURLConnection_button).setOnClickListener {
            RequestAsyncTask().execute()
        }
    }

    private class RequestAsyncTask : AsyncTask<Unit, Unit, String?>() {
        override fun doInBackground(vararg params: Unit?): String? {
            Log.d(TAG, "doInbackground start")
            val url = URL("https://api.github.com/users/kwmt/repos")
            val urlConnection = url.openConnection() as HttpURLConnection
            var result: String? = null
            try {
                val inputStream = BufferedInputStream(urlConnection.inputStream)
                result = readStream(inputStream)
            } finally {
                urlConnection.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.d(TAG, result)
        }

        private fun readStream(inputStream: BufferedInputStream): String {
            return inputStream.bufferedReader().use {
                it.readText()
            }
        }
    }

    companion object {
        private const val TAG = "GitHubSample"
    }
}
