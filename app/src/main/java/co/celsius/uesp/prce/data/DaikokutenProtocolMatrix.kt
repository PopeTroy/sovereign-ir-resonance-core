package co.celsius.uesp.prce.data

object DaikokutenProtocolMatrix {
    
    const val FREQUENCY_NEC = 38000

    // Daikokuten Vault: Pre-compiled local standard matrices for immediate structural backup
    val BACKUP_TV_POWER = intArrayOf(9000, 4500, 560, 560, 560, 1690, 560, 560, 560, 560, 560, 1690, 560, 1690, 560, 560)
    val BACKUP_AC_SHUTDOWN = intArrayOf(8400, 4200, 420, 420, 420, 1600, 420, 1600, 420, 420, 420, 420, 420, 5000)

    fun resolveFallback(device: String): IntArray {
        return if (device == "TELEVISION") BACKUP_TV_POWER else BACKUP_AC_SHUTDOWN
    }
}
