package vn.namnt.nabweather.data.di

import dagger.Component
import vn.namnt.nabweather.common.di.CommonComponent
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.di.DataScoped

/**
 * @author namnt
 * @since 08/12/2022
 */
@Component(
    modules = [DataBindingModule::class],
    dependencies = [CommonComponent::class]
)
@DataScoped
interface DataComponent {
    fun remoteDatasource(): WeatherInfoRemoteDatasource

    fun localDatasource(): WeatherInfoLocalDatasource

    @Component.Factory
    interface Factory {
        fun create(
            commonComponent: CommonComponent
        ): DataComponent
    }
}
