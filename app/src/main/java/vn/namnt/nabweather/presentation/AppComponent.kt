package vn.namnt.nabweather.presentation

import androidx.work.DelegatingWorkerFactory
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import vn.namnt.nabweather.WeatherApplication
import vn.namnt.nabweather.common.CommonComponent
import vn.namnt.nabweather.di.AppScoped
import vn.namnt.nabweather.presentation.internal.di.ActivityBindingModule
import vn.namnt.nabweather.presentation.internal.di.CoroutineDispatcherModule
import vn.namnt.nabweather.presentation.internal.di.ViewModelModule
import vn.namnt.nabweather.presentation.internal.di.WorkerBindingModule
import vn.namnt.nabweather.repository.DomainComponent

/**
 * @author namnt
 * @since 08/12/2022
 */
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ViewModelModule::class,
        WorkerBindingModule::class,
        ActivityBindingModule::class,
        CoroutineDispatcherModule::class
    ],
    dependencies = [
        CommonComponent::class,
        DomainComponent::class
    ]
)
@AppScoped
interface AppComponent : AndroidInjector<WeatherApplication> {
    // Further support for DFMs
    fun delegatingWorkerFactory(): DelegatingWorkerFactory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            application: WeatherApplication,
            @BindsInstance
            delegatingWorkerFactory: DelegatingWorkerFactory,
            commonComponent: CommonComponent,
            domainComponent: DomainComponent
        ): AppComponent
    }
}
