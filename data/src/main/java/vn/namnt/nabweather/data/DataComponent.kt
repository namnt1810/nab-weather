package vn.namnt.nabweather.data

import dagger.Component
import vn.namnt.nabweather.common.CommonComponent
import vn.namnt.nabweather.data.internal.di.DataBindingModule
import vn.namnt.nabweather.data.internal.di.DataScoped

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
