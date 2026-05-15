package com.example.lokaal.ui.screens.map.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lokaal.domain.model.Moment
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    moments: List<Moment>,
    userLocation: GeoPoint?,
    onMarkerClick: (Moment) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val defaultLocation = GeoPoint(30.0444, 31.2357)  // Cairo

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(12.0)
            controller.setCenter(defaultLocation)
        }
    }

    // Move to user location when available
    LaunchedEffect(userLocation) {
        userLocation?.let {
            mapView.controller.animateTo(it)
            mapView.controller.setZoom(14.0)
        }
    }

    // Add moment markers
    LaunchedEffect(moments) {
        mapView.overlays.clear()

        moments.forEach { moment ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(moment.latitude, moment.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = moment.caption
                snippet = moment.locationName
                setOnMarkerClickListener { _, _ ->
                    onMarkerClick(moment)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}