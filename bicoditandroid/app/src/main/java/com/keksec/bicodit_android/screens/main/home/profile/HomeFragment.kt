package com.keksec.bicodit_android.screens.main.home.profile

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.screens.main.MainActivity
import com.keksec.bicodit_android.screens.main.NavListener
import com.mikhaellopez.circularimageview.CircularImageView
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import javax.inject.Inject


class HomeFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: RatingsAdapter

    private var listener: NavListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        listener = activity as MainActivity?
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)
        val tb = rootView.findViewById<View>(R.id.toolbar) as Toolbar
        tb.setTitle(R.string.title_home)
        val mainActivity = (activity as AppCompatActivity?)
        mainActivity?.setSupportActionBar(tb)
        val profileScrollView = rootView.findViewById<View>(R.id.profileScrollView)
        profileScrollView.scrollTo(0, 0)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        val profileAvatar = view.findViewById<CircularImageView>(R.id.profileAvatar)
        val profileLogin = view.findViewById<TextView>(R.id.profileLogin)
        val profileName = view.findViewById<TextView>(R.id.profileName)
        val profileText = view.findViewById<TextView>(R.id.profileText)
        val noRatingsTitle = view.findViewById<TextView>(R.id.noRatingsTitle)
        val noUserDataTitle = view.findViewById<TextView>(R.id.noUserDataTitle)
        val ratingLoadingSpinner = view.findViewById<GifImageView>(R.id.ratingsLoading)
        val userLoadingSpinner = view.findViewById<GifImageView>(R.id.userLoading)
        val amountOfRatings = view.findViewById<TextView>(R.id.amountOfRatings)
        val amountOfRatingsName = view.findViewById<TextView>(R.id.amountOfRatingsName)
        val ratingRecyclerView = view.findViewById<RecyclerView>(R.id.ratingRecyclerView)

        noRatingsTitle.visibility = View.GONE
        noUserDataTitle.visibility = View.GONE

        adapter =
            RatingsAdapter(
                context!!,
                listOf()
            ) {}
        ratingRecyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            homeViewModel.getUserData()
            homeViewModel.getAllUserRatings()
        }

        if (adapter.itemCount == 0) {
            noRatingsTitle.visibility = View.VISIBLE
        }

        amountOfRatings.text = getAmountOfRatings(adapter.itemCount)
        amountOfRatingsName.text = getAmountOfRatingsName(adapter.itemCount)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        ratingRecyclerView.layoutManager = layoutManager
        ratingRecyclerView.setHasFixedSize(true)
        ratingRecyclerView.isNestedScrollingEnabled = false

        fun enableRatingsSpinner() {
            ratingLoadingSpinner.visibility = View.VISIBLE
        }

        fun disableRatingsSpinner() {
            ratingLoadingSpinner.visibility = View.GONE
        }

        fun enableUserSpinner() {
            userLoadingSpinner.visibility = View.VISIBLE
        }

        fun disableUserSpinner() {
            userLoadingSpinner.visibility = View.GONE
        }

        homeViewModel.userLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> {
                        enableUserSpinner()
                    }
                    Status.ERROR -> {
                        disableUserSpinner()
                        noUserDataTitle.visibility = View.VISIBLE
                        it.error?.message?.let { errorMessage -> showFailToast(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        disableUserSpinner()
                        val user = it.data
                        val avatarResourceId =
                            resources.getIdentifier(user?.avatar, "drawable", context!!.packageName)
                        profileAvatar.setImageResource(avatarResourceId)
                        val userLoginResourceValue = "@${user?.login}"
                        profileLogin.text = userLoginResourceValue
                        profileName.text = user?.profileName
                        profileText.text = user?.profileInfo
                    }
                }
            }
        )

        homeViewModel.ratingsLiveData.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.LOADING -> {
                        noRatingsTitle.visibility = View.GONE
                        enableRatingsSpinner()
                    }
                    Status.ERROR -> {
                        disableRatingsSpinner()
                        it.error?.message?.let { errorMessage -> showFailToast(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        disableRatingsSpinner()
                        it.data?.let { newData -> adapter.setData(newData) }
                        val ratingsCount = adapter.itemCount
                        amountOfRatings.text = getAmountOfRatings(ratingsCount)
                        amountOfRatingsName.text = getAmountOfRatingsName(ratingsCount)
                        if (ratingsCount == 0) {
                            noRatingsTitle.visibility = View.VISIBLE
                        } else {
                            noRatingsTitle.visibility = View.GONE
                        }
                    }
                }
            }
        )

        homeViewModel.logoutMonitor.observe(viewLifecycleOwner,
            Observer {
                if (it == null) {
                    return@Observer
                }
                when (it.status) {
                    Status.ERROR -> {
                        it.error?.message?.let { errorMessage -> showFailToast(errorMessage) }
                    }
                    Status.SUCCESS -> {
                        listener?.navigateToAuthenticationActivity()
                    }
                }
            })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.quit -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    homeViewModel.logoutUser()
                }
                listener?.navigateToAuthenticationActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getAmountOfRatings(ratingsCount: Int): String {
        if (ratingsCount >= 1000000) {
            return (ratingsCount / 1000000).toString() + "M"
        }
        if (ratingsCount >= 10000) {
            return (ratingsCount / 10000).toString() + "K"
        }
        return ratingsCount.toString()
    }

    private fun getAmountOfRatingsName(ratingsCount: Int): String {
        val preLastDigit: Int = ratingsCount % 100 / 10

        return if (preLastDigit == 1) {
            "рейтингов"
        } else when (ratingsCount % 10) {
            1 -> "рейтинг"
            2, 3, 4 -> "рейтинга"
            else -> "рейтингов"
        }

    }

    private fun showFailToast(errorId: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorId), Toast.LENGTH_LONG).show()
    }
}