package cruid

import java.sql.DriverManager
import java.sql.SQLException

object CreateCertificateTable {
    private const val SQL_CREATE = ("CREATE TABLE certificate"
            + "("
            + " cert_id int,"
            + " keyId text,"
            + " certificate_validity_notBefore date,"
            + " certificate_validity_notAfter date,"
            + " expiration_date_of_the_cryptographic_key_notBefore date,"
            + " expiration_date_of_the_cryptographic_key_notAfter date,"
            + " certificate_status text,"
            + " organization_O text,"
            + " name_CN text,"
            + " subdivision_OU text,"
            + " OID text,"
            + " certificate_DN text,"
            + " certificate_CN text,"
            + " original_certificate text,"
            + " decode_certificate text,"
            + " PRIMARY KEY (cert_id)"
            + ")")

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            DriverManager.getConnection(
                "jdbc:postgresql://127.0.0.1:5432/test", "postgres", "admin"
            ).use { conn ->
                conn.createStatement().use { statement ->
                    // if DDL failed, it will raise an SQLException
                    statement.execute(SQL_CREATE)
                }
            }
        } catch (e: SQLException) {
            System.err.format("SQL State: %s\n%s", e.sqlState, e.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

//object CreateCertificateTable {
//    private const val SQL_CREATE = ("CREATE TABLE certificate"
//            + "("
//            + " cert_id uuid,"
//            + " version int,"
//            + " serialNumber numeric,"
//            + " issuerDN text,"
//            + " issuerX500Principal text,"
//            + " subjectDN text,"
//            + " subjectX500Principal text,"
//            + " notBefore text,"
//            + " notAfter text,"
//            + " tbsCertificate text,"
//            + " signature text,"
//            + " sigAlgName text,"
//            + " sigAlgOID text,"
//            + " sigAlgParams text,"
//            + " issuerUniqueID text,"
//            + " subjectUniqueID text,"
//            + " keyUsage text,"
//            + " extendedKeyUsage text,"
//            + " basicConstraints text,"
//            + " subjectAlternativeNames text,"
//            + " issuerAlternativeNames text,"
//            + " original_certificate text,"
//            + " decode_certificate text,"
//            + " PRIMARY KEY (cert_id)"
//            + ")")
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//        try {
//            DriverManager.getConnection(
//                "jdbc:postgresql://127.0.0.1:5432/test", "postgres", "admin"
//            ).use { conn ->
//                conn.createStatement().use { statement ->
//                    // if DDL failed, it will raise an SQLException
//                    statement.execute(SQL_CREATE)
//                }
//            }
//        } catch (e: SQLException) {
//            System.err.format("SQL State: %s\n%s", e.sqlState, e.message)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
