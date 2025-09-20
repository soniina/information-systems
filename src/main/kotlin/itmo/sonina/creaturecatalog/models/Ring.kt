package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*

@Entity
@Table(name = "rings")
data class Ring (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var power: Int,

    var weight: Long? = null
)
