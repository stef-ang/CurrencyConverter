package com.stefang.app

import com.stefang.app.feature.currency.utils.AppDispatchers
import com.stefang.app.feature.currency.utils.AppDispatchersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {

    @Singleton
    @Binds
    fun bindsAppDispatchers(dispatcher: AppDispatchersImpl): AppDispatchers
}