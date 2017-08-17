package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.config.SmtpServerConfig
import fr.bnancy.mail.smtp_server.commands.AbstractCommand
import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import javax.annotation.PostConstruct

@Component
class Server {

    @Autowired
    lateinit var configSmtp: SmtpServerConfig

    @Autowired
    lateinit var listener: SessionListener

    lateinit var socketServer: ServerSocket
    var running: Boolean = false
    val clients: ArrayList<ClientRunnable> = ArrayList()

    val commands: MutableMap<String, AbstractCommand> = HashMap()

    @PostConstruct
    fun init() {
        val reflections: Reflections = Reflections("fr.bnancy.mail.smtp_server.commands")
        for (classz in reflections.getTypesAnnotatedWith(Command::class.java)) {
            commands.put(classz.getAnnotation(Command::class.java).command, classz.newInstance() as AbstractCommand)
        }
    }

    fun start() {
        this.running = true
        this.socketServer = ServerSocket(this.configSmtp.port)
        Thread({
            while(running) {
                val client: Socket = this.socketServer.accept()
                clients.add(ClientRunnable(client, listener, configSmtp.sessionTimeout, commands))
                Thread(clients[clients.size - 1]).start()
            }
            println("server closed")
        }).start()

        println("Starting SMTP server on port ${configSmtp.port}")
    }

    fun stop() {
        running = false
        clients.forEach { it.stop() }
        try {
            this.socketServer.close()
        } catch (e: SocketException) {
            // ignore
        }
    }

    fun isRunning(): Boolean {
        return running
    }
}

