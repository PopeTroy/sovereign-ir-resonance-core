package co.celsius.uesp.prce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.celsius.uesp.prce.engine.InfraredTransmitterCore
import co.celsius.uesp.prce.ui.UniversalRemoteDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val irCore = InfraredTransmitterCore(this)
        
        setContent {
            UniversalRemoteDashboard(irCore = irCore)
        }
    }
}
