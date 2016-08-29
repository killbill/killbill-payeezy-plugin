/*! SET storage_engine=INNODB */;

drop table if exists payeezy_responses;
create table payeezy_responses (
  record_id int(11) unsigned not null auto_increment
, kb_account_id char(36) not null
, kb_payment_id char(36) not null
, kb_payment_transaction_id char(36) not null
, kb_transaction_type varchar(32) not null
, amount numeric(15,9)
, currency char(3)
, avs varchar(5)
, cvv2 varchar(5)
, method varchar(64)
, card_type varchar(64)
, cardholder_name varchar(64)
, card_number varchar(4)
, exp_date varchar(4)
, token_type varchar(64)
, token_data varchar(255)
, transaction_status varchar(64)
, validation_status varchar(64)
, transaction_type varchar(64)
, transaction_id varchar(64)
, transaction_tag varchar(64)
, merchant_ref varchar(64)
, bank_resp_code varchar(64)
, bank_message varchar(255)
, gateway_resp_code varchar(64)
, gateway_message varchar(255)
, correlation_id varchar(64)
, additional_data longtext default null
, created_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index payeezy_responses_kb_payment_id on payeezy_responses(kb_payment_id);
create index payeezy_responses_kb_payment_transaction_id on payeezy_responses(kb_payment_transaction_id);

drop table if exists payeezy_payment_methods;
create table payeezy_payment_methods (
  record_id int(11) unsigned not null auto_increment
, kb_account_id char(36) not null
, kb_payment_method_id char(36) not null
, token varchar(255) default null
, cc_first_name varchar(255) default null
, cc_last_name varchar(255) default null
, cc_type varchar(255) default null
, cc_exp_month varchar(255) default null
, cc_exp_year varchar(255) default null
, cc_number varchar(255) default null
, cc_last_4 varchar(255) default null
, cc_start_month varchar(255) default null
, cc_start_year varchar(255) default null
, cc_issue_number varchar(255) default null
, cc_verification_value varchar(255) default null
, cc_track_data varchar(255) default null
, dd_holder_name varchar(255) default null
, dd_iban varchar(255) default null
, dd_bic varchar(255) default null
, dd_mandate varchar(255) default null
, address1 varchar(255) default null
, address2 varchar(255) default null
, city varchar(255) default null
, state varchar(255) default null
, zip varchar(255) default null
, country varchar(255) default null
, is_default boolean not null default false
, is_deleted boolean not null default false
, additional_data longtext default null
, created_date datetime not null
, updated_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create unique index payeezy_payment_methods_kb_payment_id on payeezy_payment_methods(kb_payment_method_id);
