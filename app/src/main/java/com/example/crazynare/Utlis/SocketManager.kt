package com.example.crazynare.Utlis

import SocketData
import android.app.Application
import android.util.Log
import com.example.crazynare.Model.Players
import com.example.crazynare.Viewmodel.PlayerViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class SocketManager(private val application: Application) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _messages = MutableSharedFlow<SocketData>()
    val messages: SharedFlow<SocketData> = _messages.asSharedFlow()

    private var serverSocket: ServerSocket? = null
    private val clientSockets = mutableListOf<Socket>()
    var clientSocket: Socket? = null
    var isHost: Boolean = false
    private val json = Json { ignoreUnknownKeys = true }

    // Optimized server logic for host to accept clients and broadcast player info
    fun startServer(port: Int, playerViewModel: PlayerViewModel, onResult: (Boolean) -> Unit): Job {
        isHost = true
        return scope.launch {
            try {
                serverSocket = ServerSocket(port).apply { reuseAddress = true }
                Log.d("SocketManager", "Server started on port $port")
                withContext(Dispatchers.Main) {
                    playerViewModel.clearPlayers()
                    playerViewModel.addPlayer(Players(
                        playerName = "Player 1",
                        playerType = "host",
                        playerID = 1,
                        playerPenalty = 0,
                        playerStatus = true,
                        originIp = serverSocket!!.inetAddress.hostAddress!!.toString()
                    )
                    )
                    onResult(true)
                }
                playerViewModel.maxid = 1

                while (true) {
                    val clientSocket = serverSocket?.accept() ?: break
                    Log.i("ClientSocket", "Accepted ${clientSocket.inetAddress.hostAddress}")
                    clientSockets.add(clientSocket)

                    // Add new client player data to PlayerViewModel
                    playerViewModel.maxid += 1
                    val newPlayer = Players(
                        playerName = "Player ${playerViewModel.maxid }",  // Example, replace with actual data if needed
                        playerType = "guest",
                        playerID = playerViewModel.maxid ,  // Example player ID, you can adjust as per your logic
                        playerPenalty = 0,
                        playerStatus = true,  // Adjust based on the player status
                        originIp = clientSocket.inetAddress.hostAddress?.toString() ?: "::"
                    )

                    // Update PlayerViewModel with the new player
                    withContext(Dispatchers.Main){ playerViewModel.addPlayer(newPlayer)}

                    // Broadcast only the newly added player to all clients
                    val newPlayerData = SocketData(
                        type = "player",
                        playerName = newPlayer.playerName,
                        playerType = newPlayer.playerType,
                        playerID = newPlayer.playerID,
                        playerPenalty = newPlayer.playerPenalty,
                        playerStatus = newPlayer.playerStatus.toString(),
                        originIp = newPlayer.originIp
                    )
                    broadcastToAllExcept(newPlayerData, newPlayer.originIp)

                    // Send the complete list of players to the newly connected client
                    sendCompletePlayerListToClient(clientSocket, playerViewModel.players.value ?: emptyList())

                    // Handle further socket communication for this client
                    handleHostSocket(clientSocket)
                }
            } catch (e: IOException) {
                Log.e("SocketManager", "Server error: ${e.message}")
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            } finally {
                serverSocket?.close()
            }
        }
    }

    fun sendGamedata(question1 : String , question2 : String ,playerViewModel: PlayerViewModel){
        val players = playerViewModel.players.value ?: mutableListOf()

        if (players.size < 2) {
            Log.e("SocketManager", "Not enough players to send game data")
            return
        }

        // Get the list of player IDs excluding the sender's playerID
        val otherPlayerIds = players.filter { it.playerID != playerViewModel.playerId }.map { it.playerID }

        // Randomly select a playerID from the remaining IDs
        val randomPlayerId = otherPlayerIds.random()


        val data = SocketData(
            type = "game",
            content = question1,
            playerName = question2,
            playerID = randomPlayerId,
            playerPenalty = 0,
            playerStatus = "false",
            playerType = if(isHost) "host" else "guest",
            originIp = if(isHost) serverSocket?.inetAddress?.hostAddress ?: "::" else clientSocket?.localAddress?.hostAddress ?: "unknown"
        )
        scope.launch {
            if (isHost) {
                _messages.emit(data)
                broadcastToAllExcept(data, data.originIp)
            } else {
                val writer = PrintWriter(clientSocket?.getOutputStream()!!, true)
                val jsonMessage = json.encodeToString(data)
                writer.println(jsonMessage)
            }
        }
    }

    // Function to send the complete player list to the newly joined client
    private fun sendCompletePlayerListToClient(socket: Socket, players: List<Players>) {
        scope.launch {
            val writer = PrintWriter(socket.getOutputStream(), true)

            // Send each player's data
            players.forEach { player ->
                val socketData = SocketData(
                    type = "player",
                    playerName = player.playerName,
                    playerType = player.playerType,
                    playerID = player.playerID,
                    playerPenalty = player.playerPenalty,
                    playerStatus = player.playerStatus.toString(),
                    originIp = player.originIp
                )
                val jsonMessage = json.encodeToString(socketData)
                writer.println(jsonMessage) // Send player data to the client
            }
        }
    }


    // Client connection logic to connect to server
    fun connectToServer(host: String, port: Int , onResult: (Boolean) -> Unit): Job {
        isHost = false
        return scope.launch {
            try {
                clientSocket = Socket().apply {
                    connect(InetSocketAddress(host, port), 5000)
                }
                Log.i("ClientSocket", "Accepted ${clientSocket!!.inetAddress.hostAddress}")
                handleClientSocket(clientSocket!!)
                withContext(Dispatchers.Main) { onResult(true) }
            } catch (e: IOException) {
                Log.e("SocketManager", "Client connection error: ${e.message}")
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }

    // Host-side socket handling logic
    private fun handleHostSocket(socket: Socket) {
        scope.launch {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            Log.i("SocketManager", "Handling host socket for ${socket.inetAddress.hostAddress}")

            // Listening for incoming messages from clients
            launch {
                try {
                    while (isActive) {
                        val message = reader.readLine()
                        if (message != null) {
                            val socketData = json.decodeFromString<SocketData>(message)
                            _messages.emit(socketData) // Emit incoming data through SharedFlow
                            // Broadcast message to all other clients except the sender
                            broadcastToAllExcept(socketData, socket.inetAddress.hostAddress!!)
                        } else {
                            Log.w("SocketManager", "Socket closed by peer: ${socket.inetAddress.hostAddress}")
                            break // Socket disconnected
                        }
                    }
                } catch (e: IOException) {
                    Log.e("SocketManager", "Error reading from host socket: ${e.message}")
                } finally {
                    socket.close()
                    clientSockets.remove(socket)
                }
            }
        }
    }

    // Client-side socket handling logic
    private fun handleClientSocket(socket: Socket) {
        scope.launch {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            Log.i("SocketManager", "Handling client socket for ${socket.inetAddress.hostAddress}")

            // Listening for incoming messages from the server
            launch {
                try {
                    while (isActive) {
                        val message = reader.readLine()
                        if (message != null) {
                            val socketData = json.decodeFromString<SocketData>(message)
                            _messages.emit(socketData) // Emit incoming data through SharedFlow
                        } else {
                            Log.w("SocketManager", "Socket closed by server: ${socket.inetAddress.hostAddress}")
                            break // Socket disconnected
                        }
                    }
                } catch (e: IOException) {
                    Log.e("SocketManager", "Error reading from client socket: ${e.message}")
                } finally {
                    socket.close()
                }
            }
        }
    }

    // Function to send player data
    fun sendPlayerData(player: Players) {
        scope.launch {
            val clientIp :String = if(isHost) "::" else clientSocket?.localAddress?.hostAddress.toString()
            val socketData = SocketData(
                type = "player",
                playerName = player.playerName,
                playerType = player.playerType,
                playerID = player.playerID,
                playerPenalty = player.playerPenalty,
                playerStatus = player.playerStatus.toString(),
                originIp = clientIp
            )

            if (isHost) {
                // Host will broadcast to other clients
                _messages.emit(socketData)
                broadcastToAllExcept(socketData, clientIp ?: "::")
            } else {
                // Client sends the message to the host
                val writer = PrintWriter(clientSocket?.getOutputStream()!!, true)
                val jsonMessage = json.encodeToString(socketData)
                writer.println(jsonMessage) // Send to host

                // The client should not emit its own messages to avoid duplication
                // Only emit when the host broadcasts back
            }
        }
    }

    // Function to send a message
    fun sendMessage(message: String) {
        scope.launch {
            val clientIp = clientSocket?.localAddress?.hostAddress
            val socketData = SocketData("message", message, originIp = clientIp ?: "unknown")

            if (isHost) {
                // Host will broadcast to other clients
                _messages.emit(socketData)
                broadcastToAllExcept(socketData, clientIp ?: "")
            } else {
                // Client sends the message to the host
                val writer = PrintWriter(clientSocket?.getOutputStream(), true)
                val jsonMessage = json.encodeToString(socketData)
                writer.println(jsonMessage) // Send to host

                // The client should not emit its own messages to avoid duplication
                // Only emit when the host broadcasts back
            }
        }
    }

    // Broadcast message to all clients except the sender
    private fun broadcastToAllExcept(message: SocketData, senderIp: String) {
        val jsonMessage = json.encodeToString(message)
        clientSockets.forEach { client ->
            try {
                if (client.inetAddress.hostAddress != null) { // Only send to other clients
                    val writer = PrintWriter(client.getOutputStream(), true)
                    writer.println(jsonMessage)
                }
            } catch (e: Exception) {
                Log.e("SocketManager", "Broadcast error: ${e.message}")
            }
        }
    }

    // Disconnect logic
    fun disconnect() {
        try {
            if (isHost) {
                serverSocket?.close()
                clientSockets.forEach { it.close() }
                clientSockets.clear()
            } else {
                clientSocket?.close()
            }
        } catch (e: IOException) {
            Log.e("SocketManager", "Error closing socket: ${e.message}")
        } finally {
            serverSocket = null
            clientSocket = null
        }
    }
}
