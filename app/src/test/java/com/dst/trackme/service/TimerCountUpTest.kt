/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerCountUpTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var timerCountUp : TimerCountUp
    private lateinit var timerTask : Any
    @Before
    fun setUpTime(){
        timerCountUp = TimerCountUp()
        val timerTaskPrivateField = TimerCountUp::class.java.getDeclaredField("timerTask")
        timerTaskPrivateField.isAccessible = true
        timerTask = timerTaskPrivateField.get(timerCountUp)!!
    }
    @Test
    fun start() {
        timerCountUp.start()
        Thread.sleep(4000)
        val timeInSecField = TimerTaskCountUp::class.java.getDeclaredField("_timeInSec")
        timeInSecField.isAccessible = true
        val timeSecData = timeInSecField.get(timerTask)
        assertEquals(4,timeSecData)
    }

    @Test
    fun resume() {
        timerCountUp.start()
        Thread.sleep(4000)
        timerCountUp.resume()
        Thread.sleep(4000)
        val timeInSecField = TimerTaskCountUp::class.java.getDeclaredField("_timeInSec")
        timeInSecField.isAccessible = true
        val timeSecData = timeInSecField.get(timerTask)
        assertEquals(8,timeSecData)

    }
    @Test
    fun pause() {
        timerCountUp.start()
        Thread.sleep(4000)
        timerCountUp.pause()
        Thread.sleep(4000)
        val timeInSecField = TimerTaskCountUp::class.java.getDeclaredField("_timeInSec")
        timeInSecField.isAccessible = true
        val timeSecData = timeInSecField.get(timerTask)
        assertEquals(4,timeSecData)
    }
    @Test
    fun stop() {
        timerCountUp.start()
        Thread.sleep(4000)
        timerCountUp.stop()
        Thread.sleep(4000)
        val timeInSecField = TimerTaskCountUp::class.java.getDeclaredField("_timeInSec")
        timeInSecField.isAccessible = true
        val timeSecData = timeInSecField.get(timerTask)
        assertEquals(0,timeSecData)
    }

}