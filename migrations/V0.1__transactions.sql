CREATE OR REPLACE FUNCTION valid_transaction_direction(d INTEGER) RETURNS BOOLEAN AS $$
  SELECT d IN (
    0, -- In
    1 -- Out
  );
$$ LANGUAGE SQL IMMUTABLE;

CREATE TABLE transactions (
  id               UUID        PRIMARY KEY,
  owner            TEXT        NOT NULL,
  direction        SMALLINT    NOT NULL CHECK(valid_transaction_direction(direction)),
  amount           BIGINT      NOT NULL,
  currency         VARCHAR(3)  NOT NULL,
  booked_at        TIMESTAMP   NOT NULL,
  title            TEXT
);
