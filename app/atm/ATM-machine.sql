DROP DATABASE IF EXISTS atm_machine;
CREATE DATABASE atm_machine;
GRANT ALL PRIVILEGES ON DATABASE atm_machine TO scala;
\c atm_machine;

CREATE TABLE account(
  ACCOUNT_ID SERIAL NOT NULL,
  NAME TEXT NOT NULL,
  ACCOUNT_NUMBER VARCHAR(18) UNIQUE NOT NULL,
  PIN INT NOT NULL,
  AGE SMALLINT NOT NULL,
  PRIMARY KEY (ACCOUNT_ID)
);

CREATE TABLE balance(
  DEPOSIT_ID SERIAL NOT NULL,
  BALANCE BIGINT DEFAULT 0 NOT NULL,
  ACCOUNT_NUMBER VARCHAR(18) NOT NULL,
  PRIMARY KEY (DEPOSIT_ID),
  CONSTRAINT FK_ACCOUNT_NUMBER
  FOREIGN KEY (ACCOUNT_NUMBER)
  REFERENCES account(ACCOUNT_NUMBER)
);



/*

Table "public.account"
Column     |         Type          | Collation | Nullable |                   Default
----------------+-----------------------+-----------+----------+---------------------------------------------
account_id     | integer               |           | not null | nextval('account_account_id_seq'::regclass)
name           | text                  |           | not null |
account_number | character varying(18) |           | not null |
pin            | integer               |           | not null |
age            | smallint              |           | not null |
Indexes:
"account_pkey" PRIMARY KEY, btree (account_id)
"account_account_number_key" UNIQUE CONSTRAINT, btree (account_number)
Referenced by:
TABLE "balance" CONSTRAINT "fk_account_number" FOREIGN KEY (account_number) REFERENCES account(account_number)


------------------------------------------------------------------------------------------------------------------------------

Table "public.balance"
Column     |         Type          | Collation | Nullable |                   Default
----------------+-----------------------+-----------+----------+---------------------------------------------
deposit_id     | integer               |           | not null | nextval('balance_deposit_id_seq'::regclass)
balance        | bigint                |           | not null | 0
account_number | character varying(18) |           | not null |
Indexes:
"balance_pkey" PRIMARY KEY, btree (deposit_id)
Foreign-key constraints:
"fk_account_number" FOREIGN KEY (account_number) REFERENCES account(account_number)

*/
