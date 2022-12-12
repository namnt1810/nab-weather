package vn.namnt.nabweather.common

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import vn.namnt.nabweather.di.AppContext

/**
 * @author namnt
 * @since 08/12/2022
 */
@Component
interface CommonComponent {
    @AppContext
    fun appContext(): Context

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            @AppContext
            appContext: Context
        ): CommonComponent
    }
}
