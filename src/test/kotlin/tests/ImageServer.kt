package tests

import kotlin.text.toByteArray

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ImageServer(imag: BufferedImage) : Runnable {

    private lateinit var img: BufferedImage
    private lateinit var serverSocket: ServerSocket
    private lateinit var socket: Socket
    private val boundary = "stream"
    private lateinit var outputStream: OutputStream
    public var toPush: BufferedImage = imag
    private var closes = 0

    fun startStreamingServer() {
        serverSocket = ServerSocket(5801)
        socket = serverSocket.accept()
        writeHeader(socket.getOutputStream(), boundary)
    }

    fun writeHeader(stream: OutputStream, boundary: String) {
        stream.write(("HTTP/1.0 200 OK\r\n" +
                "Connection: close\r\n" +
                "Max-Age: 0\r\n" +
                "Expires: 0\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                "Pragma: no-cache\r\n" +
                "Content-Type: multipart/x-mixed-replace; " +
                "boundary=" + boundary + "\r\n" +
                "\r\n" +
                "--" + boundary + "\r\n").toByteArray())
    }

    @Suppress("TooGenericExceptionCaught")
    fun pushImage(frame: BufferedImage) {
        try {
            outputStream = socket.getOutputStream()
            img = frame
            var baos = ByteArrayOutputStream()
            ImageIO.write(img, "jpg", baos)
            val imageBytes = baos.toByteArray()
            outputStream.write(("Content-type: image/jpeg\r\n" +
                    "Content-Length: " + imageBytes.size + "\r\n" +
                    "\r\n").toByteArray())
            outputStream.write(imageBytes)
            outputStream.write(("\r\n--" + boundary + "\r\n").toByteArray())
        } catch (ex: Exception) {
            closes += 1
            println("exception $closes")
            socket = serverSocket.accept()
            writeHeader(socket.getOutputStream(), boundary)
        }
    }

    override fun run() {
        try {
            println("go to  http://localhost:8080 with browser")
            startStreamingServer()

            while (true) {
                pushImage(toPush)
            }
        } catch (e: IOException) {
            return
        }
    }

    fun stopStreamingServer() {
        socket.close()
        serverSocket.close()
    }
}
