/*
 * Copyright (c) 2020. All rights reserved.
 * created by thongds
 */

package com.dst.trackme.ui.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dst.trackme.BuildConfig
import com.dst.trackme.data.local.TrackMeDao
import com.dst.trackme.data.local.TrackMeDatabase
import com.dst.trackme.databinding.HistoryFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
class HistoryFragment : Fragment() {
    lateinit var binding : HistoryFragmentBinding
    private lateinit var viewModel: HistoryViewModel
    lateinit var trackMeDao : TrackMeDao
    private val fragmentJop = Job()
    private var loading = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main + fragmentJop )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackMeDao = TrackMeDatabase.getInstance(requireContext()).trackMeDatabaseDao
    }
    private val recycleListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as HistoryAdapter.ViewHolder
        mapHolder.clearView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HistoryFragmentBinding.inflate(inflater,container,false)
        binding.recordBtn.setOnClickListener {
           openRecordFragment()
        }
        val historyViewModelFactory = HistoryViewModelFactory(trackMeDao)
        binding.historyRyc.setRecyclerListener(recycleListener)
        viewModel = ViewModelProvider(this,historyViewModelFactory).get(HistoryViewModel::class.java)
        binding.historyViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val adapter = HistoryAdapter()
        binding.historyRyc.adapter  = adapter
        viewModel.sessionData.observe(viewLifecycleOwner, Observer {
            loading = false
            adapter.submitList(it?.toMutableList())
        })
        // detecting end scroll to load more data
        binding.historyRyc.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (loading) return
                val layoutManager = binding.historyRyc.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val pastVisibleItems: Int = layoutManager.findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    if (!::viewModel.isInitialized) return
                    loading = true
                    viewModel.getMoreSessionPage()
                }
            }
        })
        return binding.root
    }
    private fun openRecordFragment(){
        coroutineScope.launch {
            if(foregroundPermissionApproved()){
                val sessionId = viewModel.createNewSession()
                findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToRecordFragment(sessionId))
            }else{
                requestForegroundPermissions()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    //user interaction was interrupted
                     showPermissionDeny()
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was accepted
                    openRecordFragment()
                }
                else -> {
                    // Permission denied
                    showPermissionDeny()
                }
            }
        }
    }
    private fun showPermissionDeny(){
        Snackbar.make(
            binding.mainHolder,
            "Location permission needed for core functionality",
            Snackbar.LENGTH_LONG
        )
            .setAction("settings") {
                //displays app setting
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()

    }
    private fun requestForegroundPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }
    // checks if permission approved
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        fragmentJop.cancel()
    }
}