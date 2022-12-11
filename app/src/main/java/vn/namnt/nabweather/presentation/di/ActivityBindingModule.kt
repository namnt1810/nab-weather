package vn.namnt.nabweather.presentation.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.namnt.nabweather.di.ActivityScoped
import vn.namnt.nabweather.presentation.di.main.MainModule
import vn.namnt.nabweather.presentation.main.MainActivity

@Module
internal abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [MainModule::class]
    )
    internal abstract fun contributeMainActivity(): MainActivity
}
