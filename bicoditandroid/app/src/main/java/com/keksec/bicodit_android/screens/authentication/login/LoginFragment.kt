package com.keksec.bicodit_android.screens.authentication.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.screens.authentication.AuthenticationActivity
import com.keksec.bicodit_android.screens.authentication.NavListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject

class LoginFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    private var listener: NavListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        listener = activity as AuthenticationActivity?
        super.onCreate(savedInstanceState)

        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        val staticImage = view.findViewById<ImageView>(R.id.loginBicorabbit)
        val animationView =
            view.findViewById<GifImageView>(R.id.loginLoading)

        staticImage.visibility = View.VISIBLE
        animationView.visibility = View.INVISIBLE

        // Setup observers
        loginViewModel.loginValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.loginError == null) {
                    loginLoginTxtLayout.error = null
                } else {
                    loginLoginTxtLayout.error = getString(it.loginError)
                }
                if (it.passwordError == null) {
                    loginPasswordTxtLayout.error = null
                } else {
                    loginPasswordTxtLayout.error = getString(it.passwordError)
                }
            })

        fun showLoading() {
            staticImage.visibility = View.GONE
            animationView.visibility = View.VISIBLE

            loginLoginBtn.isEnabled = false
            loginLoginTxt.isEnabled = false
            loginPasswordTxt.isEnabled = false
        }

        fun stopLoading() {
            staticImage.visibility = View.VISIBLE
            animationView.visibility = View.GONE

            loginLoginBtn.isEnabled = true
            loginLoginTxt.isEnabled = true
            loginPasswordTxt.isEnabled = true
        }

        loginViewModel.userLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showLoginFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        listener?.navigateToMainActivity()
                    }
                }
            })

        // Create listeners
        val afterLoginTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginChanged(loginLoginTxt.text.toString())
            }
        }

        val afterPasswordTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.passwordChanged(loginPasswordTxt.text.toString())
            }
        }

        fun loginBtnClickedListener() {
            lifecycleScope.launch(Dispatchers.IO) {
                loginViewModel.loginUser(
                    loginLoginTxt.text.toString(),
                    loginPasswordTxt.text.toString()
                )
            }
        }

        fun transitionToRegClickedListener() {
            val action = LoginFragmentDirections
                .actionLoginFragmentToRegistrationFragment()
            NavHostFragment.findNavController(this@LoginFragment)
                .navigate(action)
        }

        // Setup listeners
        loginLoginTxt.addTextChangedListener(afterLoginTextChangedListener)
        loginPasswordTxt.addTextChangedListener(afterPasswordTextChangedListener)
        loginLoginBtn.setOnClickListener {
            loginBtnClickedListener()
        }
        loginTransitionToRegBtn.setOnClickListener {
            transitionToRegClickedListener();
        }
    }

    private fun showLoginFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}

