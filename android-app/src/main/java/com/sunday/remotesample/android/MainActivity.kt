package com.sunday.remotesample.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.remote.player.view.RemoteComposePlayer
import com.sunday.remotesample.documents.RemoteDocument
import com.sunday.remotesample.documents.RemoteDocumentFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RemoteComposeScreen() }
    }
}

@Composable
private fun RemoteComposeScreen() {
    var document by remember { mutableStateOf<ByteArray?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF4F8FB)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    when {
                        isLoading -> CircularProgressIndicator(color = Color(0xFF2878B8))
                        document != null -> RemoteDocumentPlayer(document!!)
                        error != null -> Text(error!!, color = Color(0xFF9B2C2C))
                        else -> Text("Tap below to fetch a remote layout", color = Color(0xFF486581))
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2878B8)),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            error = null
                            try {
                                document = fetchRemoteDocument()
                            } catch (failure: Exception) {
                                Log.e(TAG, "Couldn't load remote Compose document", failure)
                                error = "Couldn't load layout: ${failure.message ?: failure::class.simpleName}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                ) {
                    Text(if (document == null) "Surprise me" else "Show another")
                }
            }
        }
    }
}

private suspend fun fetchRemoteDocument(): ByteArray = withContext(Dispatchers.IO) {
    val connection = URL(BuildConfig.REMOTE_COMPOSE_URL).openConnection() as HttpURLConnection
    try {
        connection.connectTimeout = 5_000
        connection.readTimeout = 5_000
        connection.requestMethod = "GET"
        connection.connect()
        val statusCode = connection.responseCode
        if (statusCode !in 200..299) {
            val reason = connection.responseMessage?.let { " $it" }.orEmpty()
            throw IllegalStateException("HTTP $statusCode$reason")
        }
        connection.inputStream.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8 * 1024)
            while (true) {
                val bytesRead = input.read(buffer)
                if (bytesRead == -1) break
                require(output.size() + bytesRead <= MAX_DOCUMENT_SIZE) { "Remote document exceeds size limit" }
                output.write(buffer, 0, bytesRead)
            }
            val document = output.toByteArray()
            require(document.isNotEmpty()) { "Remote document was empty" }
            document
        }
    } finally {
        connection.disconnect()
    }
}

@Composable
@Suppress("RestrictedApi")
private fun RemoteDocumentPlayer(document: ByteArray, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = ::RemoteComposePlayer,
        update = { player -> player.setDocument(document) },
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun DigitalClockPreview() = RemotePreview(RemoteDocument.CLOCK)

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun WeatherCardPreview() = RemotePreview(RemoteDocument.WEATHER)

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun PostItNotePreview() = RemotePreview(RemoteDocument.NOTE)

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun MessageCardPreview() = RemotePreview(RemoteDocument.MESSAGE)

@Composable
private fun RemotePreview(document: RemoteDocument) {
    RemoteDocumentPlayer(RemoteDocumentFactory().create(document))
}

private const val MAX_DOCUMENT_SIZE = 512 * 1024
private const val TAG = "RemoteComposeSample"
