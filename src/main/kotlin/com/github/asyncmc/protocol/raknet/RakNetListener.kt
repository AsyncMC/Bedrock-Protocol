package com.github.asyncmc.protocol.raknet

import com.github.asyncmc.protocol.raknet.session.RakNetSession
import io.ktor.network.sockets.Datagram
import java.net.SocketAddress

interface RakNetListener {
    fun onUnknownDatagram(server: RakNetServer, session: RakNetSession?, datagram: Datagram)
    fun onPingFromDisconnected(server: RakNetServer, sender: SocketAddress, sentTick: Long): ByteArray
}
