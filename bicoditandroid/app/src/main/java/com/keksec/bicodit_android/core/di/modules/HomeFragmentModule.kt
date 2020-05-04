package com.keksec.bicodit_android.core.di.modules

import com.keksec.bicodit_android.screens.main.home.createrating.RatingFragment
import com.keksec.bicodit_android.screens.main.home.profile.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeRatingFragment(): RatingFragment
}