package com.knotworking.whereami.data.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isEmpty
import assertk.assertions.isTrue
import com.knotworking.whereami.domain.game.model.HighScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RoomHighScoreRepositoryTest {

    private val fakeDao = FakeHighScoreDao()
    private val repository = RoomHighScoreRepository(fakeDao)

    @Test
    fun `getTopScores maps entities to domain models`() = runTest {
        fakeDao.entities.value = listOf(
            HighScoreEntity(id = 1, totalScore = 5000, timestamp = 1000L),
            HighScoreEntity(id = 2, totalScore = 3000, timestamp = 2000L)
        )

        repository.getTopScores().test {
            val scores = awaitItem()
            assertThat(scores).hasSize(2)
            assertThat(scores[0]).isEqualTo(HighScore(id = 1, totalScore = 5000, timestamp = 1000L))
            assertThat(scores[1]).isEqualTo(HighScore(id = 2, totalScore = 3000, timestamp = 2000L))
        }
    }

    @Test
    fun `save inserts entity with correct totalScore`() = runTest {
        repository.save(4500)

        assertThat(fakeDao.insertedEntities).hasSize(1)
        assertThat(fakeDao.insertedEntities[0].totalScore).isEqualTo(4500)
    }

    @Test
    fun `save sets a non-zero timestamp`() = runTest {
        repository.save(1000)

        assertThat(fakeDao.insertedEntities[0].timestamp > 0).isTrue()
    }

    @Test
    fun `clearAll delegates to dao deleteAll`() = runTest {
        repository.clearAll()

        assertThat(fakeDao.deleteAllCalled).isTrue()
    }

    @Test
    fun `getTopScores returns empty list when dao has no entities`() = runTest {
        repository.getTopScores().test {
            assertThat(awaitItem()).isEmpty()
        }
    }
}

private class FakeHighScoreDao : HighScoreDao {
    val entities = MutableStateFlow<List<HighScoreEntity>>(emptyList())
    val insertedEntities = mutableListOf<HighScoreEntity>()
    var deleteAllCalled = false

    override fun getTopScores(): Flow<List<HighScoreEntity>> = entities

    override suspend fun insert(entity: HighScoreEntity) {
        insertedEntities.add(entity)
    }

    override suspend fun deleteAll() {
        deleteAllCalled = true
    }
}
