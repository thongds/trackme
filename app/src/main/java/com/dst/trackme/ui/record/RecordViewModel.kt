/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dst.trackme.data.local.TrackMeDao
import com.dst.trackme.service.TimerCountUp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class RecordViewModel (val sessionId : Long, trackMeDao: TrackMeDao) : ViewModel() {
    var stopRecordAction = MutableLiveData(false)
    private var recordViewModelRepository = RecordViewModelRepository(sessionId,trackMeDao)
    private var _startRecord = MutableLiveData(true)
    val startRecord : LiveData<Boolean>
    get() = _startRecord
    var latLnLastUpdate = recordViewModelRepository.getLastLatLn()
    var sessionUpdate = recordViewModelRepository.getSession()
    private var _timeCountUpResult = MutableLiveData<Int>()
    val timeCountUpResult : LiveData<Int>
    get() = _timeCountUpResult
    private val timerTaskCountUp = TimerCountUp()
    private val composite = CompositeDisposable()
    init {
        timerTaskCountUp.start()
        composite.add(timerTaskCountUp.getCountTime().observeOn(AndroidSchedulers.mainThread()).subscribeBy {
            _timeCountUpResult.value = it
        })
    }
    suspend fun saveSession(){
        recordViewModelRepository.saveSession(_timeCountUpResult.value!!)
    }

    suspend fun deleteSession(){
        recordViewModelRepository.deleteSession()
    }

    //handle ui action
    fun onStartAndStopRecordClick(){
        _startRecord.value = !_startRecord.value!!
        if (_startRecord.value!!){timerTaskCountUp.resume()}else{timerTaskCountUp.pause()}

    }
    //handle ui action
    fun stopAndQuitRecord(){
        stopRecordAction.value = true
    }

    override fun onCleared() {
        super.onCleared()
        timerTaskCountUp.stop()
        composite.clear()
    }
}