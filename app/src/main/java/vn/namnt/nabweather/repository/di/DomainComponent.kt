package vn.namnt.nabweather.repository.di

import dagger.Component
import vn.namnt.nabweather.data.di.DataComponent
import vn.namnt.nabweather.di.DomainScoped
import vn.namnt.nabweather.repository.WeatherInfoRepository

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
