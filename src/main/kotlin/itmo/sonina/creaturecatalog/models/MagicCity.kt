package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "magic_cities")
data class MagicCity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    override val id: Int = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var area: Long,

    @Column(nullable = false)
    var population: Int,

    @Column(nullable = false)
    var establishmentDate: LocalDate,

    @Enumerated(EnumType.STRING)
    var governor: BookCreatureType? = null,

    @Column(nullable = false)
    var capital: Boolean,

    @Column(nullable = false)
    var populationDensity: Float,

    @OneToMany(mappedBy = "creatureLocation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var bookCreatures: MutableList<BookCreature> = mutableListOf()
) : EntityWithId {
    override fun equals(other: Any?) =
        this === other || (other is Coordinates && id == other.id)

    override fun hashCode() = id.hashCode()
}
