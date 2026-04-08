---
name: android-data-layer
description: |
  Data layer patterns for Android/KMP - data sources, repositories, DTOs, mappers, Room entities, Retrofit HttpClient, safe call helpers, token storage, and offline-first. Use this skill whenever writing or reviewing a data source or repository, creating DTOs or Room entities, writing mappers, setting up the Retrofit HttpClient, handling network errors, or implementing token refresh. Trigger on phrases like "create a repository", "create a data source", "add a DAO", "Retrofit client", "write a mapper", "DTO", "Room entity", "network call", "token storage", or "offline-first".
---

# Android / KMP Data Layer

## Error Handling

This skill uses `Result<T, E>`, `DataError`, and the extension helpers defined in the **android-error-handling** skill. Refer to that skill for the full `Result` wrapper, `DataError` sealed interface, and `map`/`onSuccess`/`onFailure`/`asEmptyResult` extensions.

## Domain Layer Contracts

- Pure Kotlin — no Android/framework imports.
- Contains: domain models, data source/repository **interfaces**, error types.
- **Every data source or repository used by a ViewModel must have an interface in `domain`** — enforces that `ui` never depends on `data`, and enables testing.

---

## DTOs and Domain Models

- Always separate: DTOs (data layer) ↔ Domain Models (domain layer).
- Domain models never go directly into Room entities or Retrofit request/response bodies.
- Mappers are simple extension functions living in the data layer alongside the DTO:

```kotlin
fun BenHikesPhotoDto.toPhoto(): Photo = Photo(
    id = filename,
    title = filename,
    latitude = lat,
    longitude = lng,
    urlM = url
)

fun HighScoreEntity.toHighScore(): HighScore = HighScore(
    id = id,
    totalScore = totalScore,
    timestamp = timestamp
)
fun HighScore.toHighScoreEntity(): HighScoreEntity = HighScoreEntity(
    id = id,
    totalScore = totalScore,
    timestamp = timestamp
)
```
 
---

## Implementations

Name implementations for what makes them unique — never suffix with `Impl`.

### Data source (single source)

```kotlin
class FlickrDataSource(private val api: FlickrApi) : RemotePhotoDataSource {
    override suspend fun fetchPhoto(): Photo? {
        val response = api.getRandomPhoto()
        return response.photos.photo.randomOrNull()?.toPhoto()
    }
}
```

### Repository

```kotlin
class PhotoRepositoryImpl(
    private val flickrDataSource: FlickrDataSource,
    private val benHikesDataSource: BenHikesDataSource,
    private val dataStore: DataStore<Preferences>
) : PhotoRepository {
    override suspend fun getRandomGeotaggedPhoto(): Result<Photo, Error> {
        val source = getPhotoSource().first()
        val dataSource = when (source) {
            PhotoSource.FLICKR -> flickrDataSource
            PhotoSource.BENHIKES -> benHikesDataSource
        }
        return when (val result = safeCall { dataSource.fetchPhoto() }) {
            is Result.Success -> result.data?.let { Result.Success(it) }
                ?: Result.Error(PhotoError.NO_PHOTO_FOUND)
            is Result.Error -> result
        }
    }
}
```

Use names like `FlickrDataSource`, `BenHikesDataSource`, `RoomHighScoreRepository`. The name should tell you what the class wraps or how it behaves.

---

## Room Migrations

Prefer `@Database(autoMigrations = [AutoMigration(from = 1, to = 2)])`. Use manual `Migration` objects when the schema change is too complex for auto-migration.
 
---

## Offline-First (when applicable)

Follow **Room as single source of truth**: fetch from network → persist to Room → expose DB `Flow` to the ViewModel. The ViewModel never observes network responses directly.

This pattern is optional — apply it when the project requires offline support.
 
---

## Naming Conventions

| Thing | Convention | Example |
|---|---|---|
| Data source interface | `Remote<Entity>DataSource` | `RemotePhotoDataSource` |
| Data source impl | describe what makes it unique | `FlickrDataSource`, `BenHikesDataSource` |
| Repository interface | `<Entity>Repository` | `PhotoRepository`, `HighScoreRepository` |
| Repository impl | describe what makes it unique | `PhotoRepositoryImpl`, `RoomHighScoreRepository` |
| DTO | `<Model>Dto` | `BenHikesPhotoDto` |
| Room entity | `<Model>Entity` | `HighScoreEntity` |
| Mapper | extension fun on source type | `fun BenHikesPhotoDto.toPhoto()` |
 
---

## Checklist: Adding a New Data Source or Repository

- [ ] Define domain model(s) in `domain:<feature>`
- [ ] Define data source or repository interface in `domain:<feature>`
- [ ] Define feature-specific error type(s) in `domain:<feature>` (implement `Error`) — see **android-error-handling** skill
- [ ] Define DTOs and Room entities in `data:<feature>`
- [ ] Write mappers as extension functions in `data:<feature>`
- [ ] Implement data source (single source) or repository (multi-source) in `data:<feature>`, named for what makes it unique
