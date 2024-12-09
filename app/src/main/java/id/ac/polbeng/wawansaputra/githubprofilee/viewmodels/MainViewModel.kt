package id.ac.polbeng.wawansaputra.githubprofilee

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.polbeng.wawansaputra.githubprofilee.helpers.Config
import id.ac.polbeng.wawansaputra.githubprofilee.models.GithubUser
import id.ac.polbeng.wawansaputra.githubprofilee.services.GithubUserService
import id.ac.polbeng.wawansaputra.githubprofilee.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    companion object {
        val TAG: String = MainViewModel::class.java.simpleName
    }

    private val _githubUser = MutableLiveData<GithubUser?>()
    val githubUser: LiveData<GithubUser?> = _githubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val defaultUser = Config.DEFAULT_USER_LOGIN
        if (defaultUser.isNullOrEmpty()) {
            Log.e(TAG, "Login pengguna default kosong atau null")
        } else {
            searchUser(defaultUser)  // Mencari pengguna default saat pertama kali
        }
    }

    fun searchUser(query: String) {
        // Pastikan query tidak kosong atau null
        if (query.isEmpty()) {
            Log.e(TAG, "Query pencarian tidak boleh kosong")
            return
        }

        _isLoading.value = true
        Log.d(TAG, "getDataUserProfileFromAPI: mulai untuk query: $query")

        val githubUserService: GithubUserService = ServiceBuilder.buildService(GithubUserService::class.java)
        val requestCall: Call<GithubUser> = githubUserService.loginUser(Config.PERSONAL_ACCESS_TOKEN, query)
        Log.d(TAG, "URL Request: ${requestCall.request().url}")

        requestCall.enqueue(object : Callback<GithubUser> {
            override fun onResponse(call: Call<GithubUser>, response: Response<GithubUser>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        Log.d(TAG, "getDataUserFromAPI: onResponse sukses: $result")
                        _githubUser.postValue(result)
                    } else {
                        Log.d(TAG, "getDataUserFromAPI: onResponse sukses tetapi body null")
                        _githubUser.postValue(null)
                    }
                } else {
                    Log.e(TAG, "getDataUserFromAPI: onResponse gagal dengan kode: ${response.code()}")
                    _githubUser.postValue(null)
                }
            }

            override fun onFailure(call: Call<GithubUser>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "getDataUserFromAPI: onFailure ${t.message}")
                _githubUser.postValue(null)
            }
        })
    }
}
