package vn.namnt.nabweather.presentation.internal.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import vn.namnt.nabweather.presentation.utils.ViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factor: ViewModelFactory): ViewModelProvider.Factory
}
