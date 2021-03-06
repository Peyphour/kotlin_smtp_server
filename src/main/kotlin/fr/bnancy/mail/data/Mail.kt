package fr.bnancy.mail.data

import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class Mail(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var sender: String = "",
        var recipients: ArrayList<String> = ArrayList(),
        @Lob
        var headers: String = "",
        @Lob
        var content: String = "",
        var date: Date = Date.from(Instant.now()),
        var secured: Boolean = false,
        var seen: Boolean = false,
        var spam: Boolean = false,
        @Lob
        var spamData: String = ""
)