import java.time.LocalDateTime

data class Certificate (
    var certificate_id: String? = null,
    var cert_begin_date: String? = null,
    var cert_expired_date: LocalDateTime? = null,
    var key_begin_date: LocalDateTime? = null,
    var key_expired_date: LocalDateTime? = null,
    var org_o: String? = null,
    var name_cn: String? = null,
    var depatment_ou: String? = null,
    var oid_value: String? = null,
    var dn_cn_cert: String? = null,
)