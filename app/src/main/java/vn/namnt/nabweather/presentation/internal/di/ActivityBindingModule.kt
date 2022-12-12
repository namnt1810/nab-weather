package vn.namnt.nabweather.presentation.internal.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.namnt.nabweather.di.ActivityScoped
import vn.namnt.nabweather.presentation.internal.di.main.MainModule
import vn.namnt.nabweather.presentation.internal.main.MainActivity

@Module
internal abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [MainModule::class]
    )
    internal abstract fun contributeMainActivity(): MainActivity
}
