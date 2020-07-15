/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.bindingAdapter

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dst.trackme.ui.binding.setDistanceFormat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BindingAdapterDistance {
    @Test
    fun setDistanceTest_zero_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDistanceFormat(0F)
        val result = textView.text.toString()
        val expect = "0 m"
        Assert.assertEquals(expect, result)
    }
    @Test
    fun setDistanceTest_round_value_up_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDistanceFormat(2020.6F)
        val result = textView.text.toString()
        val expect = "2 km 21 m"
        Assert.assertEquals(expect, result)
    }
    @Test
    fun setDistanceTest_native_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDistanceFormat(-1F)
        val result = textView.text.toString()
        val expect = "0 m"
        Assert.assertEquals(expect, result)
    }
    @Test
    fun setDistanceTest_NaN_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDistanceFormat(Float.NaN)
        val result = textView.text.toString()
        val expect = "0 m"
        Assert.assertEquals(expect, result)
    }
    @Test
    fun setDistanceTest_one_km_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDistanceFormat(1000F)
        val result = textView.text.toString()
        val expect = "1 km "
        Assert.assertEquals(expect, result)
    }
}