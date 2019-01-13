package org.team5499.dashboard

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.lang.Thread
import java.lang.Runnable

import org.json.JSONObject

/**
 * Handles websocket events
 */
@WebSocket
class SocketHandler {

    companion object {
        public const val BROADCAST_FREQUENCY: Double = 40.0 // Hertz
        private var sessions: Queue<Session> = ConcurrentLinkedQueue<Session>()
        private var lastVariables: JSONObject = JSONObject()
        private val broadcastThread: Thread = Thread(Broadcaster())
        public fun broadcastJSON(json: JSONObject, prefix: String) {
            for (s in sessions) {
                sendJSON(s, json, "updates")
            }
        }
        public fun sendJSON(session: Session, json: JSONObject, prefix: String) {
            session.getRemote().sendString(prefix + ":" + json.toString())
        }
        public fun sendVariables(session: Session) {
            sendJSON(session, Dashboard.variables, "variables")
        }
        public fun broadcastUpdates() {
            if (Dashboard.variableUpdates.keySet().size > 0) {
                broadcastJSON(Dashboard.variableUpdates, "updates")
                Dashboard.mergeVariableUpdates()
            }
        }
        public fun startBroadcastThread() {
            broadcastThread.start()
        }
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        sessions.add(session)
        sendVariables(session)
    }

    @OnWebSocketClose
    fun onClose(session: Session, statusCode: Int, reason: String) {
        sessions.remove(session)
    }

    @OnWebSocketMessage
    fun onMessage(session: Session, message: String) {
        val updates = JSONObject(message)
        Dashboard.mergeVariableUpdates(updates)
    }

    @Suppress("EmptyDefaultConstructor")
    class Broadcaster() : Runnable {
        override fun run() {
            while (true) {
                SocketHandler.broadcastUpdates()
                @Suppress("MagicNumber")
                Thread.sleep((1000.0 / SocketHandler.BROADCAST_FREQUENCY).toLong())
            }
        }
    }
}
