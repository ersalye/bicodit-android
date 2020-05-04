package com.keksec.bicodit_android.screens.main.home.createrating

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.screens.main.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_rating.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RatingFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var ratingViewModel: RatingViewModel

    private val DEFAULT_RATING_VALUE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity?)?.hideBottomNav()
        val rootView: View = inflater.inflate(R.layout.fragment_rating, container, false)
        val tb = rootView.findViewById<View>(R.id.createRatingToolbar) as Toolbar
        tb.setTitle(R.string.title_rating)
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        tb.setNavigationOnClickListener {
            transitionToHomeClickedListener()
        }
        return rootView
    }

    private fun transitionToHomeClickedListener() {
        val action = RatingFragmentDirections
            .actionNavigationRatingToNavigationHome()
        NavHostFragment.findNavController(this@RatingFragment)
            .navigate(action)
    }

    override fun onDestroyView() {
        (activity as MainActivity?)?.showBottomNav()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ratingViewModel = ViewModelProvider(this, viewModelFactory).get(RatingViewModel::class.java)
        val ratingImage = view.findViewById<ImageView>(R.id.createRatingImage)
        val ratingValues = arrayOf<ImageView>(
            view.findViewById(R.id.createRatingValueOne),
            view.findViewById(R.id.createRatingValueTwo),
            view.findViewById(R.id.createRatingValueThree),
            view.findViewById(R.id.createRatingValueFour),
            view.findViewById(R.id.createRatingValueFive)
        )

        fun updateRatingImage(ratingValue: Int) {
            ratingImage.setImageResource(getRatingImageResource(ratingValue))
        }

        fun updateRatingValues(ratingValue: Int) {
            ratingValues[ratingValue - 1].setImageResource(getValueResource(ratingValue, true))
            for (i in ratingValues.indices) {
                if (i != ratingValue - 1) {
                    ratingValues[i].setImageResource(getValueResource(i + 1, false))
                }
            }
        }

        for (i in ratingValues.indices) {
            ratingValues[i].setOnClickListener {
                ratingViewModel.ratingValueChanged(i + 1)
            }
        }

        updateRatingImage(DEFAULT_RATING_VALUE)
        updateRatingValues(DEFAULT_RATING_VALUE)

        //setup observers
        ratingViewModel.ratingValueState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                updateRatingImage(it.ratingValue)
                updateRatingValues(it.ratingValue)
            })

        ratingViewModel.aboutValidationState.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                if (it.aboutError == null) {
                    createRatingAboutLayout.error = null
                } else {
                    createRatingAboutLayout.error = getString(it.aboutError)
                }
            })

        ratingViewModel.ratingLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.SUCCESS -> {
                        transitionToHomeClickedListener()
                    }
                    Status.ERROR -> {
                        it.error?.message?.let { errorMessage -> showLoginFailed(errorMessage) }
                    }
                }
            })

        //create listeners
        val afterRatingTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                ratingViewModel.aboutChanged(createRatingAboutEditText.text.toString())
            }
        }

        fun createRatingBtnClickedListener() {
            lifecycleScope.launch(Dispatchers.IO) {
                ratingViewModel.createRating(
                    createRatingAboutEditText.text.toString(),
                    ratingViewModel.ratingValueState.value!!.ratingValue
                )
            }
        }

        createRatingAboutEditText.addTextChangedListener(afterRatingTextChangedListener)
        createRatingBtn.setOnClickListener {
            createRatingBtnClickedListener()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getRatingImageResource(ratingValue: Int): Int {
        return when (ratingValue) {
            1 -> {
                R.drawable.super_sad_panda
            }
            2 -> {
                R.drawable.very_sad_panda
            }
            3 -> {
                R.drawable.ordinary_panda
            }
            4 -> {
                R.drawable.happy_panda
            }
            5 -> {
                R.drawable.super_happy_panda
            }
            else -> {
                -1
            }
        }
    }

    private fun getValueResource(ratingValue: Int, isActive: Boolean): Int {
        return when (ratingValue) {
            1 -> {
                if (isActive) {
                    R.drawable.one_focused
                } else {
                    R.drawable.one_default
                }
            }
            2 -> {
                if (isActive) {
                    R.drawable.two_focused
                } else {
                    R.drawable.two_default
                }
            }
            3 -> {
                if (isActive) {
                    R.drawable.three_focused
                } else {
                    R.drawable.three_default
                }
            }
            4 -> {
                if (isActive) {
                    R.drawable.four_focused
                } else {
                    R.drawable.four_default
                }
            }
            5 -> {
                if (isActive) {
                    R.drawable.five_focused
                } else {
                    R.drawable.five_default
                }
            }
            else -> {
                -1
            }
        }
    }

    private fun showLoginFailed(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}