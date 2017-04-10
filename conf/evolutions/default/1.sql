# --- !Ups
CREATE OR REPLACE FUNCTION NOW_UTC() RETURNS TIMESTAMP AS $$
  SELECT NOW() AT TIME ZONE 'UTC';;
$$ LANGUAGE SQL;

CREATE TABLE accounts(
    account_id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL,
    is_email_verified boolean NOT NULL DEFAULT FALSE,
    is_active boolean NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
    CONSTRAINT account_username_idx UNIQUE (username),
    CONSTRAINT account_email_idx UNIQUE (email)
);

CREATE TABLE shortcodes(
    shortcode_id VARCHAR(20) NOT NULL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    shortcode_type VARCHAR(20) NOT NULL,
    shortcode_app_id VARCHAR(100) NOT NULL,
    shortcode_app_secret VARCHAR(100) NOT NULL,
    is_active boolean NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
    CONSTRAINT shortcode_account_id_fk FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

CREATE TABLE subscribers(
	subscriber_id BIGSERIAL NOT NULL PRIMARY KEY,
	msisdn VARCHAR(20) NOT NULL,
	shortcode_id VARCHAR(20) NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
	CONSTRAINT subscriber_msisdn_shortcode_idx UNIQUE (msisdn, shortcode_id),
	CONSTRAINT subscriber_shortcode_id_fk FOREIGN KEY (shortcode_id) REFERENCES shortcodes(shortcode_id) ON DELETE CASCADE
);

CREATE TABLE globelabs_subscribers(
	subscriber_id BIGINT NOT NULL PRIMARY KEY,
	msisdn VARCHAR(20) NOT NULL,
	shortcode_id VARCHAR(20) NOT NULL,
	access_token VARCHAR(255) NOT NULL,
	created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW_UTC(),
	CONSTRAINT globelabs_subscriber_msisdn_shortcode_idx UNIQUE (msisdn, shortcode_id),
	CONSTRAINT globelabs_subscribers_id_fk FOREIGN KEY (subscriber_id) REFERENCES subscribers(subscriber_id) ON DELETE CASCADE,
	CONSTRAINT globelabs_subscriber_shortcode_id_fk FOREIGN KEY (shortcode_id) REFERENCES shortcodes(shortcode_id) ON DELETE CASCADE
);

CREATE TABLE platform_settings(
    platform_setting_id VARCHAR(50) NOT NULL PRIMARY KEY,
    content TEXT NOT NULL
);

# --- !Downs

DROP TABLE platform_settings;

DROP TABLE globelabs_subscribers;

DROP TABLE subscribers;

DROP TABLE shortcodes;

DROP TABLE accounts;

DROP FUNCTION NOW_UTC()



