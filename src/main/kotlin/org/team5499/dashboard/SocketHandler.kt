package org.team5499.dashboard

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Handles websocket events
 */
@WebSocket
class SocketHandler {

    companion object {
        private var sessions: Queue<Session> = ConcurrentLinkedQueue<Session>()
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        sessions.add(session)
        println("DEEK")
    }
}
