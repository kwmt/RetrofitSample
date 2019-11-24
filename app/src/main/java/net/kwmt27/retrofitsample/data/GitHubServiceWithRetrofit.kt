package net.kwmt27.retrofitsample.data

import net.kwmt27.retrofitsample.data.model.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubServiceWithRetrofit {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>
}