package com.sunday.remotesample.documents

import androidx.compose.remote.core.RcProfiles
import androidx.compose.remote.core.operations.TextFromFloat
import androidx.compose.remote.core.operations.layout.managers.BoxLayout
import androidx.compose.remote.core.operations.layout.managers.ColumnLayout
import androidx.compose.remote.core.operations.layout.managers.RowLayout
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.RemoteComposeContext
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.modifiers.RecordingModifier
import androidx.compose.remote.creation.modifiers.RoundedRectShape

@Suppress("RestrictedApi")
class RemoteDocumentFactory {
    fun create(document: RemoteDocument): ByteArray {
        val writer = RemoteComposeWriter(
            DOCUMENT_WIDTH,
            DOCUMENT_HEIGHT,
            document.description,
            DOCUMENT_API_LEVEL,
            RcProfiles.PROFILE_ANDROIDX,
            JvmRcPlatformServices(),
        )
        val context = RemoteComposeContext(writer)

        context.root {
            when (document) {
                RemoteDocument.CLOCK -> context.clock()
                RemoteDocument.WEATHER -> context.weather()
                RemoteDocument.NOTE -> context.note()
                RemoteDocument.MESSAGE -> context.message()
            }
        }

        return writer.encodeToByteArray()
    }

    private fun RemoteComposeContext.clock() {
        writer.beginGlobal()
        val hour = createTextFromFloat(Hour().toFloat(), 2, 0, TextFromFloat.PAD_PRE_ZERO)
        val minute = createTextFromFloat(
            (Minutes() % 60f).toFloat(),
            2,
            0,
            TextFromFloat.PAD_PRE_ZERO,
        )
        val second = createTextFromFloat(
            (Seconds() % 60f).toFloat(),
            2,
            0,
            TextFromFloat.PAD_PRE_ZERO,
        )
        val colon = textCreateId(":")
        val time = textMerge(hour, colon, minute, colon, second)
        writer.endGlobal()

        column(
            RecordingModifier().fillMaxSize().clip(CARD_SHAPE).background(NAVY).padding(28f),
            ColumnLayout.START,
            ColumnLayout.CENTER,
        ) {
            text("LOCAL TIME", RecordingModifier().padding(8f), color = MIST, fontSize = 18f)
            text(time, color = WHITE, fontSize = 96f, fontWeight = 700f)
            text("THURSDAY  /  OCTOBER 08", RecordingModifier().padding(8f), color = SKY, fontSize = 18f)
            text("A little more time for what matters.", RecordingModifier().padding(12f), color = MIST, fontSize = 18f)
        }
    }

    private fun RemoteComposeContext.weather() {
        column(
            RecordingModifier().fillMaxSize().clip(CARD_SHAPE).background(BLUE).padding(24f),
            ColumnLayout.START,
            ColumnLayout.CENTER,
        ) {
            text("TODAY IN PARIS", color = MIST, fontSize = 18f)
            row(RecordingModifier().fillMaxWidth().padding(8f), RowLayout.SPACE_BETWEEN, RowLayout.CENTER) {
                text("18 C", color = WHITE, fontSize = 88f, fontWeight = 700f)
                box(RecordingModifier().background(0xFFFFD16A.toInt()), BoxLayout.CENTER, BoxLayout.CENTER) {
                    text("SUN", color = NAVY, fontSize = 18f, fontWeight = 700f)
                }
            }
            text("Mostly sunny", color = WHITE, fontSize = 26f)
            text("Feels like 19 C   HIGH 21 C   LOW 13 C", RecordingModifier().padding(4f), color = MIST, fontSize = 18f)
            text("A bright day to get outside.", RecordingModifier().padding(4f), color = WHITE, fontSize = 19f)
        }
    }

    private fun RemoteComposeContext.note() {
        box(
            RecordingModifier().fillMaxSize().clip(CARD_SHAPE).background(POSTIT_YELLOW),
            BoxLayout.CENTER,
            BoxLayout.CENTER,
        ) {
            column(
                RecordingModifier().fillMaxSize().padding(42f),
                ColumnLayout.START,
                ColumnLayout.CENTER,
            ) {
                text("A NOTE TO SELF", color = 0xFF715F32.toInt(), fontSize = 22f)
                text("Make room for\nthe good stuff.", RecordingModifier().padding(22f), color = NAVY, fontSize = 54f, fontWeight = 700f)
                text("One small step is still a step.", RecordingModifier().padding(22f), color = 0xFF715F32.toInt(), fontSize = 24f)
                text("- YOU", RecordingModifier().padding(30f), color = BLUE, fontSize = 22f, fontWeight = 700f)
            }
        }
    }

    private fun RemoteComposeContext.message() {
        column(
            RecordingModifier().fillMaxSize().clip(CARD_SHAPE).background(PALE_BLUE).padding(34f),
            ColumnLayout.START,
            ColumnLayout.CENTER,
        ) {
            box(RecordingModifier().width(72f).height(7f).background(BLUE))
            text("A small reminder", RecordingModifier().padding(24f), color = BLUE, fontSize = 23f, fontWeight = 700f)
            text("Good things take\ntime. You are right\non schedule.", RecordingModifier().padding(14f), color = NAVY, fontSize = 47f, fontWeight = 700f)
            text("Keep going. Your future self is cheering you on.", RecordingModifier().padding(20f), color = SLATE, fontSize = 24f)
        }
    }

    private companion object {
        const val DOCUMENT_WIDTH = 720
        const val DOCUMENT_HEIGHT = 360
        const val DOCUMENT_API_LEVEL = 7
        const val NAVY = 0xFF102A43.toInt()
        const val BLUE = 0xFF2878B8.toInt()
        const val SKY = 0xFF7CC4F2.toInt()
        const val PALE_BLUE = 0xFFE8F4FC.toInt()
        const val MIST = 0xFFD8E8F3.toInt()
        const val SLATE = 0xFF486581.toInt()
        const val WHITE = 0xFFFFFFFF.toInt()
        const val POSTIT_YELLOW = 0xFFF3D982.toInt()
        val CARD_SHAPE = RoundedRectShape(24f, 24f, 24f, 24f)
    }
}

enum class RemoteDocument(val description: String) {
    CLOCK("Digital clock"),
    WEATHER("Weather card"),
    NOTE("Post-it note"),
    MESSAGE("Reminder card"),
}
