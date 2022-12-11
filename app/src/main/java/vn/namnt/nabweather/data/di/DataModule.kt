package vn.namnt.nabweather.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.namnt.nabweather.BuildConfig
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasource
import vn.namnt.nabweather.data.local.WeatherInfoLocalDatasourceImpl
import vn.namnt.nabweather.data.local.database.WeatherDatabase
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasource
import vn.namnt.nabweather.data.remote.WeatherInfoRemoteDatasourceImpl
import vn.namnt.nabweather.data.remote.interceptors.NoConnectionInterceptor
import vn.namnt.nabweather.data.remote.interceptors.RequestInterceptor
import vn.namnt.nabweather.di.AppContext

/**
 * @author namnt
 * @since 08/12/2022
 */
@Module
internal class DataProvidingModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideDatabase(@AppContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(context, WeatherDatabase::class.java, "weather_info_database")
            .build()
    }

    @Provides
    fun provideOkHttpClient(@AppContext context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(RequestInterceptor("60c6fbeb4b93ac653c492ba806fc346d")) // TODO: exposed app id
            .addInterceptor(NoConnectionInterceptor(context))
            .addNetworkInterceptor(NoConnectionInterceptor(context))

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().also {
                it.level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

@Module(includes = [DataProvidingModule::class])
internal abstract class DataBindingModule {
    @Binds
    abstract fun bindRemoteDatasource(impl: WeatherInfoRemoteDatasourceImpl): WeatherInfoRemoteDatasource

    @Binds
    abstract fun bindLocalDatasource(impl: WeatherInfoLocalDatasourceImpl): WeatherInfoLocalDatasource
}
