package com.github.asyncmc.protocol.raknet.packet

import com.github.asyncmc.protocol.raknet.RakNetServer
import io.ktor.network.sockets.Datagram
import io.ktor.utils.io.core.*
import java.net.SocketAddress

object RakNetPacketUnconnectedPing: RakNetPacketHandler(ID_NOT_CONNECTED_PING) {
    private val SIZE = NOT_CONNECTED_MAGIC.size + 8L

    override fun handleNoSession(server: RakNetServer, sender: SocketAddress, data: ByteReadPacket) {
        if (data.remaining < SIZE) {
            return
        }

        val sentTick = data.readLong()
        val magic = data.readBytes(NOT_CONNECTED_MAGIC.size)
        if (!NOT_CONNECTED_MAGIC.contentEquals(magic)) {
            return
        }

        val userData = server.listener.onPingFromDisconnected(server, sender, sentTick)

        val response = buildPacket(35 + userData.size) {
            writeUByte(ID_NOT_CONNECTED_PONG)
            writeLong(sentTick)
            writeLong(server.guid)
            writeFully(NOT_CONNECTED_MAGIC)
            writeUShort(userData.size.toUShort())
            writeFully(userData)
        }

        server.send(Datagram(response, sender))
    }
}
