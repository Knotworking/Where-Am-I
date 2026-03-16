package com.knotworking.whereami.core.domain.usecase;

import com.knotworking.whereami.core.domain.repository.PhotoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class GetRandomPhotoUseCase_Factory implements Factory<GetRandomPhotoUseCase> {
  private final Provider<PhotoRepository> photoRepositoryProvider;

  private GetRandomPhotoUseCase_Factory(Provider<PhotoRepository> photoRepositoryProvider) {
    this.photoRepositoryProvider = photoRepositoryProvider;
  }

  @Override
  public GetRandomPhotoUseCase get() {
    return newInstance(photoRepositoryProvider.get());
  }

  public static GetRandomPhotoUseCase_Factory create(
      Provider<PhotoRepository> photoRepositoryProvider) {
    return new GetRandomPhotoUseCase_Factory(photoRepositoryProvider);
  }

  public static GetRandomPhotoUseCase newInstance(PhotoRepository photoRepository) {
    return new GetRandomPhotoUseCase(photoRepository);
  }
}
