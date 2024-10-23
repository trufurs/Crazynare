import kotlinx.serialization.Serializable

@Serializable
data class SocketData(
    val type: String,
    val content: String = "",
    val playerName: String? = null,
    val playerType: String? = null,
    val playerID: Int? = null,
    val playerPenalty: Int? = null,
    val playerStatus: String? = null,
    val originIp: String // Include origin IP address in the message
)
