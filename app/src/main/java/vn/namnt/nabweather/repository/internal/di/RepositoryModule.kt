package vn.namnt.nabweather.repository.internal.di

import dagger.Binds
import dagger.Module
import vn.namnt.nabweather.repository.WeatherInfoRepository
import vn.namnt.nabweather.repository.internal.WeatherInfoRepositoryImpl

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: WeatherInfoRepositoryImpl): WeatherInfoRepository
}
