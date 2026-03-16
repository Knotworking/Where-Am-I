package com.knotworking.whereami.core.network.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.knotworking.whereami.core.network.di.FlickrApiKey")
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
public final class NetworkModule_ProvideFlickrApiKeyFactory implements Factory<String> {
  @Override
  public String get() {
    return provideFlickrApiKey();
  }

  public static NetworkModule_ProvideFlickrApiKeyFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideFlickrApiKey() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFlickrApiKey());
  }

  private static final class InstanceHolder {
    static final NetworkModule_ProvideFlickrApiKeyFactory INSTANCE = new NetworkModule_ProvideFlickrApiKeyFactory();
  }
}
