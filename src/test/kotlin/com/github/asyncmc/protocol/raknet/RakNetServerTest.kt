package com.github.asyncmc.protocol.raknet

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.net.InetSocketAddress
import java.util.*
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
internal class RakNetServerTest {
    @Mock
    lateinit var listener: RakNetListener

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val listener = mock<RakNetListener> {
                on { onPingFromDisconnected(any(), any(), any()) }.thenReturn(
                        StringJoiner(";").apply {
                            add("MCPE")
                            add("Test line Line 1")
                            add("390")
                            add("1.14.60")
                            add("13")
                            add("40")
                            add(Random.nextLong().toString())
                            add("This is Line 2")
                            add("Survival")
                            add("1")
                        }.toString().toByteArray()
                )
            }
            val server = RakNetServer(InetSocketAddress(19132), listener)
            server.start()
            runBlocking {
                server.join()
            }
        }
    }
}
