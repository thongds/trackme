/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SessionEntryTest {
    private lateinit var database: TrackMeDatabase
    private lateinit var trackMeDao: TrackMeDao
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // database is stored in memory
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TrackMeDatabase::class.java
        ).build()
        trackMeDao = database.trackMeDatabaseDao
    }

    @Test
    fun testInsertSession(){
        val sessionEntry = SessionEntry()
        val sessionId =trackMeDao.insertSession(sessionEntry)
        assertThat(sessionId,`is`(1L))
    }

    @Test
    fun testUpdateDuration() = runBlockingTest{
        val durationExpect = 100
        val sessionEntry = SessionEntry()
        val sessionId =trackMeDao.insertSession(sessionEntry)
        val sessionEntryResult = trackMeDao.getSessionByIdRawValue(sessionId)
        sessionEntryResult!!.duration = durationExpect
        trackMeDao.updateSession(sessionEntryResult)
        val duration = trackMeDao.getSessionById(sessionId)
        assertThat(duration,`is`(duration))
    }
    @Test
    fun testUpdateDistance()= runBlockingTest{
        val distanceExpect = 100F
        val sessionEntry = SessionEntry()
        val sessionId =trackMeDao.insertSession(sessionEntry)
        val sessionEntryResult = trackMeDao.getSessionByIdRawValue(sessionId)
        sessionEntryResult!!.distance = distanceExpect
        trackMeDao.updateSession(sessionEntryResult)
        val duration = trackMeDao.getSessionById(sessionId)
        assertThat(duration,`is`(duration))
    }
    @Test
    fun testValidSession() = runBlockingTest{
        val sessionEntry = SessionEntry()
        val sessionId =trackMeDao.insertSession(sessionEntry)
        val sessionEntryResult = trackMeDao.getSessionByIdRawValue(sessionId)
        sessionEntryResult!!.valid = true
        trackMeDao.updateSession(sessionEntryResult)
        val session = trackMeDao.getSessionByIdRawValue(sessionId)
        assertThat(session!!.valid,`is`(true))
    }
    @Test
    fun testSessionPage() = runBlockingTest{
        for(x in 0..10){
            val sessionEntry = SessionEntry(valid = true)
            trackMeDao.insertSession(sessionEntry)
        }
        val session = trackMeDao.getSessionPage(0,10,true)
        assertThat(session.size,`is`(10))
    }


    @After
    fun closeDb() = database.close()
}