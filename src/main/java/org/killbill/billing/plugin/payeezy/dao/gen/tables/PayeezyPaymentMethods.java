/**
 * This class is generated by jOOQ
 */
package org.killbill.billing.plugin.payeezy.dao.gen.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.0"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PayeezyPaymentMethods extends org.jooq.impl.TableImpl<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> {

	private static final long serialVersionUID = -948921900;

	/**
	 * The reference instance of <code>killbill.payeezy_payment_methods</code>
	 */
	public static final org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods PAYEEZY_PAYMENT_METHODS = new org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> getRecordType() {
		return org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord.class;
	}

	/**
	 * The column <code>killbill.payeezy_payment_methods.record_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, org.jooq.types.UInteger> RECORD_ID = createField("record_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.kb_account_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> KB_ACCOUNT_ID = createField("kb_account_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.kb_payment_method_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> KB_PAYMENT_METHOD_ID = createField("kb_payment_method_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.token</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> TOKEN = createField("token", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_first_name</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_FIRST_NAME = createField("cc_first_name", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_last_name</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_LAST_NAME = createField("cc_last_name", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_type</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_TYPE = createField("cc_type", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_exp_month</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_EXP_MONTH = createField("cc_exp_month", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_exp_year</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_EXP_YEAR = createField("cc_exp_year", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_number</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_NUMBER = createField("cc_number", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_last_4</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_LAST_4 = createField("cc_last_4", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_start_month</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_START_MONTH = createField("cc_start_month", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_start_year</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_START_YEAR = createField("cc_start_year", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_issue_number</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_ISSUE_NUMBER = createField("cc_issue_number", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_verification_value</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_VERIFICATION_VALUE = createField("cc_verification_value", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.cc_track_data</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CC_TRACK_DATA = createField("cc_track_data", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.address1</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> ADDRESS1 = createField("address1", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.address2</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> ADDRESS2 = createField("address2", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.city</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> CITY = createField("city", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.state</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> STATE = createField("state", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.zip</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> ZIP = createField("zip", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.country</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> COUNTRY = createField("country", org.jooq.impl.SQLDataType.VARCHAR.length(255), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.is_default</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.Byte> IS_DEFAULT = createField("is_default", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.is_deleted</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.Byte> IS_DELETED = createField("is_deleted", org.jooq.impl.SQLDataType.TINYINT.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.additional_data</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> ADDITIONAL_DATA = createField("additional_data", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.created_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.sql.Timestamp> CREATED_DATE = createField("created_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.updated_date</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.sql.Timestamp> UPDATED_DATE = createField("updated_date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * The column <code>killbill.payeezy_payment_methods.kb_tenant_id</code>.
	 */
	public final org.jooq.TableField<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, java.lang.String> KB_TENANT_ID = createField("kb_tenant_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * Create a <code>killbill.payeezy_payment_methods</code> table reference
	 */
	public PayeezyPaymentMethods() {
		this("payeezy_payment_methods", null);
	}

	/**
	 * Create an aliased <code>killbill.payeezy_payment_methods</code> table reference
	 */
	public PayeezyPaymentMethods(java.lang.String alias) {
		this(alias, org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods.PAYEEZY_PAYMENT_METHODS);
	}

	private PayeezyPaymentMethods(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> aliased) {
		this(alias, aliased, null);
	}

	private PayeezyPaymentMethods(java.lang.String alias, org.jooq.Table<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.killbill.billing.plugin.payeezy.dao.gen.Killbill.KILLBILL, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord, org.jooq.types.UInteger> getIdentity() {
		return org.killbill.billing.plugin.payeezy.dao.gen.Keys.IDENTITY_PAYEEZY_PAYMENT_METHODS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> getPrimaryKey() {
		return org.killbill.billing.plugin.payeezy.dao.gen.Keys.KEY_PAYEEZY_PAYMENT_METHODS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord>>asList(org.killbill.billing.plugin.payeezy.dao.gen.Keys.KEY_PAYEEZY_PAYMENT_METHODS_PRIMARY, org.killbill.billing.plugin.payeezy.dao.gen.Keys.KEY_PAYEEZY_PAYMENT_METHODS_PAYEEZY_PAYMENT_METHODS_KB_PAYMENT_ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods as(java.lang.String alias) {
		return new org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods rename(java.lang.String name) {
		return new org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods(name, null);
	}
}
