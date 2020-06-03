package com.github.asyncmc.protocol.raknet.packet

import com.github.asyncmc.protocol.raknet.RakNetServer
import com.github.asyncmc.protocol.raknet.session.RakNetSession
import io.ktor.utils.io.core.ByteReadPacket
import java.net.SocketAddress

abstract class RakNetPacketHandler(val packetId: UByte) {
    open fun handleNoSession(server: RakNetServer, sender: SocketAddress, data: ByteReadPacket) {
        // Does nothing by default
    }

    open fun handleSession(server: RakNetServer, session: RakNetSession, data: ByteReadPacket) {
        // Does nothing by default
    }

    companion object {
        val NOT_CONNECTED_MAGIC = byteArrayOf(0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120)

        const val ID_CONNECTED_PING: UByte = 0x00u
        const val ID_NOT_CONNECTED_PING: UByte = 0x01u
        const val ID_UNCONNECTED_PING_OPEN_CONNECTIONS: UByte = 0x02u
        const val ID_CONNECTED_PONG: UByte = 0x03u
        const val ID_DETECT_LOST_CONNECTION: UByte = 0x04u
        const val ID_OPEN_CONNECTION_REQUEST_1: UByte = 0x05u
        const val ID_OPEN_CONNECTION_REPLY_1: UByte = 0x06u
        const val ID_OPEN_CONNECTION_REQUEST_2: UByte = 0x07u
        const val ID_OPEN_CONNECTION_REPLY_2: UByte = 0x08u
        const val ID_CONNECTION_REQUEST: UByte = 0x09u
        const val ID_CONNECTION_REQUEST_ACCEPTED: UByte = 0x10u
        const val ID_CONNECTION_REQUEST_FAILED: UByte = 0x11u
        const val ID_ALREADY_CONNECTED: UByte = 0x12u
        const val ID_NEW_INCOMING_CONNECTION: UByte = 0x13u
        const val ID_NO_FREE_INCOMING_CONNECTIONS: UByte = 0x14u
        const val ID_DISCONNECTION_NOTIFICATION: UByte = 0x15u
        const val ID_CONNECTION_LOST: UByte = 0x16u
        const val ID_CONNECTION_BANNED: UByte = 0x17u
        const val ID_INCOMPATIBLE_PROTOCOL_VERSION: UByte = 0x19u
        const val ID_IP_RECENTLY_CONNECTED: UByte = 0x1au
        const val ID_TIMESTAMP: UByte = 0x1bu
        const val ID_NOT_CONNECTED_PONG: UByte = 0x1cu
        const val ID_ADVERTISE_SYSTEM: UByte = 0x1du
        const val ID_USER_PACKET_ENUM: UByte = 0x80u

        val byPacketId = listOf(
                RakNetPacketUnconnectedPing
        ).associateBy { it.packetId }
    }
}
