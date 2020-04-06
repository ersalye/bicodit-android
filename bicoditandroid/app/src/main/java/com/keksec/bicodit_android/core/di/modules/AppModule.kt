package com.keksec.bicodit_android.core.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(private val context: Context) {
    @Provides
    @Singleton
    fun provideAppContext() = context
}