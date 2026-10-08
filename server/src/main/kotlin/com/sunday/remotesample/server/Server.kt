package com.sunday.remotesample.server

import com.sunday.remotesample.documents.RemoteDocument
import com.sunday.remotesample.documents.RemoteDocumentFactory
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO

fun main() {
    val documentFactory = RemoteDocumentFactory()
    val documents = RemoteDocument.entries
    var nextDocumentIndex = 0

    embeddedServer(CIO, port = System.getenv("PORT")?.toIntOrNull() ?: 8080, host = "0.0.0.0") {
        routing {
            get("/remote-compose") {
                val document = synchronized(documents) {
                    documents[nextDocumentIndex++ % documents.size]
                }
                call.respondBytes(
                    bytes = documentFactory.create(document),
                    contentType = ContentType.Application.OctetStream,
                )
            }
        }
    }.start(wait = true)
}
