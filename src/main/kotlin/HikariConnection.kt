import java.sql.Connection
import javax.sql.DataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*


class HikariConnection {
    private var datasource: DataSource? = null

    private fun getDataSource(): Connection? {
        if (datasource == null) {
            val config = HikariConfig()
            config.jdbcUrl = "jdbc:postgresql://localhost/test"
            config.username = "postgres"
            config.password = "admin"
            config.maximumPoolSize = 10
            config.isAutoCommit = false
            config.addDataSourceProperty("cachePrepStmts", "true")
            config.addDataSourceProperty("prepStmtCacheSize", "250")
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            datasource = HikariDataSource(config)
        }
        return datasource!!.connection
    }

    fun startHandler (): Boolean {
        var retryCount = 5
        var transactionCompleted = false
        do {
            try {
                getDataSource()!!.use { conn ->
                    conn.autoCommit = false;
                    conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE
                    ).use { preparedStatement ->
                        val resultSet =
                            preparedStatement.executeQuery("select * from request where processed is null limit $batchSize for no key update skip locked")
                        while (resultSet.next()) {
                            val message_uuid = UUID.fromString(resultSet.getString("message_uuid"))
                            val payload = resultSet.getString("payload")
                            val created = resultSet.getTimestamp("created")
                            val message_type = resultSet.getInt("message_type")
                            val obj = Certificate()
                            // Timestamp -> LocalDateTime
//                            obj.created = created.toLocalDateTime()
//                            obj.message_type = message_type
                            resultSet.updateTimestamp("processed", Timestamp(Date().time))
                            resultSet.updateRow();
                            result.add(obj)
                            conn.commit();
                        }
                        resultSet?.close()
                        transactionCompleted = true
                    }
                    if (conn != null) {
                        try {
                            //
                            // If we got here, and conn is not null, the
                            // transaction should be rolled back, as not
                            // all work has been done
                            conn.use { conn ->
                                conn.rollback();
                            }
                        } catch (sqlEx: SQLException) {
                            //
                            // If we got an exception here, something
                            // pretty serious is going on, so we better
                            // pass it up the stack, rather than just
                            // logging it. . .
                            throw sqlEx;
                        }
                    }

                }
            } catch (sqlEx: SQLException) {
                // The two SQL states that are 'retry-able'
                // for a communications error.
                // Only retry if the error was due to a stale connection,
                // communications problem
                val sqlState = sqlEx.sqlState
                if ("Substitute with Your DB documented sqlstate number for stale connection" == sqlState) {
                    retryCount--;
                } else {
                    retryCount = 0;
                }
            }
        } while (!transactionCompleted && (retryCount > 0));
        return true
    }
}