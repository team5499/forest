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
        public fun broadcastJSON(json: JSONObject) {
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
            lateinit var updates: JSONObject
            Dashboard.variableLock.lock()
            try {
                updates = Dashboard.variableUpdates
            } finally {
                Dashboard.variableLock.unlock()
            }
            if (updates.keySet().size > 0) {
                broadcastJSON(updates)
                Dashboard.mergeVariableUpdates()
            }
        }
        public fun startBroadcastThread() {
            broadcastThread.start()
        }

        public fun stopBroadcastThread() {
            broadcastThread.interrupt()
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
    @Suppress("ComplexMethod", "NestedBlockDepth")
    fun onMessage(session: Session, message: String) {
        val updates = JSONObject(message)
        Dashboard.mergeVariableUpdates(updates)

        Dashboard.concurrentCallbackLock.lock()
        try {
            for (k in updates.keySet()) {
                if (Dashboard.concurrentCallbacks.containsKey(k)) {
                    for (c in Dashboard.concurrentCallbacks.get(k)!!) {
                        c()
                    }
                }
            }
        } finally {
            Dashboard.concurrentCallbackLock.unlock()
        }

        Dashboard.inlineLock.lock()
        try {
            for (k in updates.keySet()) {
                if (!Dashboard.inlineCallbackUpdates.contains(k)) {
                    Dashboard.inlineCallbackUpdates.add(k)
                }
            }
        } finally {
            Dashboard.inlineLock.unlock()
        }

        broadcastJSONMinusSession(updates, session)
    }

    @OnWebSocketError
    fun onError(t: Throwable) {
    }

    @Suppress("EmptyDefaultConstructor")
    class Broadcaster() : Runnable {
        override fun run() {
            try {
                while (true) {
                    SocketHandler.broadcastUpdates()
                    @Suppress("MagicNumber")
                    Thread.sleep((1000.0 / SocketHandler.BROADCAST_FREQUENCY).toLong())
                }
            } catch (ie: InterruptedException) {
                println("Exiting broadcast thread")
                return
            }
        }
    }
}
