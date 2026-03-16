package com.knotworking.whereami.core.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CalculateScoreUseCase_Factory implements Factory<CalculateScoreUseCase> {
  @Override
  public CalculateScoreUseCase get() {
    return newInstance();
  }

  public static CalculateScoreUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CalculateScoreUseCase newInstance() {
    return new CalculateScoreUseCase();
  }

  private static final class InstanceHolder {
    static final CalculateScoreUseCase_Factory INSTANCE = new CalculateScoreUseCase_Factory();
  }
}
