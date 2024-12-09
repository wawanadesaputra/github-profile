package id.ac.polbeng.wawansaputra.githubprofilee.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ac.polbeng.wawansaputra.githubprofilee.models.GithubUser
import id.ac.polbeng.wawansaputra.githubprofilee.MainViewModel
import id.ac.polbeng.wawansaputra.githubprofilee.R
import id.ac.polbeng.wawansaputra.githubprofilee.databinding.ActivityMainBinding
import id.ac.polbeng.wawansaputra.githubprofilee.helpers.Config

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)
        mainViewModel.githubUser.observe(this) { user ->
            setUserData(user)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnSearchUserLogin.setOnClickListener {
            val userLogin = binding.etSearchUserLogin.text.toString()
            var query = Config.DEFAULT_USER_LOGIN
            if (userLogin.isNotEmpty()) {
                query = userLogin
            }
            mainViewModel.searchUser(query)
        }
    }

    private fun setUserData(githubUser: GithubUser?) {
        if (githubUser != null) {
            binding.tvUser.text = githubUser.toString()

            // Log URL to debug
            val avatarUrl = githubUser.avatarUrl
            if (avatarUrl.isNullOrEmpty()) {
                binding.tvUser.text = "User has no avatar"
                Glide.with(applicationContext)
                    .load(R.drawable.baseline_broken_image_24)  // Image fallback
                    .into(binding.imgUser)
            } else {
                Glide.with(applicationContext)
                    .load(avatarUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.baseline_image_24)
                            .error(R.drawable.baseline_broken_image_24))
                    .into(binding.imgUser)
            }
        } else {
            binding.tvUser.text = "User Not Found"
            Glide.with(applicationContext)
                .load(R.drawable.baseline_broken_image_24)
                .into(binding.imgUser)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
