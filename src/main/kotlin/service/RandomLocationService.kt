package com.sjcompany.service

import com.sjcompany.db.DB
import com.sjcompany.dto.LocationDTO
import java.time.ZoneOffset

object RandomLocationService {

    // grids → 랜덤 셀 내부 임의 좌표 1개 뽑기 + locations 저장
    fun pickAndSave(): LocationDTO {
        DB.conn().use { conn ->
            // 1) 랜덤 좌표 뽑기 (PostGIS 필요: ST_Dump, ST_GeneratePoints)
            val selectSql = """
                WITH random_cell AS (
                    SELECT id, adm_nm, cell
                    FROM grids
                    WHERE blacklist = false
                    ORDER BY RANDOM()
                    LIMIT 1
                ),
                random_point AS (
                    SELECT (ST_Dump(ST_GeneratePoints(cell, 1))).geom AS pt, id, adm_nm
                    FROM random_cell
                )
                SELECT
                    ST_Y(pt) AS lat,
                    ST_X(pt) AS lng,
                    adm_nm,
                    id AS grid_id
                FROM random_point;
            """.trimIndent()

            val (lat, lng, address, gridId) = conn.prepareStatement(selectSql).use { ps ->
                ps.executeQuery().use { rs ->
                    if (!rs.next()) error("No random point generated. Check grids data.")
                    val latVal = rs.getDouble("lat")
                    val lngVal = rs.getDouble("lng")
                    val admNm  = rs.getString("adm_nm") // 행정동명 → locations.address에 저장
                    val gridId = rs.getLong("grid_id")
                    Quad(latVal, lngVal, admNm, gridId)
                }
            }

            // 2) locations 저장 (geom 제거된 스키마 기준)
            val insertSql = """
                INSERT INTO locations (lat, lng, address, grid_id)
                VALUES (?, ?, ?, ?)
                RETURNING location_id, created_at;
            """.trimIndent()

            return conn.prepareStatement(insertSql).use { ps ->
                ps.setDouble(1, lat)
                ps.setDouble(2, lng)
                ps.setString(3, address)
                ps.setLong(4, gridId)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) error("Insert failed: no row returned.")
                    val id = rs.getInt("location_id")
                    val createdAt = rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC).toString()
                    LocationDTO(
                        locationId = id,
                        lat = lat,
                        lng = lng,
                        address = address,
                        createdAt = createdAt
                    )
                }
            }
        }
    }

    // 작은 튜플 헬퍼 (data class 대신 간단히)
    private data class Quad(val a: Double, val b: Double, val c: String?, val d: Long)
}