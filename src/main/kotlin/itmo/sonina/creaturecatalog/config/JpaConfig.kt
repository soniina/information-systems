package itmo.sonina.creaturecatalog.config

import org.eclipse.persistence.config.PersistenceUnitProperties
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter
import org.springframework.transaction.jta.JtaTransactionManager
import javax.sql.DataSource
import org.eclipse.persistence.logging.SessionLog


@Configuration
class EclipseLinkJpaConfiguration(
    dataSource: DataSource,
    jpaProperties: JpaProperties,
    jtaTransactionManager: ObjectProvider<JtaTransactionManager>
) : JpaBaseConfiguration(dataSource, jpaProperties, jtaTransactionManager) {

    override fun createJpaVendorAdapter(): AbstractJpaVendorAdapter {
        return EclipseLinkJpaVendorAdapter()
    }

    override fun getVendorProperties(dataSource: DataSource): MutableMap<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[PersistenceUnitProperties.WEAVING] = "static"
        props[PersistenceUnitProperties.LOGGING_LEVEL] = SessionLog.FINE_LABEL
        props[PersistenceUnitProperties.DDL_GENERATION] = PersistenceUnitProperties.NONE
        return props
    }
}
