package vn.namnt.nabweather.repository

import dagger.Component
import vn.namnt.nabweather.data.DataComponent
import vn.namnt.nabweather.di.DomainScoped
import vn.namnt.nabweather.repository.internal.di.RepositoryModule

/**
 * @author namnt
 * @since 08/12/2022
 */
@Component(
    modules = [RepositoryModule::class],
    dependencies = [DataComponent::class]
)
@DomainScoped
interface DomainComponent {
    fun weatherRepository(): WeatherInfoRepository

    @Component.Factory
    interface Factory {
        fun create(
            dataComponent: DataComponent
        ): DomainComponent
    }
}
