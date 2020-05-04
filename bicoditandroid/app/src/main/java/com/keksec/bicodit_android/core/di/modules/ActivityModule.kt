package com.keksec.bicodit_android.core.di.modules

import com.keksec.bicodit_android.screens.authentication.AuthenticationActivity
import com.keksec.bicodit_android.screens.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [AuthFragmentModule::class])
    abstract fun contributeAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}

