package com.knotworking.whereami.feature.game;

import com.knotworking.whereami.core.domain.usecase.CalculateDistanceUseCase;
import com.knotworking.whereami.core.domain.usecase.CalculateScoreUseCase;
import com.knotworking.whereami.core.domain.usecase.GetRandomPhotoUseCase;
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
public final class GameViewModel_Factory implements Factory<GameViewModel> {
  private final Provider<GetRandomPhotoUseCase> getRandomPhotoUseCaseProvider;

  private final Provider<CalculateDistanceUseCase> calculateDistanceUseCaseProvider;

  private final Provider<CalculateScoreUseCase> calculateScoreUseCaseProvider;

  private GameViewModel_Factory(Provider<GetRandomPhotoUseCase> getRandomPhotoUseCaseProvider,
      Provider<CalculateDistanceUseCase> calculateDistanceUseCaseProvider,
      Provider<CalculateScoreUseCase> calculateScoreUseCaseProvider) {
    this.getRandomPhotoUseCaseProvider = getRandomPhotoUseCaseProvider;
    this.calculateDistanceUseCaseProvider = calculateDistanceUseCaseProvider;
    this.calculateScoreUseCaseProvider = calculateScoreUseCaseProvider;
  }

  @Override
  public GameViewModel get() {
    return newInstance(getRandomPhotoUseCaseProvider.get(), calculateDistanceUseCaseProvider.get(), calculateScoreUseCaseProvider.get());
  }

  public static GameViewModel_Factory create(
      Provider<GetRandomPhotoUseCase> getRandomPhotoUseCaseProvider,
      Provider<CalculateDistanceUseCase> calculateDistanceUseCaseProvider,
      Provider<CalculateScoreUseCase> calculateScoreUseCaseProvider) {
    return new GameViewModel_Factory(getRandomPhotoUseCaseProvider, calculateDistanceUseCaseProvider, calculateScoreUseCaseProvider);
  }

  public static GameViewModel newInstance(GetRandomPhotoUseCase getRandomPhotoUseCase,
      CalculateDistanceUseCase calculateDistanceUseCase,
      CalculateScoreUseCase calculateScoreUseCase) {
    return new GameViewModel(getRandomPhotoUseCase, calculateDistanceUseCase, calculateScoreUseCase);
  }
}
