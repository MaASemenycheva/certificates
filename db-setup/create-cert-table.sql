
/******************** Add Table: public.certificate_decode ************************/

/* Build Table Structure */
CREATE TABLE public.certificate_decode
(
	keyId bytea DEFAULT  PRIMARY KEY,
	certificate TEXT NOT NULL,
	member_id TEXT,
	transaction_id bytea NOT NULL,
	walletId TEXT,
	senderId TEXT,
	walletKeyId TEXT NOT NULL,
	certBeginDate TIMESTAMP WITHOUT TIME ZONE,
	certExpiredDate TIMESTAMP WITHOUT TIME ZONE,
	keyBeginDate TIMESTAMP WITHOUT TIME ZONE,
	keyExpiredDate TIMESTAMP WITHOUT TIME ZONE,
	org_o TEXT,
	name_cn TEXT,
	depatment_ou TEXT,
	oid TEXT,
	dn_cn_cert TEXT
	PRIMARY KEY (cert_id)
)