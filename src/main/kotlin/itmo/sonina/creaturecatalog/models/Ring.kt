package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*

@Entity
@Table(name = "rings")
data class Ring(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    override val id: Int = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var power: Int,

    var weight: Long? = null,

    @OneToOne(mappedBy = "ring", cascade = [CascadeType.ALL])
    var bookCreature: BookCreature? = null
) : EntityWithId {
    override fun equals(other: Any?) =
        this === other || (other is Ring && id == other.id)

    override fun hashCode() = id.hashCode()
}
