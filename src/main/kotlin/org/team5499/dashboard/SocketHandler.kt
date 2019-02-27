package org.team5499.dashboard

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
import org.eclipse.jetty.websocket.api.WebSocketException
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

        public fun stopBroadcastThread() {
            broadcastThread.stop()
        }

        public fun awaitStop() {
            broadcastThread.join()
        }

        public fun broadcastJSONMinusSession(json: JSONObject, session: Session) {
            for (s in sessions) {
                if (!s.equals(session)) {
                    try {
                        sendJSON(s, json, "updates")
                    } catch (e: WebSocketException) {
                        session.close()
                        continue
                    }
                }
            }
        }
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        sessions.add(session)
        sendVariables(session)
    }

    @OnWebSocketClose
    fun onClose(session: Session?, statusCode: Int, reason: String?) {
        if (session != null) {
            sessions.remove(session)
        }
    }

    @OnWebSocketMessage
    fun onMessage(session: Session, message: String) {
        val updates = JSONObject(message)
        Dashboard.mergeVariableUpdates(updates)
        for (k in updates.keySet()) {
            if (Dashboard.callbacks.contains(k)) {
                for (c in Dashboard.callbacks.get(k)!!) {
                    c(k, updates.get(k))
                }
            }
        }
        broadcastJSONMinusSession(updates, session)
    }

    @OnWebSocketError
    fun onError(t: Throwable) {
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
