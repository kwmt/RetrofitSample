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
import net.kwmt27.retrofitsample.data.GitHubServiceWithRetrofit
import net.kwmt27.retrofitsample.data.GitHubServiceImpl
import net.kwmt27.retrofitsample.data.GitHubServiceWithoutRetrofit
import retrofit2.Retrofit
import retrofit2.await

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

        val gitHubServiceWithRetrofit: GitHubServiceWithRetrofit =
            Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(MyConverterFactory())
                .build()
                .create(GitHubServiceWithRetrofit::class.java)

        view.findViewById<Button>(R.id.retrofit_button).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val results = gitHubServiceWithRetrofit.listRepos("kwmt").await()
                results.forEach {
                    Log.d(TAG, it.name)
                }
            }
        }

        val serviceWithoutRetrofit: GitHubServiceWithoutRetrofit = GitHubServiceImpl()

        view.findViewById<Button>(R.id.HttpURLConnection_button).setOnClickListener {
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


