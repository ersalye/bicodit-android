package com.keksec.bicodit_android.core.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.keksec.bicodit_android.core.factory.ViewModelFactory
import com.keksec.bicodit_android.core.di.ViewModelKey
import com.keksec.bicodit_android.screens.authentication.login.LoginViewModel
import com.keksec.bicodit_android.screens.authentication.registration.RegistrationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    /*
     * This method basically says
     * inject this object into a Map using the @IntoMap annotation,
     * with the  LoginViewModel.class as key,
     * and a Provider that will build a LoginViewModel
     * object.
     *
     * */

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    protected abstract fun loginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    protected abstract fun registrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel
}

