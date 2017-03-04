
CREATE TABLE "users" (
    
  "id"    CHAR(32)      NOT NULL,
  "name"  VARCHAR(256)  NOT NULL,

  PRIMARY KEY ("id"),
  UNIQUE ("name")
);

CREATE TABLE "settings" (

  "user"  CHAR(32)      NOT NULL,
  "key"   VARCHAR(128)  NOT NULL,
  "value" VARCHAR(256)  NOT NULL,

  PRIMARY KEY ("user", "key")
);
