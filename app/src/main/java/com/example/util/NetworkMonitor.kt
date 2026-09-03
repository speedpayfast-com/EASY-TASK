package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _isDeviceConnected = MutableStateFlow(checkInitialConnectivity())
    val isDeviceConnected: StateFlow<Boolean> = _isDeviceConnected.asStateFlow()

    private val _isSimulatedOffline = MutableStateFlow(false)
    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline.asStateFlow()

    // Effectively online if real device has internet AND simulated offline is NOT activated
    val isOnline: StateFlow<Boolean> = combine(_isDeviceConnected, _isSimulatedOffline) { connected, simulated ->
        connected && !simulated
    }.stateIn(scope, SharingStarted.Eagerly, true)

    val isOffline: StateFlow<Boolean> = combine(_isDeviceConnected, _isSimulatedOffline) { connected, simulated ->
        !connected || simulated
    }.stateIn(scope, SharingStarted.Eagerly, false)

    init {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager?.registerNetworkCallback(
                request,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isDeviceConnected.value = true
                    }

                    override fun onLost(network: Network) {
                        _isDeviceConnected.value = checkInitialConnectivity()
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities
                    ) {
                        val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        _isDeviceConnected.value = hasInternet
                    }
                }
            )
        } catch (_: Exception) {
            // Fallback: default to true for testing environments if callback registration is unsupported
            _isDeviceConnected.value = true
        }
    }

    fun toggleSimulatedOffline(offline: Boolean? = null) {
        if (offline != null) {
            _isSimulatedOffline.value = offline
        } else {
            _isSimulatedOffline.value = !_isSimulatedOffline.value
        }
    }

    private fun checkInitialConnectivity(): Boolean {
        val cm = connectivityManager ?: return true
        val activeNetwork = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
