package com.github.asyncmc.protocol.raknet

import com.github.asyncmc.protocol.raknet.packet.RakNetPacketHandler
import com.github.asyncmc.protocol.raknet.session.RakNetSession
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.aSocket
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readUByte
import kotlinx.coroutines.*
import org.jctools.maps.NonBlockingHashMap
import org.jctools.maps.NonBlockingHashSet
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicBoolean

class RakNetServer(
        private val socketAddress: InetSocketAddress,
        internal val listener: RakNetListener
) {
    val guid = ThreadLocalRandom.current().nextLong()
    private val started = AtomicBoolean(false)
    private lateinit var binding: BoundDatagramSocket
    private lateinit var rakNetScope: CoroutineScope

    val job get() = rakNetScope.coroutineContext[Job]!!
    val blockedAddresses: MutableSet<InetAddress> = NonBlockingHashSet()
    val sessions: MutableMap<InetSocketAddress, RakNetSession> = NonBlockingHashMap()

    internal fun send(datagram: Datagram): Job {
        checkIsRunning()
        return rakNetScope.launch {
            binding.outgoing.send(datagram)
        }
    }

    private fun checkIsRunning() {
        if(!started.get()) {
            throw IOException("The server is not running")
        }
    }

    private fun handle(datagram: Datagram) {
        val packetId = datagram.packet.readUByte()
        val session = sessions[datagram.address]
        val handler = RakNetPacketHandler.byPacketId[packetId]
        if (handler == null) {
            listener.onUnknownDatagram(this, session, datagram)
            return
        }
        if (session != null) {
            handler.handleSession(this, session, datagram.packet)
        } else {
            handler.handleNoSession(this, datagram.address, datagram.packet)
        }
    }

    private suspend fun listen() {
        while (true) {
            try {
                val datagram = binding.incoming.receive()
                val address = datagram.address
                val packet = datagram.packet
                if (packet.isEmpty || address is InetSocketAddress && address.address in blockedAddresses) {
                    packet.release()
                    continue
                }
                coroutineScope {
                    launch(Dispatchers.Default) {
                        try {
                            handle(datagram)
                        } finally {
                            packet.release()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
                started.compareAndSet(true, false)
                throw e
            }
        }
    }

    @OptIn(KtorExperimentalAPI::class)
    fun start() {
        check(started.compareAndSet(false, true))
        try {
            binding = aSocket(ActorSelectorManager(Dispatchers.IO)).udp().bind(socketAddress)
        } catch (e: Throwable) {
            started.compareAndSet(true, false)
            throw e
        }

        rakNetScope = CoroutineScope(Dispatchers.IO)
        rakNetScope.launch {
            listen()
        }
    }

    fun stop(message: String? = null, cause: Throwable? = null) {
        try {
            rakNetScope.takeIf { it.isActive }?.cancel(CancellationException(message, cause))
        } finally {
            try {
                binding.close()
            } finally {
                started.compareAndSet(true, false)
            }
        }
    }

    suspend fun join() = job.join()
}
