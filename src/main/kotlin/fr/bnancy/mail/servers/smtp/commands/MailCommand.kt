package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.Command
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@Command("MAIL")
class MailCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {

        if(!session.state.contains(SessionState.HELO))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue HELO/EHLO first.")
        if(session.state.contains(SessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Transaction in progress already.")

        // Clean buffers as specified by RFC
        session.to = ArrayList()
        session.content = ""

        val mailRegex = Regex("<(.*)>")
        val address = mailRegex.find(data)!!.groupValues[1]

        if(!listener.acceptSender(address))
            return SmtpResponseCode.MAILBOX_UNAVAILABLE("$address isn't authorized to send mail here")

        session.from = address

        session.state.add(SessionState.MAIL)

        return SmtpResponseCode.OK("OK")
    }
}