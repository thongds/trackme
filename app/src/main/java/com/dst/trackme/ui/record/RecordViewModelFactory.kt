/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dst.trackme.data.local.TrackMeDao

class RecordViewModelFactory(private val sessionId : Long, private val trackMeDao: TrackMeDao) : ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            return RecordViewModel(sessionId, trackMeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}