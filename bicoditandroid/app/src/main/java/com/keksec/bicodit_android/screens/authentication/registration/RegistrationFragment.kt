package com.keksec.bicodit_android.screens.authentication.registration

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
import androidx.navigation.fragment.NavHostFragment
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.screens.authentication.login.LoginViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_registration.*
import timber.log.Timber
import javax.inject.Inject

class RegistrationFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationViewModel = ViewModelProvider(this, viewModelFactory).get(RegistrationViewModel::class.java)

        val staticImage = view.findViewById<ImageView>(R.id.registrationBicorabbit)
        val animationView =
            view.findViewById<pl.droidsonroids.gif.GifImageView>(R.id.registrationLoading)

        staticImage.visibility = View.VISIBLE
        animationView.visibility = View.INVISIBLE

        registrationViewModel.registrationValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.emailError == null) {
                    registrationEmailTxtLayout.error = null
                } else {
                    registrationEmailTxtLayout.error = getString(it.emailError)
                }
                if (it.loginError == null) {
                    registrationLoginTxtLayout.error = null
                } else {
                    registrationLoginTxtLayout.error = getString(it.loginError)
                }
                if (it.passwordError == null) {
                    registrationPasswordTxtLayout.error = null
                } else {
                    registrationPasswordTxtLayout.error = getString(it.passwordError)
                }
            })

        fun showLoading() {
            staticImage.visibility = View.INVISIBLE
            animationView.visibility = View.VISIBLE

            registrationRegistrationBtn.isEnabled = false
            registrationEmailTxt.isEnabled = false
            registrationLoginTxt.isEnabled = false
            registrationPasswordTxt.isEnabled = false
        }

        fun stopLoading() {
            staticImage.visibility = View.VISIBLE
            animationView.visibility = View.INVISIBLE

            registrationRegistrationBtn.isEnabled = true
            registrationEmailTxt.isEnabled = true
            registrationLoginTxt.isEnabled = true
            registrationPasswordTxt.isEnabled = true
        }

        registrationViewModel.userLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> {
                        stopLoading()
                        it.error?.message?.let { errorMessage -> showRegistrationFailed(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        stopLoading()
                        Timber.d("SUCCESS")
                    }
                }
            })


        // Create listeners
        val afterEmailTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registrationViewModel.emailChanged(registrationEmailTxt.text.toString())
            }
        }

        val afterLoginTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registrationViewModel.loginChanged(registrationLoginTxt.text.toString())
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
                registrationViewModel.passwordChanged(registrationPasswordTxt.text.toString())
            }
        }

        fun registrationBtnClickedListener() {
            registrationViewModel.registerUser(
                registrationEmailTxt.text.toString(),
                registrationLoginTxt.text.toString(),
                registrationPasswordTxt.text.toString()
            )
        }

        fun transitionToRegClickedListener() {
            val action = RegistrationFragmentDirections
                .actionRegistrationFragmentToLoginFragment()
            NavHostFragment.findNavController(this@RegistrationFragment)
                .navigate(action)
        }

        // Setup listeners
        registrationEmailTxt.addTextChangedListener(afterEmailTextChangedListener)
        registrationLoginTxt.addTextChangedListener(afterLoginTextChangedListener)
        registrationPasswordTxt.addTextChangedListener(afterPasswordTextChangedListener)
        registrationRegistrationBtn.setOnClickListener {
            registrationBtnClickedListener()
        }
        regTransitionToLoginBtn.setOnClickListener {
            transitionToRegClickedListener();
        }
    }

    private fun showRegistrationFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}