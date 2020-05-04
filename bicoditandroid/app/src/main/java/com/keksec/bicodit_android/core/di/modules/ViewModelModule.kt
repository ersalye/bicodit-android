package com.keksec.bicodit_android.core.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keksec.bicodit_android.core.di.ViewModelKey
import com.keksec.bicodit_android.core.factory.ViewModelFactory
import com.keksec.bicodit_android.screens.authentication.login.LoginViewModel
import com.keksec.bicodit_android.screens.authentication.registration.RegistrationViewModel
import com.keksec.bicodit_android.screens.main.home.createrating.RatingViewModel
import com.keksec.bicodit_android.screens.main.home.profile.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    protected abstract fun loginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    protected abstract fun registrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    protected abstract fun homeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RatingViewModel::class)
    protected abstract fun ratingViewModel(ratingViewModel: RatingViewModel): ViewModel
}

