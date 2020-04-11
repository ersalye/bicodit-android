package com.keksec.bicodit_android.core.di.modules

import com.keksec.bicodit_android.screens.authentication.AuthenticationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    /*
     * We modify ActivityModule by adding the
     * FragmentModule to the Activity which contains
     * the fragment
     */
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeAuthenticationActivity(): AuthenticationActivity
}

