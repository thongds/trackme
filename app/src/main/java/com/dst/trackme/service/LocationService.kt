/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dst.trackme.R
import com.dst.trackme.data.local.LatLongSessionEntry
import com.dst.trackme.data.local.TrackMeDao
import com.dst.trackme.data.local.TrackMeDatabase
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class LocationService  : Service() {

    lateinit var trackMeDao: TrackMeDao
    private val TAG = LocationService::class.simpleName
    private var configurationChange = false
    private var _sessionId = -1L
    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO+coroutineJob)
    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        trackMeDao = TrackMeDatabase.getInstance(applicationContext).trackMeDatabaseDao
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(30)
            fastestInterval = TimeUnit.SECONDS.toMillis(10)
            maxWaitTime = TimeUnit.MINUTES.toMillis(1)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                if (locationResult?.lastLocation != null) {
                    Log.d(TAG,  "update location "+locationResult.lastLocation.longitude.toString())
                    currentLocation = locationResult.lastLocation
                    if (_sessionId >= 0){
                        currentLocation?.let {local->
                            val latLongSessionEntry = LatLongSessionEntry(
                               lat = local.latitude,lng = local.longitude,currentSpeed = local.speed,sessionId = _sessionId
                            )
                            coroutineScope.launch {
                                val lastLocation = trackMeDao.getLastLatLongOfSessionRawValue(_sessionId)
                                lastLocation?.let {
                                    val result = FloatArray(3)
                                    Location.distanceBetween(it.lat,it.lng,local.latitude,local.longitude,result)
                                    val sessionEntry = trackMeDao.getSessionByIdRawValue(_sessionId)
                                    sessionEntry?.let {session->
                                        session.distance = abs(result[0]) +session.distance
                                        trackMeDao.updateSession(session)
                                    }
                                }
                                trackMeDao.insertLatLng(latLongSessionEntry)
                            }
                        }

                    }
                    // Updates notification content if this service is running as a foreground service
                    if (serviceRunningInForeground) {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification()
                        )
                    }
                } else {
                    Log.d(TAG, "Location information isn't available.")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING, false)
        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        // not to recreate the service after it's been killed
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind()")
        //app became foreground => remove notification
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }
    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")
        //app became foreground => remove notification
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }
    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")
        //app became foreground or just config change
        if (!configurationChange) {
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification()
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }
        // return true mean client can rebind service
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    // provide function which client can control location update
    fun subscribeToLocationUpdates(sessionId : Long) {
        Log.d(TAG, "subscribeToLocationUpdates()")
        _sessionId = sessionId
        // make sure service started
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "local permission error")
        }
    }

    fun unsubscribeToLocationUpdates() {
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    stopSelf()
                } else {
                    Log.d(TAG, "remove location callback error")
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "local permission error")
        }
    }

    private fun generateNotification(): Notification {
        Log.d(TAG, "generateNotification()")
        val mainNotificationText = "We are tracking your location"
        val titleText = getString(R.string.app_name)
        // create notification for api 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)
        val cancelIntent = Intent(this, LocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING, true)
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    companion object {
        private const val PACKAGE_NAME = "com.dst.trackme.service"
        const val EXTRA_CANCEL_LOCATION_TRACKING =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
        private const val NOTIFICATION_ID = 12345678
        private const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
    }
}
