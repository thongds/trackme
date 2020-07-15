/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.binding

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dst.trackme.data.local.SessionEntry
import com.dst.trackme.ui.history.HistoryAdapter
import java.text.DecimalFormat
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
@BindingAdapter("durationFormat")
fun TextView.setDurationFormat(durationSec : Int){
    if (durationSec<=0) text = "00:00:00"
    val sec = durationSec%60
    val secString = if(sec<10)  "0${sec}" else "$sec"
    val minute = durationSec/60
    val minuteRound = minute%60
    val minuteString = if(minuteRound<10)  "0${minuteRound}" else "$minuteRound"
    val hour = minute/60
    val hourString = if(hour<10)  "0${hour}" else "$hour"
    val timeInString = "$hourString:$minuteString:$secString"
    text = timeInString
}
/*
speed : m/s
*/
@BindingAdapter("speedFormat")
fun TextView.setSpeedFormat(speed : Float){
    if(speed.isNaN() || speed <=0){
        text = "0 m/s"
        return
    }
    text = speed.speedFormat()
}
fun Float.speedFormat() : String{
    val kmInHour = this*(60*60)/1000F
    return if(kmInHour >= 1){ "${String.format("%.1f",kmInHour)} km/h" } else { "${String.format("%.1f",this)} m/s" }
}
@BindingAdapter("distanceFormat")
fun TextView.setDistanceFormat(distance : Float){
    if(distance.isNaN() || distance <=0){
        text = "0 m"
        return
    }
    val distanceRound = distance.roundToInt()
    val km = distanceRound/1000
    val m = distanceRound%1000
    var result = ""
    if(km > 0) result += "$km km "
    if(m>0) result += "$m m"
    text = result
}
@BindingAdapter("distance","time")
fun TextView.avgSpeed(distance: Float,time : Float){
    if(distance.isNaN() || time.isNaN() || time <=0){
        text = "0 m/s"
        return
    }
    val avgSpeed = distance/time
    text = avgSpeed.speedFormat()
}