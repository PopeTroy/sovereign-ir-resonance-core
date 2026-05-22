package co.celsius.uesp.prce.engine

import android.content.Context
import android.hardware.ConsumerIrManager
import android.util.Log

class InfraredTransmitterCore(private val context: Context) {

    private val irManager: ConsumerIrManager? = 
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    fun hasIrEmitter(): Boolean = irManager?.hasIrEmitter() == true

    fun transmitSignal(frequencyHz: Int, patternMicroseconds: IntArray) {
        if (!hasIrEmitter()) {
            Log.e("PRCE_IR", "Hardware emission call failed: No IR blaster present.")
            return
        }
        try {
            irManager?.transmit(frequencyHz, patternMicroseconds)
            Log.d("PRCE_IR", "Dispatched physical pulses. Total transitions: ${patternMicroseconds.size}")
        } catch (e: Exception) {
            Log.e("PRCE_IR", "Hardware substrate fault during pulse delivery", e)
        }
    }
}
