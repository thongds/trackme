/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*


class TimerCountUp{

    private var timerTask = TimerTaskCountUp()
    private var timer = Timer()
    fun start(){
        timerTask.resetCountTime()
        timer.schedule(timerTask,0,1000)
    }
    fun resume(){
        timerTask.resume()
    }
    fun pause(){
        timerTask.pause()
    }
    //clear count up value and release timer
    fun stop(){
        timerTask.resetCountTime()
        timer.cancel()
        timer.purge()
    }
    fun getCountTime() : Observable<Int> = timerTask.countUpSec
}
class TimerTaskCountUp : TimerTask() {
    private var _timeInSec = 0
    private  var _pause = false
    var countUpSec: PublishSubject<Int> = PublishSubject.create()
    override fun run() {
        if (!_pause){
            _timeInSec++
            //emmit time to observers
            countUpSec.onNext(_timeInSec)
        }
    }
    fun resetCountTime(){
        _timeInSec = 0
    }
    fun pause(){
        _pause = true
    }
    fun resume(){
        _pause = false
    }
}