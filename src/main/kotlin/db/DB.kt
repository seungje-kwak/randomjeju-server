package com.sjcompany.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import java.sql.Connection

object DB {
    private lateinit var ds: HikariDataSource

    fun init() {
        val env = try {
            dotenv()   // 로컬 환경에서는 .env 파일 읽기
        } catch (e: Exception) {
            null       // Render 환경에서는 null 반환 (System.getenv 사용)
        }

        // ✅ 2️⃣ Render에서는 System.getenv(), 로컬에서는 .env 값 사용
        val url = env?.get("DB_URL") ?: System.getenv("DB_URL")
        val user = env?.get("DB_USER") ?: System.getenv("DB_USER")
        val dbpassword = env?.get("DB_PASSWORD") ?: System.getenv("DB_PASSWORD")

        // ✅ 3️⃣ HikariConfig 설정
        val cfg = HikariConfig().apply {
            jdbcUrl = url
            username = user
            password = dbpassword

            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            isAutoCommit = true
            addDataSourceProperty("ssl", "true")
        }

        ds = HikariDataSource(cfg)
        println("✅ Database connection pool initialized successfully.")
    }

    fun conn(): Connection = ds.connection
}