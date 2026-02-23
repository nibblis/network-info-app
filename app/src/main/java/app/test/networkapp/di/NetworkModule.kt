package app.test.networkapp.di

import app.test.networkapp.data.remote.RipeApiService
import app.test.networkapp.di.interceptor.ErrorResponseInterceptor
import app.test.networkapp.di.interceptor.RetryInterceptor
import app.test.networkapp.utils.LoggingEventListener
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    // Factory for creating our custom EventListener.
    single<EventListener.Factory> {
        EventListener.Factory { LoggingEventListener() }
    }

    single {
        OkHttpClient.Builder()
            // Interceptor to convert HTTP errors (4xx, 5xx) to specific exceptions.
            .addInterceptor(ErrorResponseInterceptor())
            // Interceptor to automatically retry failed requests (5xx, network issues).
            .addInterceptor(RetryInterceptor())
            // HttpLoggingInterceptor is great for seeing the request/response body and headers.
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            // Custom interceptor to add headers
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .eventListenerFactory(get())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://rest.db.ripe.net")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<RipeApiService> {
        get<Retrofit>().create(RipeApiService::class.java)
    }
}
