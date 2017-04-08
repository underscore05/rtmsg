# --- !Ups
CREATE OR REPLACE FUNCTION NOW_UTC() RETURNS TIMESTAMP AS $$
  SELECT NOW() AT TIME ZONE 'UTC';;
$$ LANGUAGE SQL;

CREATE TABLE subscribers(
	subscriber_id BIGSERIAL NOT NULL PRIMARY KEY,
	msisdn VARCHAR(20) NOT NULL,
	shortcode VARCHAR(20) NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
	CONSTRAINT subscriber_msisdn_shortcode_idx UNIQUE (msisdn, shortcode)
);

CREATE TABLE globelabs_subscribers(
	subscriber_id BIGINT NOT NULL PRIMARY KEY,
	msisdn VARCHAR(20) NOT NULL,
	shortcode VARCHAR(20) NOT NULL,
	access_token VARCHAR(255) NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
	CONSTRAINT globelabs_subscriber_msisdn_shortcode_idx UNIQUE (msisdn, shortcode),
	CONSTRAINT globelabs_subscribers_id_fk FOREIGN KEY (subscriber_id) REFERENCES subscribers(subscriber_id) ON DELETE CASCADE
);

CREATE TABLE accounts(
    account_id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL,
    is_email_verified boolean NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
    CONSTRAINT account_username_idx UNIQUE (username),
    CONSTRAINT account_email_idx UNIQUE (email),
    CONSTRAINT account_id_email_idx UNIQUE (account_id, email)
);

CREATE TABLE platform_settings(
    platform_setting_id VARCHAR(50) NOT NULL PRIMARY KEY,
    content TEXT NOT NULL
);

--
--CREATE TABLE shortcodes(
--    shortcode_id BIGINT NOT NULL,
--    account_id BIGINT NOT NULL,
--    shortcode VARCHAR(20) NOT NULL,
--    CONSTRAINT shortcode_idx UNIQUE (shortcode),
--);

# --- !Downs
DROP TABLE platform_settings;

DROP TABLE accounts;

DROP TABLE globelabs_subscribers;

DROP TABLE subscribers;

DROP FUNCTION NOW_UTC()



