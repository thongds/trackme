/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.history

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dst.trackme.data.local.SessionEntry
import com.dst.trackme.databinding.HistoryItemBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.dst.trackme.R
import com.dst.trackme.ui.util.bitmapDescriptorFromVector

class HistoryAdapter : ListAdapter<SessionEntry,HistoryAdapter.ViewHolder>(SessionEntryDiffCallback()) {
    private lateinit var context: Context
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        OnMapReadyCallback {

        private val mapView: MapView = binding.liteListrowMap
        private lateinit var map: GoogleMap
        private lateinit var startSessionLatLng: LatLng
        private lateinit var endSessionLatLng: LatLng

        init {
            with(mapView) {
                // Init the MapView
                onCreate(null)
                getMapAsync(this@ViewHolder)
            }
        }

        private fun setMapLocation() {
            if (!::map.isInitialized) return
            val startLatLn = startSessionLatLng
            val endLatLb = endSessionLatLng
            with(map) {
                val bitmapDescriptor = BitmapDescriptorFactory.defaultMarker()
                val stopIcon = context.bitmapDescriptorFromVector(R.drawable.ic_local_dot)
                addMarker(MarkerOptions().position(startLatLn).icon(bitmapDescriptor))
                addMarker(MarkerOptions().position(endLatLb).icon(stopIcon))

                addPolyline(PolylineOptions()
                    .clickable(true).width(5F).visible(true)
                    .add(startLatLn,endLatLb
                        ))
                // Move the googleMap to center
                val latLngBuilder = LatLngBounds.builder()
                latLngBuilder.include(startLatLn)
                latLngBuilder.include(endLatLb)
                val camera = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(),50)
                moveCamera(camera)
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }

        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(context)
            map = googleMap ?: return
            setMapLocation()
        }

        fun bindView(session: SessionEntry) {
            startSessionLatLng = LatLng(session.startLat,session.startLn)
            endSessionLatLng = LatLng(session.endLat,session.endLn)
            binding.sessionEntry = session
            //binding.executePendingBindings()
            setMapLocation()
        }

        fun clearView() {
            if (!::map.isInitialized) return
            with(map) {
                // Clear the map and free up resource
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }
    }
}
class SessionEntryDiffCallback : DiffUtil.ItemCallback<SessionEntry>(){
    override fun areContentsTheSame(oldItem: SessionEntry, newItem: SessionEntry): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: SessionEntry, newItem: SessionEntry): Boolean {
        return oldItem.sessionId == newItem.sessionId
    }
}