package itmo.sonina.creaturecatalog.aspects

import jakarta.persistence.EntityManagerFactory
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor
import org.eclipse.persistence.sessions.server.ServerSession
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Aspect
@Component
class CacheLoggerAspect(private val entityManagerFactory: EntityManagerFactory) {
    private val logger = LoggerFactory.getLogger(CacheLoggerAspect::class.java)

    @Value("\${app.cache.logging-enabled}")
    private var isLoggingEnabled: Boolean = true

    @AfterReturning("execution(* itmo.sonina.creaturecatalog.services.CrudService.get*(..))")
    fun logCacheStatistics(joinPoint: JoinPoint) {
        if (!isLoggingEnabled) return
        try {
            val session = entityManagerFactory.unwrap(ServerSession::class.java)

            val internalAccessor = session.identityMapAccessor as? IdentityMapAccessor ?: return

            val trackedEntities = listOf(
                itmo.sonina.creaturecatalog.models.BookCreature::class.java,
                itmo.sonina.creaturecatalog.models.Coordinates::class.java,
                itmo.sonina.creaturecatalog.models.MagicCity::class.java,
                itmo.sonina.creaturecatalog.models.Ring::class.java
            )

            val logBuilder = StringBuilder(">>> [L2 Cache Stats] Method: ${joinPoint.signature.name}")

            trackedEntities.forEach { entityClass ->
                val identityMap = internalAccessor.getIdentityMap(entityClass)
                val size = identityMap?.size ?: 0
                logBuilder.append(" | ${entityClass.simpleName}: $size")
            }

            logger.info(logBuilder.toString())
        } catch (e: Exception) {
            logger.error("Error logging cache stats: ${e.message}")
        }
    }
}