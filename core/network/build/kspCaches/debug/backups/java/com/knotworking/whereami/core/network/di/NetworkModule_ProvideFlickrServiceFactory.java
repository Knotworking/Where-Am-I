package com.knotworking.whereami.core.network.di;

import com.knotworking.whereami.core.network.FlickrService;
import com.squareup.moshi.Moshi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkModule_ProvideFlickrServiceFactory implements Factory<FlickrService> {
  private final Provider<Moshi> moshiProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  private NetworkModule_ProvideFlickrServiceFactory(Provider<Moshi> moshiProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.moshiProvider = moshiProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public FlickrService get() {
    return provideFlickrService(moshiProvider.get(), okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideFlickrServiceFactory create(Provider<Moshi> moshiProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideFlickrServiceFactory(moshiProvider, okHttpClientProvider);
  }

  public static FlickrService provideFlickrService(Moshi moshi, OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFlickrService(moshi, okHttpClient));
  }
}
