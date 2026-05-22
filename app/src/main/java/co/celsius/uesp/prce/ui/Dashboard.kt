package co.celsius.uesp.prce.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.celsius.uesp.prce.diagnostics.JouganTelemetryCore
import co.celsius.uesp.prce.data.DaikokutenProtocolMatrix
import co.celsius.uesp.prce.engine.AethericResonanceBridge
import co.celsius.uesp.prce.engine.InfraredTransmitterCore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalRemoteDashboard(irCore: InfraredTransmitterCore) {
    val coroutineScope = rememberCoroutineScope()
    val resonanceBridge = remember { AethericResonanceBridge() }

    var userIntentPrompt by remember { mutableStateOf("Optimize cooling matrix for late-night layout") }
    var runningStatusLog by remember { mutableStateOf("System Ready") }
    var targetDeviceSelection by remember { mutableStateOf("AIR_CONDITIONER") }

    val ambientLightLux = 45.0f
    val systemHour = 22 

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFF7B2CBF), shape = RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("UESP CODESPACE", color = Color(0xFF7B2CBF), fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("Jougan Analysis x Daikokuten Protocol Storage", color = Color(0xFF3C096C), fontSize = 10.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { targetDeviceSelection = "AIR_CONDITIONER" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (targetDeviceSelection == "AIR_CONDITIONER") Color(0xFF7B2CBF) else Color(0xFFE0AAFF).copy(alpha = 0.4f))
                ) { Text("AC Core", color = if (targetDeviceSelection == "AIR_CONDITIONER") Color.White else Color(0xFF3C096C)) }

                Button(
                    onClick = { targetDeviceSelection = "TELEVISION" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (targetDeviceSelection == "TELEVISION") Color(0xFF7B2CBF) else Color(0xFFE0AAFF).copy(alpha = 0.4f))
                ) { Text("TV Array", color = if (targetDeviceSelection == "TELEVISION") Color.White else Color(0xFF3C096C)) }
            }

            OutlinedTextField(
                value = userIntentPrompt,
                onValueChange = { userIntentPrompt = it },
                label = { Text("AI Macro Parameter Intent") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(0xFF7B2CBF), unfocusedBorderColor = Color(0xFFE0AAFF))
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        runningStatusLog = "Jougan auditing telemetry..."
                        val currentTelemetry = JouganTelemetryCore.TelemetryState(ambientLightLux, systemHour, targetDeviceSelection, "ACTIVE")
                        
                        if (!JouganTelemetryCore.structuralAudit(currentTelemetry)) {
                            runningStatusLog = "Jougan caught invalid metrics."
                            return@launch
                        }

                        runningStatusLog = "Querying NVIDIA NIM network..."
                        val remotePattern = resonanceBridge.getAiInferredIrPattern(targetDeviceSelection, userIntentPrompt, ambientLightLux, systemHour)

                        if (remotePattern != null && remotePattern.isNotEmpty()) {
                            runningStatusLog = "NIM resolved. Transmitting..."
                            irCore.transmitSignal(DaikokutenProtocolMatrix.FREQUENCY_NEC, remotePattern)
                            runningStatusLog = "Inference command dispatched successfully."
                        } else {
                            runningStatusLog = "NIM timeout. Deploying Daikokuten fallback..."
                            val localFallback = DaikokutenProtocolMatrix.resolveFallback(targetDeviceSelection)
                            irCore.transmitSignal(DaikokutenProtocolMatrix.FREQUENCY_NEC, localFallback)
                            runningStatusLog = "Local Daikokuten backup pattern sent."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF240046))
            ) {
                Text("RESOLVE & TRANSMIT PULSE", color = Color(0xFFE0AAFF), fontFamily = FontFamily.Monospace)
            }

            Text(
                text = runningStatusLog, color = Color(0xFF3C096C), fontSize = 11.sp, fontFamily = FontFamily.Monospace,
                modifier = Modifier.fillMaxWidth().background(Color(0xFFE0AAFF).copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)).padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
