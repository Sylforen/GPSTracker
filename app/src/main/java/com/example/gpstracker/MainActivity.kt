package com.example.gpstracker

//noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.Text

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.content.Context
import android.os.Bundle
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
import org.xmlpull.v1.XmlPullParser





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    DisplayXmlContent(xmlResId = R.layout.activity_main) // Replace with your XML resource ID
                }
            }
        }
        setContentView(R.layout.activity_main)
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