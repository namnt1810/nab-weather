package vn.namnt.nabweather.presentation.internal.di.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import vn.namnt.nabweather.di.ViewModelKey
import vn.namnt.nabweather.presentation.internal.main.MainViewModel

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(vm: MainViewModel): ViewModel
}
