package co.celsius.uesp.prce.engine

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import android.util.Log

class AethericResonanceBridge {
    private val client = HttpClient(CIO)
    private val endpointUrl = "https://api.celsiusmediagroup.co.za/v1/prce/ir-resonance"

    suspend fun getAiInferredIrPattern(
        targetDevice: String,
        userIntent: String,
        ambientLightLux: Float,
        hourOfDay: Int
    ): IntArray? {
        return try {
            val jsonPayload = buildJsonObject {
                put("model", "meta/llama-3-sonar-large-32k-chat")
                put("temperature", 0.1)
                put("telemetry", buildJsonObject {
                    put("device_type", targetDevice)
                    put("intent_raw", userIntent)
                    put("ambient_lux", ambientLightLux.toDouble())
                    put("temporal_marker_hour", hourOfDay)
                })
            }

            val response: HttpResponse = client.post(endpointUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                header(HttpHeaders.Authorization, "Bearer PRCE_MEGA_SECRET_TOKEN_144K")
                setBody(jsonPayload.toString())
            }

            if (response.status == HttpStatusCode.OK) {
                val responseText = response.bodyAsText()
                val root = Json.parseToJsonElement(responseText).jsonObject
                val patternArray = root["inferred_ir_pattern"]?.jsonArray
                patternArray?.map { it.jsonPrimitive.int }?.toIntArray()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("PRCE_BRIDGE", "Nvidia NIM out-of-band communication interruption", e)
            null
        }
    }

    fun close() {
        client.close()
    }
}
