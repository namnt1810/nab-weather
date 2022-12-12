package vn.namnt.nabweather.presentation.internal.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal class CoroutineDispatcherModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
