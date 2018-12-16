package org.team5499.dashboard

import org.eclipse.jetty.websocket.api.*
import org.eclipse.jetty.websocket.api.annotations.*
import java.io.*
import java.util.*
import java.util.concurrent.*

@WebSocket
class SocketHandler {

    var sessions: Queue<Session> = ConcurrentLinkedQueue<Session>()

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        sessions.add(session)
        println("DEEK")
    }
}
