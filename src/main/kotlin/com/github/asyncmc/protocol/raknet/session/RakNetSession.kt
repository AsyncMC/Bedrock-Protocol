package com.github.asyncmc.protocol.raknet.session

import com.github.asyncmc.protocol.raknet.RakNetServer
import java.net.SocketAddress

class RakNetSession(
        val clientSocket: SocketAddress,
        val protocolServer: RakNetServer
)
