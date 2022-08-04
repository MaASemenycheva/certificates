import DbConnection.closeEntityManagerFactory
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.security.Principal
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.security.auth.x500.X500Principal


private class RegistrationService : RegistrationServiceGrpcKt.RegistrationServiceCoroutineImplBase() {

    private val server: Server? = null
    private val logger = LoggerFactory.getLogger(CertificateServer::class.java)

    @Throws(Exception::class)
    private fun convertStringToX509Cert(certificate: String): X509Certificate? {
        val targetStream: InputStream = ByteArrayInputStream(certificate.toByteArray())
        return CertificateFactory
            .getInstance("X509")
            .generateCertificate(targetStream) as X509Certificate
    }

    fun getField(field: String, subField: String): String {
        val indexOfFieldStart: Int = field.indexOf(subField)
        val fieldLength: Int = field.length
        val substring = field.subSequence(indexOfFieldStart, fieldLength)
        val indexOfOrganizationEnd: Int = substring.indexOf(",")
        return substring.subSequence(subField.length, indexOfOrganizationEnd) as String
    }

    fun getExpirationDateOfTheCryptographicKey (field: String): Pair<ZonedDateTime, ZonedDateTime> {
//            Pair<String, String> {
        val subField  = "PrivateKeyUsage: ["
        val substring = field.subSequence(field.indexOf(subField), field.length)
        val period = substring.subSequence(subField.length, substring.indexOf("]")) as String
//        logger.info("period = "+ period)
        val notBefore = period.subSequence(period.indexOf("From")+6, period.indexOf(",")).toString()
//        logger.info("notBefore " + timeParse(notBefore))
        val notAfter = period.subSequence(period.indexOf("To")+4, period.length).toString()
//        logger.info("notAfter" + timeParse(notAfter))

        return Pair(timeParse(notBefore), timeParse(notAfter))
    }

    fun timeParse(input: String): ZonedDateTime {
        val f: DateTimeFormatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z uuuu")
            .withLocale(Locale.ROOT)
        return ZonedDateTime.parse(input, f)
    }

    @Throws(IOException::class)
    override suspend fun register(request: Register.Cert): Register.RegistrationResult {
        val cert = convertStringToX509Cert(request.certificate)
        logger.info("Идентификатор ключа  (keyId) ")
        logger.info("Срок действия сертификата (notBefore) = " + timeParse(cert!!.notBefore.toString()))
        logger.info("Срок действия сертификата (notAfter) = " + timeParse(cert!!.notAfter.toString()))
        logger.info("Срок действия криптографиеского ключа (notBefore) = "+getExpirationDateOfTheCryptographicKey(cert!!.toString()).first)
        logger.info("Срок действия криптографиеского ключа (notAfter) = "+getExpirationDateOfTheCryptographicKey(cert!!.toString()).second)
        logger.info("Статус сертификата = ")
        logger.info("Организация (O) - Issuer = " + getField(cert!!.issuerDN.toString(), "O="))
        logger.info("Имя (CN) - Issuer = " + getField(cert!!.issuerDN.toString(), "CN="))
        logger.info("Организация (O) - Subject = " + getField(cert!!.subjectDN.toString(), "O="))
        logger.info("Имя (CN) - Subject = " + getField(cert!!.subjectDN.toString(), "CN="))
        logger.info("Подразделение (OU) = "+ getField(cert!!.subjectDN.toString(), "OU="))
        logger.info("OID = "+ cert!!.sigAlgOID)
        logger.info("CN/DN сертификата (Issuer)= " + cert.issuerDN)
        logger.info("CN/DN сертификата (Subject)= " + cert.subjectDN)



//        try {
//            DriverManager.getConnection(
//                "jdbc:postgresql://127.0.0.1:5432/test", "postgres", "admin"
//            ).use { conn ->
//                conn.createStatement().use { statement ->
//                    val row =
//                        statement.executeUpdate(
//                            CertificateServer.generateInsert(
//                                UUID.fromString(UUID.randomUUID().toString()),
//                                cert!!.version,
//                                cert!!.serialNumber,
//                                cert!!.issuerDN,
//                                cert!!.issuerX500Principal,
//                                cert!!.subjectDN,
//                                cert!!.subjectX500Principal,
//                                cert!!.notBefore,
//                                cert!!.notAfter,
//                                cert!!.tbsCertificate,
//                                cert!!.signature,
//                                cert!!.sigAlgName,
//                                cert!!.sigAlgOID,
//                                cert!!.sigAlgParams,
//                                cert!!.issuerUniqueID,
//                                cert!!.subjectUniqueID,
//                                cert!!.keyUsage,
//                                cert!!.extendedKeyUsage,
//                                cert!!.basicConstraints,
//                                cert!!.subjectAlternativeNames,
//                                cert!!.issuerAlternativeNames,
//                                request.certificate,
//                                cert
//                            )
//                        )
//                }
//            }
//        } catch (e: SQLException) {
//            System.err.format("SQL State: %s\n%s", e.sqlState, e.message)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return registrationResult { succeeded=true }
    }
}


class CertificateServer(serverBuilder: ServerBuilder<*>, private val port: Int) {
    private val logger = LoggerFactory.getLogger(CertificateServer::class.java)
    private val server: Server?

    constructor(port: Int) : this(ServerBuilder.forPort(port), port) {}

    init {
        server = serverBuilder.addService(RegistrationService()).build()
    }

    /**
     * Start serving requests.
     *
     * @throws java.io.IOException
     */
    @Throws(IOException::class)
    fun start() {
        server?.start()
        logger.info("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down")
                this@CertificateServer.stop()
                System.err.println("*** server shut down")
            }
        })
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    fun stop() {
        if (server != null) {
            System.err.println("*** close Database Connections")
            closeEntityManagerFactory()
            server.shutdown()
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    @Throws(InterruptedException::class)
    private fun blockUntilShutdown() {
        if (server != null) {
            server.awaitTermination()
        }
    }

    companion object {

        fun generateInsert(cert_id: UUID?,
                           version: Int?,
                           serialNumber: BigInteger?,
                           issuerDN: Principal?,
                           issuerX500Principal: X500Principal?,
                           subjectDN: Principal?,
                           subjectX500Principal: X500Principal?,
                           notBefore: Date?,
                           notAfter: Date?,
                           tbsCertificate: ByteArray?,
                           signature: ByteArray?,
                           sigAlgName: String?,
                           sigAlgOID: String?,
                           sigAlgParams: ByteArray?,
                           issuerUniqueID: BooleanArray?,
                           subjectUniqueID: BooleanArray?,
                           keyUsage: BooleanArray?,
                           extendedKeyUsage: MutableList<String>?,
                           basicConstraints: Int?,
                           subjectAlternativeNames: MutableCollection<MutableList<*>>?,
                           issuerAlternativeNames: MutableCollection<MutableList<*>>?,
                           original_certificate: String?,
                           decode_certificate: X509Certificate?): String {

            return "INSERT INTO certificate (" +
                    "cert_id, " +
                    "version, " +
                    "serialNumber, " +
                    "issuerDN, " +
                    "issuerX500Principal, " +
                    "subjectDN, " +
                    "subjectX500Principal, " +
                    "notBefore, " +
                    "notAfter, " +
                    "tbsCertificate, " +
                    "signature, " +
                    "sigAlgName, " +
                    "sigAlgOID, " +
                    "sigAlgParams, " +
                    "issuerUniqueID, " +
                    "subjectUniqueID, " +
                    "keyUsage, " +
                    "extendedKeyUsage, " +
                    "basicConstraints, " +
                    "subjectAlternativeNames, " +
                    "issuerAlternativeNames, " +
                    "original_certificate, " +
                    "decode_certificate" +
                    ") " +
                    "VALUES ('" + cert_id + "','"+
                    version + "','"+
                    serialNumber + "','"+
                    issuerDN + "','"+
                    issuerX500Principal + "','"+
                    subjectDN + "','"+
                    subjectX500Principal + "','"+
                    notBefore + "','"+
                    notAfter + "','"+
                    tbsCertificate + "','"+
                    signature + "','"+
                    sigAlgName + "','"+
                    sigAlgOID + "','"+
                    sigAlgParams + "','"+
                    issuerUniqueID + "','"+
                    subjectUniqueID + "','"+
                    keyUsage + "','"+
                    extendedKeyUsage + "','"+
                    basicConstraints + "','"+
                    subjectAlternativeNames + "','"+
                    issuerAlternativeNames + "','"+
                    original_certificate + "','"+
                    decode_certificate + "')"
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val server = CertificateServer(8080)
            server.start()
            server.blockUntilShutdown()
        }
    }
}