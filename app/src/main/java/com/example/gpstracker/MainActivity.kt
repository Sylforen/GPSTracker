package com.example.gpstracker

//noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Text

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import org.xmlpull.v1.XmlPullParser

class MainActivity : ComponentActivity() {


    final val DEFAULT_UPDATE_INTERVAL = 30
    final val FAST_UPDATE_INTERVAL = 5



    @SuppressLint("WrongViewCast", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    DisplayXmlContent(xmlResId = R.layout.activity_main)
                }
            }


        }
        val updateOn = false

        // google's api for location services
        val fusedLocationProviderClient : FusedLocationProviderClient

        setContentView(R.layout.activity_main)
        val tv_lat: TextView = findViewById(R.id.tv_lat)
        val tv_lon: TextView = findViewById(R.id.tv_lon)
        val tv_altitutde: TextView = findViewById(R.id.tv_altitude)
        val tv_accuracy: TextView = findViewById(R.id.tv_accuracy)
        val tv_speed: TextView = findViewById(R.id.tv_speed)
        val tv_sensor: TextView = findViewById(R.id.tv_sensor)
        val tv_updates: TextView = findViewById(R.id.tv_updates)
        val tv_address: TextView = findViewById(R.id.tv_address)

        val sw_locationupdates : Switch = findViewById(R.id.sw_locationsupdates)
        val sw_gps : Switch = findViewById(R.id.sw_gps)

        // TODO: ADD DYNAMIC TOGGLE FOR DEFAULT INTERVAL + FAST INTERVAL
        var speed = DEFAULT_UPDATE_INTERVAL
        var locationRequest : LocationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY,30000).build()
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