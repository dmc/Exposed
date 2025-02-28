package org.jetbrains.exposed.spring.autoconfigure

import org.jetbrains.exposed.spring.DatabaseInitializer
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.DatabaseConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
@EnableTransactionManagement
open class ExposedAutoConfiguration(private val applicationContext: ApplicationContext) {

    @Value("\${spring.exposed.excluded-packages:}#{T(java.util.Collections).emptyList()}")
    private lateinit var excludedPackages: List<String>

    @Value("\${spring.exposed.show-sql:false}")
    private var showSql: Boolean = false

    @Bean
    open fun springTransactionManager(datasource: DataSource, databaseConfig: DatabaseConfig): SpringTransactionManager {
        return SpringTransactionManager(datasource, databaseConfig, showSql)
    }

    /**
     * Database config with default values
     */
    @Bean
    @ConditionalOnMissingBean(DatabaseConfig::class)
    open fun databaseConfig(): DatabaseConfig {
        return DatabaseConfig {}
    }

    @Bean
    @ConditionalOnProperty("spring.exposed.generate-ddl", havingValue = "true", matchIfMissing = false)
    open fun databaseInitializer() = DatabaseInitializer(applicationContext, excludedPackages)
}
