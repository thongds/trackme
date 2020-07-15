/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.record

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dst.trackme.data.local.TrackMeDao
import com.dst.trackme.data.local.TrackMeDatabase
import com.dst.trackme.databinding.RecordFragmentBinding
import com.dst.trackme.service.LocationService
import com.dst.trackme.service.TimerCountUp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class RecordFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: RecordViewModel
    private lateinit var mapView : MapView
    private lateinit var binding: RecordFragmentBinding
    private lateinit var  trackerDao : TrackMeDao
    private val job = Job()
    private val recordCoroutineScope = CoroutineScope(Dispatchers.Main+job)
    private var sessionId : Long = -1
    private var mGoogleMap : GoogleMap? = null
    private var locationService: LocationService? = null
    private var foregroundOnlyLocationServiceBound = false
    private val polylineOptions = PolylineOptions().clickable(true).width(5F).visible(true)
    private val latLngBuilder = LatLngBounds.builder()
    private val args: RecordFragmentArgs by navArgs()
    private var firstPlace : LatLng? = null
    // should we subscribeToLocation which decided by view
    private var shouldSubscribe = true
    private val foregroundOnlyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            if(shouldSubscribe)
                locationService?.subscribeToLocationUpdates(sessionId)
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionId = args.sessionId
        trackerDao = TrackMeDatabase.getInstance(requireContext()).trackMeDatabaseDao
        setHasOptionsMenu(true)

    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)
        requireActivity().bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showConfirmDialog()
            }
        })
    }

    fun showConfirmDialog(){
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("Do you want to save this record?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            recordCoroutineScope.launch {
                viewModel.saveSession()
                killService()
                findNavController().navigateUp()
            }
        }
        alertDialog.setNegativeButton("No"){_,_ ->
            recordCoroutineScope.launch {
                viewModel.deleteSession()
                killService()
                findNavController().navigateUp()
            }
        }
        alertDialog.show()
    }
    // detect click back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showConfirmDialog()
        return super.onOptionsItemSelected(item)
    }

    private fun killService(){
        val stopIntent = Intent(requireActivity(),LocationService::class.java)
        stopIntent.putExtra(LocationService.EXTRA_CANCEL_LOCATION_TRACKING, true)
        requireActivity().startService(stopIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecordFragmentBinding.inflate(inflater,container,false)
        val viewModelFactory = RecordViewModelFactory(sessionId,trackerDao)
        viewModel = ViewModelProvider(this,viewModelFactory).get(RecordViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // stop record and turn back home fragment
        viewModel.stopRecordAction.observe(viewLifecycleOwner, Observer {
            if(it){
               showConfirmDialog()
            }
        })
        //detect start and stop record action
        viewModel.startRecord.observe(viewLifecycleOwner, Observer {startRecord ->
            if (startRecord){
                shouldSubscribe = true
                locationService?.subscribeToLocationUpdates(sessionId)
            }
            else{
                shouldSubscribe = false
                locationService?.unsubscribeToLocationUpdates()
            }
        })
        // update router when received new location
        viewModel.latLnLastUpdate.observe(viewLifecycleOwner, Observer {latLongEntry ->
            latLongEntry?.let {
                val lastLn = LatLng(it.lat,it.lng)
                if(firstPlace == null){
                    firstPlace = lastLn
                    mGoogleMap?.addMarker(MarkerOptions().position(firstPlace!!).icon(BitmapDescriptorFactory.defaultMarker()))
                }
                polylineOptions.add(lastLn)
                mGoogleMap?.let { map->
                    with(map){
                        latLngBuilder.include(lastLn)
                        map.addPolyline(polylineOptions)
                        val camera = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(),50)
                        animateCamera(camera)
                    }
                }
            }
        })
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        mGoogleMap!!.addPolyline(polylineOptions)
        mGoogleMap!!.isMyLocationEnabled = true
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            requireActivity().unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}