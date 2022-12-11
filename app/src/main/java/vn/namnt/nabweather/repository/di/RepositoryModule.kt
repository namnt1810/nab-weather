package vn.namnt.nabweather.repository.di

import dagger.Binds
import dagger.Module
import vn.namnt.nabweather.repository.WeatherInfoRepository
import vn.namnt.nabweather.repository.WeatherInfoRepositoryImpl

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: WeatherInfoRepositoryImpl): WeatherInfoRepository
}
