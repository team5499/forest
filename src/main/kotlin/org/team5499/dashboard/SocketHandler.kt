package org.team5499.dashboard

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

@WebSocket
class SocketHandler {

    companion object {
        var sessions: Queue<Session> = ConcurrentLinkedQueue<Session>()
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        sessions.add(session)
        println("DEEK")
    }
}
