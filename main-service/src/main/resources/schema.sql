CREATE TABLE IF NOT EXISTS users (
  id    BIGINT  GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name  VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
	id   BIGINT  GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	name VARCHAR NOT NULL,
	CONSTRAINT pk_category PRIMARY KEY (id),
	CONSTRAINT uk_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
	id                 BIGINT    GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	annotation         VARCHAR   NOT NULL,
	description        VARCHAR   NOT NULL,
	created            TIMESTAMP NOT NULL,
	event_date         TIMESTAMP NULL,
	published          TIMESTAMP NULL,
	location_lat       FLOAT     NOT NULL,
	location_lon       FLOAT     NOT NULL,
	paid               BOOLEAN   NULL,
	participant_limit  INT       DEFAULT 0,
	request_moderation BOOLEAN   DEFAULT TRUE,
	title              VARCHAR   NOT NULL,
	category_id        BIGINT    NOT NULL,
	owner_id           BIGINT    NOT NULL,
	state              VARCHAR   NOT NULL,
	CONSTRAINT pk_event PRIMARY KEY (id),
	CONSTRAINT fk_event_on_category FOREIGN KEY (category_id) REFERENCES categories(id),
	CONSTRAINT fk_event_on_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS requests (
	id       BIGINT    GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	user_id  BIGINT    NOT NULL,
	event_id BIGINT    NOT NULL,
	created  TIMESTAMP NOT NULL,
	status   VARCHAR   NOT NULL, /* created, aproved, canceled, rejected*/
	CONSTRAINT pk_request PRIMARY KEY (id),
	CONSTRAINT fk_requst_on_user FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_request_on_event FOREIGN KEY (event_id) REFERENCES events(id),
	CONSTRAINT uk_request UNIQUE (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilations (
	id     BIGINT  GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	title  VARCHAR NOT NULL,
	pinned BOOLEAN,
	CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_events (
	id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	comp_id  BIGINT NOT NULL,
	event_id BIGINT NOT NULL,
	CONSTRAINT pk_comp_event PRIMARY KEY (id),
	CONSTRAINT uk_comp_event UNIQUE (comp_id, event_id),
	CONSTRAINT fk_comp_event_on_comp FOREIGN KEY (comp_id) REFERENCES compilations(id) ON DELETE CASCADE,
	CONSTRAINT fk_comp_event_on_event FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
	id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	user_id   BIGINT NOT NULL,
	friend_id BIGINT NOT NULL,
	CONSTRAINT pk_subscription PRIMARY KEY (id),
	CONSTRAINT fk_subscriptions_on_user FOREIGN KEY (user_id) REFERENCES users(id),
	CONSTRAINT fk_subscriptions_on_friend FOREIGN KEY (friend_id) REFERENCES users(id),
	CONSTRAINT uk_subscription UNIQUE (user_id, friend_id)
);