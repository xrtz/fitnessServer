package com.example.myfitness.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

@Configuration
class DatabaseConfig {

    @Bean
    fun jpaVendorAdapter(): HibernateJpaVendorAdapter {
        val adapter = HibernateJpaVendorAdapter()
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect")
        adapter.setDatabase(org.springframework.orm.jpa.vendor.Database.POSTGRESQL)
        adapter.setGenerateDdl(false)
        adapter.setShowSql(false)
        return adapter
    }
}