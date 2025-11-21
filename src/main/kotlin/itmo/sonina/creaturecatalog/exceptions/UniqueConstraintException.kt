package itmo.sonina.creaturecatalog.exceptions

class UniqueConstraintException(
    val field: String,
    override val message: String
) : RuntimeException(message)