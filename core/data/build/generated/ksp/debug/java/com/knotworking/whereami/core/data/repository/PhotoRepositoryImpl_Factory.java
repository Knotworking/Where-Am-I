package com.knotworking.whereami.core.data.repository;

import com.knotworking.whereami.core.network.FlickrService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class PhotoRepositoryImpl_Factory implements Factory<PhotoRepositoryImpl> {
  private final Provider<FlickrService> flickrServiceProvider;

  private final Provider<String> apiKeyProvider;

  private PhotoRepositoryImpl_Factory(Provider<FlickrService> flickrServiceProvider,
      Provider<String> apiKeyProvider) {
    this.flickrServiceProvider = flickrServiceProvider;
    this.apiKeyProvider = apiKeyProvider;
  }

  @Override
  public PhotoRepositoryImpl get() {
    return newInstance(flickrServiceProvider.get(), apiKeyProvider.get());
  }

  public static PhotoRepositoryImpl_Factory create(Provider<FlickrService> flickrServiceProvider,
      Provider<String> apiKeyProvider) {
    return new PhotoRepositoryImpl_Factory(flickrServiceProvider, apiKeyProvider);
  }

  public static PhotoRepositoryImpl newInstance(FlickrService flickrService, String apiKey) {
    return new PhotoRepositoryImpl(flickrService, apiKey);
  }
}
