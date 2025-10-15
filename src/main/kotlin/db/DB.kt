package com.sjcompany.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import java.sql.Connection

object DB {
    private lateinit var ds: HikariDataSource

    fun init() {
        val env = dotenv()

        val cfg = HikariConfig().apply {
            jdbcUrl = env["DB_URL"]
            username = env["DB_USER"]
            password = env["DB_PASSWORD"]

            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = true
        }
        ds = HikariDataSource(cfg)
    }

    fun conn(): Connection = ds.connection
}