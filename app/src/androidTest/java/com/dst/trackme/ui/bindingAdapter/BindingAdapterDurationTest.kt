/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.bindingAdapter

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dst.trackme.ui.binding.setDurationFormat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BindingAdapterDurationTest {
    @Test
    fun testDurationFormat_second_zero(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(0)
        val result = textView.text.toString()
        val expect = "00:00:00"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_invalid(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(0)
        val result = textView.text.toString()
        val expect = "00:00:00"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(10)
        val result = textView.text.toString()
        val expect = "00:00:10"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input_small_than_10(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(1)
        val result = textView.text.toString()
        val expect = "00:00:01"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input_bigger_than_10(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(20)
        val result = textView.text.toString()
        val expect = "00:00:20"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input_massive_value(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(39560)
        val result = textView.text.toString()
        val expect = "10:59:20"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input_hour_value(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(212400)
        val result = textView.text.toString()
        val expect = "59:00:00"
        Assert.assertEquals(result, expect)
    }
    @Test
    fun testDurationFormat_second_input_59_value(){
        val textView = TextView(ApplicationProvider.getApplicationContext())
        textView.setDurationFormat(215999)
        val result = textView.text.toString()
        val expect = "59:59:59"
        Assert.assertEquals(result, expect)
    }
}