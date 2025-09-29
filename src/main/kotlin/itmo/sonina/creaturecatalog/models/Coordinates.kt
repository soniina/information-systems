package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*

@Entity
@Table(name = "coordinates")
data class Coordinates(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    override val id: Int = 0,

    @Column(nullable = false)
    var x: Float,

    @Column(nullable = false)
    var y: Long,

    @OneToOne(mappedBy = "coordinates", orphanRemoval = true)
    var bookCreature: BookCreature? = null
): EntityWithId {
    override fun equals(other: Any?) =
        this === other || (other is Coordinates && id == other.id)

    override fun hashCode() = id.hashCode()
}
