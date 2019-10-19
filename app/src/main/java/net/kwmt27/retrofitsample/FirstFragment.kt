package net.kwmt27.retrofitsample

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

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(MyConverterFactory())
                .build()

            val gitHubService = retrofit.create(GitHubService::class.java)
            val repos: Call<List<Repo>> = gitHubService.listRepos("kwmt")

            repos.enqueue(object : retrofit2.Callback<List<Repo>> {
                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    Log.d("GitHubSample", t.message)
                }

                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    Log.d("GitHubSample", response.toString())
                }
            })
        }
    }
}
