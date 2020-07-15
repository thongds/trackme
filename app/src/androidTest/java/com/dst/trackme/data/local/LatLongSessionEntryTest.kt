/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class LatLongSessionEntryTest{
    private lateinit var database: TrackMeDatabase
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun initDb() {
        // database is stored in memory
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TrackMeDatabase::class.java
        ).build()
    }
    @Test
    fun insertLatLn() = runBlockingTest {
        val session = SessionEntry()
        val sessionId = database.trackMeDatabaseDao.insertSession(session)
        val latLongSessionEntry = LatLongSessionEntry(lat = 10.0,lng = 10.0,createdTime = 10,currentSpeed = 10F,sessionId = sessionId)
        database.trackMeDatabaseDao.insertLatLng(latLongSessionEntry)
        val latLongSessionEntryResult = database.trackMeDatabaseDao.getFirstLatLongOfSession(sessionId)
        assertThat(latLongSessionEntryResult!!.lat,`is`(10.0))
    }
    @Test
    fun deleteLatLn() = runBlockingTest {
        val session = SessionEntry()
        val sessionId = database.trackMeDatabaseDao.insertSession(session)
        val latLongSessionEntry = LatLongSessionEntry(lat = 10.0,lng = 10.0,createdTime = 10,currentSpeed = 10F,sessionId = sessionId)
        database.trackMeDatabaseDao.insertLatLng(latLongSessionEntry)
        database.trackMeDatabaseDao.deleteLatLnOfSession(sessionId)
        val result = database.trackMeDatabaseDao.getLastLatLongOfSession(sessionId)
        val test = result.value == null
        assertThat(test,`is`(true))
    }
    @After
    fun closeDb() = database.close()
}