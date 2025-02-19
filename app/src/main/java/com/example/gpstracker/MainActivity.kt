package com.example.gpstracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {

    final val DEFAULT_UPDATE_INTERVAL = 30
    final val FAST_UPDATE_INTERVAL = 5
    val PERMISSION_FINE_LOCATION: Int = 13

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var tv_lat: TextView
    lateinit var tv_lon: TextView
    lateinit var tv_altitutde: TextView
    lateinit var tv_accuracy: TextView
    lateinit var tv_speed: TextView
    lateinit var tv_sensor: TextView
    lateinit var tv_updates: TextView
    lateinit var tv_address: TextView

    lateinit var locationCallback: LocationCallback

    lateinit var geocoder : Geocoder

    //lateinit var


    //@SuppressLint("WrongViewCast", "UseSwitchCompatOrMaterialCode")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    DisplayXmlContent(xmlResId = R.layout.activity_main)
                }
            }
        }

        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        tv_lat = findViewById(R.id.tv_lat)
        tv_lon = findViewById(R.id.tv_lon)
        tv_altitutde = findViewById(R.id.tv_altitude)
        tv_accuracy = findViewById(R.id.tv_accuracy)
        tv_speed = findViewById(R.id.tv_speed)
        tv_sensor = findViewById(R.id.tv_sensor)
        tv_updates = findViewById(R.id.tv_updates)
        tv_address = findViewById(R.id.tv_address)

        val sw_locationupdates: Switch = findViewById(R.id.sw_locationsupdates)
        val sw_gps: Switch = findViewById(R.id.sw_gps)


        // TODO: ADD DYNAMIC TOGGLE FOR DEFAULT INTERVAL + FAST INTERVAL
        var speed = DEFAULT_UPDATE_INTERVAL
        var locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000).build()



        sw_gps.setOnClickListener {
            if (sw_gps.isChecked) {
                // most accurate - use GPS
                locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
                tv_sensor.text = "Using GPS sensor"
            } else {
                locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000).build()
                tv_sensor.text = "Using Towers + WiFi"
            }
        }

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                val location: Location? = p0.lastLocation
                if (location != null) {
                    updateUIValues(location)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun startLocationUpdates() {
            tv_updates.text = "Location is being tracked"

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            updateGPS()

        }


        fun stopLocationUpdates() {
            tv_updates.text = "Location is NOT being tracked"
            tv_lat.text = "Not tracking latitude"
            tv_lon.text = "Not tracking longitude"
            tv_speed.text = "Not tracking speed"
            tv_address.text = "Not tracking address"
            tv_altitutde.text = "Not tracking altitude"
            tv_sensor.text = "Not tracking sensor data"
            tv_accuracy.text = "Not tracking accuracy"

            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }


        sw_locationupdates.setOnClickListener {

            if (sw_locationupdates.isChecked) {
                // turn on location tracking
                startLocationUpdates()

            } else {
                // turn off location tracking
                stopLocationUpdates()

            }
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_FINE_LOCATION
            )
        } else {
            updateGPS()
        }
    } // end of onCreate()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS()
                } else {
                    //Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show()
                    //finish()
                    updateGPS()
                }

            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    fun updateGPS() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            // handle location object
            if (location != null) {
                // Update UI with location data
                updateUIValues(location)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun updateUIValues(location: Location) {
        // update all of the text view objects with a new location

        tv_lat.text = location.latitude.toString()
        tv_lon.text = location.longitude.toString()


        if (location.hasAltitude()) {
            tv_altitutde.text = location.altitude.toString()
        } else {
            tv_altitutde.text = "Not available on this device."
        }

        if (location.hasSpeed()) {
            tv_speed.text = location.speed.toString()
        } else {
            tv_speed.text = "Not available on this device."
        }

        if (location.hasAccuracy()) {
            tv_accuracy.text = location.accuracy.toString()
        } else {
            tv_accuracy.text = "Not available on this device"
        }


        val geocoder = Geocoder(this)
        geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address>) {
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    runOnUiThread {
                        tv_address.text = address.getAddressLine(0)
                    }
                } else {
                    runOnUiThread {
                        tv_address.text = "Address not found"
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                runOnUiThread {
                    tv_address.text = "Unable to get address"
                }
            }
        })

    }
}

@Composable
fun DisplayXmlContent(xmlResId: Int) {
    val context = LocalContext.current
    val xmlContent = parseXml(context, xmlResId)

    Column(modifier = Modifier.padding(16.dp)) {
        xmlContent.forEach { (tag, text) ->
            Text(text = "$tag: $text", fontSize = 16.sp)
        }
    }
}



fun parseXml(context: Context, xmlResId: Int): List<Pair<String, String>> {
    val xmlResourceParser = context.resources.getXml(xmlResId)
    val result = mutableListOf<Pair<String, String>>()
    var eventType = xmlResourceParser.eventType

    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            val tagName = xmlResourceParser.name
            if (xmlResourceParser.next() == XmlPullParser.TEXT) {
                val text = xmlResourceParser.text
                result.add(tagName to text)
            }
        }
        eventType = xmlResourceParser.next()
    }

    return result
}



@Preview(showBackground = true)
@Composable
fun PreviewDisplayXmlContent() {
    DisplayXmlContent(xmlResId = R.xml.activity_main) // Replace with your XML resource ID
}