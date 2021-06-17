package com.monta.cozy.di.component

import android.app.Application
import com.monta.cozy.CozyApplication
import com.monta.cozy.di.module.ActivityModule
import com.monta.cozy.di.module.AppModule
import com.monta.cozy.di.module.NetworkModule
import com.monta.cozy.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        ActivityModule::class,
        ViewModelModule::class,
        NetworkModule::class]
)
@Singleton
interface AppComponent : AndroidInjector<CozyApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}