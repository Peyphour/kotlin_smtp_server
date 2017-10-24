package fr.bnancy.mail.smtp_server.listeners

import fr.bnancy.mail.smtp_server.data.Session

interface SessionListener {
    fun sessionOpened(session: Session)
    fun sessionClosed(session: Session)
    fun deliverMail(session: Session)
    fun acceptRecipient(recipientAddress: String): Boolean
    fun acceptSender(address: String): Boolean
    fun isValidUser(session: Session, password: String): Boolean
}
