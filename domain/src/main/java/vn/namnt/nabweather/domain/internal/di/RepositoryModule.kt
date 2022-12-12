package vn.namnt.nabweather.domain.internal.di

import dagger.Binds
import dagger.Module
import vn.namnt.nabweather.domain.WeatherInfoRepository
import vn.namnt.nabweather.domain.internal.WeatherInfoRepositoryImpl

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: WeatherInfoRepositoryImpl): WeatherInfoRepository
}
