/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.bindingAdapter

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dst.trackme.ui.binding.setDurationFormat
import com.dst.trackme.ui.binding.setSpeedFormat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BindingAdapterSpeedTest{
    @Test
    fun setSpeedFormatTest_zero_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(0F)
        val result = textView.text.toString()
        val expect = "0 m/s"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun setSpeedFormatTest_native_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(-10F)
        val result = textView.text.toString()
        val expect = "0 m/s"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun setSpeedFormatTest_NAN_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(Float.NaN)
        val result = textView.text.toString()
        val expect = "0 m/s"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun setSpeedFormatTest_small_format_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(0.1F)
        val result = textView.text.toString()
        val expect = "0.1 m/s"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun setSpeedFormatTest_normal_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(1F)
        val result = textView.text.toString()
        val expect = "3.6 km/h"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun setSpeedFormatTest_massive_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setSpeedFormat(1000F)
        val result = textView.text.toString()
        val expect = "3600.0 km/h"
        Assert.assertEquals(result, expect)
    }

}