package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "import_history")
data class ImportOperation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = 0,

    @Column(nullable = false)
    val creationDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ImportStatus,

    @Column(nullable = false)
    val objectsAdded: Int = 0,

    @Lob
    @Column(columnDefinition = "TEXT")
    val errorMessage: String? = null
)