package com.keksec.bicodit_android.core.di.modules

import com.keksec.bicodit_android.screens.authentication.login.LoginFragment
import com.keksec.bicodit_android.screens.authentication.registration.RegistrationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    /*
     * We define the name of the Fragment we are going
     * to inject the ViewModelFactory into. i.e.
     */
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment
}

