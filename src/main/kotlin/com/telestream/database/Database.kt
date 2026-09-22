package com.telestream.database

import java.io.File
import java.sql.DriverManager

data class Bookmark(
    val id: Int,
    val userId: Long,
    val provider: String,
    val mediaUrl: String,
    val title: String,
    val posterUrl: String
)

object Database {
    private val dbDir = File("data").apply { mkdirs() }
    private val dbFile = File(dbDir, "telestream.db")
    private val url = "jdbc:sqlite:${dbFile.absolutePath}"

    init {
        DriverManager.getConnection(url).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY,
                        language TEXT DEFAULT 'en',
                        joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )
                stmt.execute(
                    """
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER,
                        provider TEXT,
                        media_url TEXT,
                        title TEXT,
                        poster_url TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(user_id, media_url)
                    );
                    """.trimIndent()
                )
            }
        }
    }

    fun getUserLanguage(userId: Long): String {
        DriverManager.getConnection(url).use { conn ->
            val sql = "SELECT language FROM users WHERE user_id = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return rs.getString("language") ?: "en"
                }
            }
        }
        return "en"
    }

    fun setUserLanguage(userId: Long, language: String) {
        DriverManager.getConnection(url).use { conn ->
            val sql = """
                INSERT INTO users (user_id, language) VALUES (?, ?)
                ON CONFLICT(user_id) DO UPDATE SET language = excluded.language
            """.trimIndent()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                stmt.setString(2, language)
                stmt.executeUpdate()
            }
        }
    }

    fun addBookmark(userId: Long, provider: String, mediaUrl: String, title: String, posterUrl: String) {
        DriverManager.getConnection(url).use { conn ->
            val sql = """
                INSERT OR IGNORE INTO bookmarks (user_id, provider, media_url, title, poster_url)
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                stmt.setString(2, provider)
                stmt.setString(3, mediaUrl)
                stmt.setString(4, title)
                stmt.setString(5, posterUrl)
                stmt.executeUpdate()
            }
        }
    }

    fun removeBookmark(userId: Long, mediaUrl: String) {
        DriverManager.getConnection(url).use { conn ->
            val sql = "DELETE FROM bookmarks WHERE user_id = ? AND media_url = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                stmt.setString(2, mediaUrl)
                stmt.executeUpdate()
            }
        }
    }

    fun isBookmarked(userId: Long, mediaUrl: String): Boolean {
        DriverManager.getConnection(url).use { conn ->
            val sql = "SELECT 1 FROM bookmarks WHERE user_id = ? AND media_url = ?"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                stmt.setString(2, mediaUrl)
                val rs = stmt.executeQuery()
                return rs.next()
            }
        }
    }

    fun getBookmarks(userId: Long): List<Bookmark> {
        val list = mutableListOf<Bookmark>()
        DriverManager.getConnection(url).use { conn ->
            val sql = "SELECT * FROM bookmarks WHERE user_id = ? ORDER BY created_at DESC LIMIT 30"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, userId)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    list.add(
                        Bookmark(
                            id = rs.getInt("id"),
                            userId = rs.getLong("user_id"),
                            provider = rs.getString("provider"),
                            mediaUrl = rs.getString("media_url"),
                            title = rs.getString("title"),
                            posterUrl = rs.getString("poster_url") ?: ""
                        )
                    )
                }
            }
        }
        return list
    }

    fun getTotalUsers(): Int {
        DriverManager.getConnection(url).use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT COUNT(*) FROM users")
                if (rs.next()) return rs.getInt(1)
            }
        }
        return 0
    }

    fun getTotalBookmarks(): Int {
        DriverManager.getConnection(url).use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT COUNT(*) FROM bookmarks")
                if (rs.next()) return rs.getInt(1)
            }
        }
        return 0
    }
}
