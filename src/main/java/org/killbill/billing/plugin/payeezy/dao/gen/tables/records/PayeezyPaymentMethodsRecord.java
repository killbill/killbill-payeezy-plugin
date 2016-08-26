/**
 * This class is generated by jOOQ
 */
package org.killbill.billing.plugin.payeezy.dao.gen.tables.records;

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
public class PayeezyPaymentMethodsRecord extends org.jooq.impl.UpdatableRecordImpl<org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord> {

	private static final long serialVersionUID = 1139930880;

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.record_id</code>.
	 */
	public void setRecordId(org.jooq.types.UInteger value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.record_id</code>.
	 */
	public org.jooq.types.UInteger getRecordId() {
		return (org.jooq.types.UInteger) getValue(0);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.kb_account_id</code>.
	 */
	public void setKbAccountId(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.kb_account_id</code>.
	 */
	public java.lang.String getKbAccountId() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.kb_payment_method_id</code>.
	 */
	public void setKbPaymentMethodId(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.kb_payment_method_id</code>.
	 */
	public java.lang.String getKbPaymentMethodId() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.token</code>.
	 */
	public void setToken(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.token</code>.
	 */
	public java.lang.String getToken() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_first_name</code>.
	 */
	public void setCcFirstName(java.lang.String value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_first_name</code>.
	 */
	public java.lang.String getCcFirstName() {
		return (java.lang.String) getValue(4);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_last_name</code>.
	 */
	public void setCcLastName(java.lang.String value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_last_name</code>.
	 */
	public java.lang.String getCcLastName() {
		return (java.lang.String) getValue(5);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_type</code>.
	 */
	public void setCcType(java.lang.String value) {
		setValue(6, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_type</code>.
	 */
	public java.lang.String getCcType() {
		return (java.lang.String) getValue(6);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_exp_month</code>.
	 */
	public void setCcExpMonth(java.lang.String value) {
		setValue(7, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_exp_month</code>.
	 */
	public java.lang.String getCcExpMonth() {
		return (java.lang.String) getValue(7);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_exp_year</code>.
	 */
	public void setCcExpYear(java.lang.String value) {
		setValue(8, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_exp_year</code>.
	 */
	public java.lang.String getCcExpYear() {
		return (java.lang.String) getValue(8);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_number</code>.
	 */
	public void setCcNumber(java.lang.String value) {
		setValue(9, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_number</code>.
	 */
	public java.lang.String getCcNumber() {
		return (java.lang.String) getValue(9);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_last_4</code>.
	 */
	public void setCcLast_4(java.lang.String value) {
		setValue(10, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_last_4</code>.
	 */
	public java.lang.String getCcLast_4() {
		return (java.lang.String) getValue(10);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_start_month</code>.
	 */
	public void setCcStartMonth(java.lang.String value) {
		setValue(11, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_start_month</code>.
	 */
	public java.lang.String getCcStartMonth() {
		return (java.lang.String) getValue(11);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_start_year</code>.
	 */
	public void setCcStartYear(java.lang.String value) {
		setValue(12, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_start_year</code>.
	 */
	public java.lang.String getCcStartYear() {
		return (java.lang.String) getValue(12);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_issue_number</code>.
	 */
	public void setCcIssueNumber(java.lang.String value) {
		setValue(13, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_issue_number</code>.
	 */
	public java.lang.String getCcIssueNumber() {
		return (java.lang.String) getValue(13);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_verification_value</code>.
	 */
	public void setCcVerificationValue(java.lang.String value) {
		setValue(14, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_verification_value</code>.
	 */
	public java.lang.String getCcVerificationValue() {
		return (java.lang.String) getValue(14);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.cc_track_data</code>.
	 */
	public void setCcTrackData(java.lang.String value) {
		setValue(15, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.cc_track_data</code>.
	 */
	public java.lang.String getCcTrackData() {
		return (java.lang.String) getValue(15);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.address1</code>.
	 */
	public void setAddress1(java.lang.String value) {
		setValue(16, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.address1</code>.
	 */
	public java.lang.String getAddress1() {
		return (java.lang.String) getValue(16);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.address2</code>.
	 */
	public void setAddress2(java.lang.String value) {
		setValue(17, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.address2</code>.
	 */
	public java.lang.String getAddress2() {
		return (java.lang.String) getValue(17);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.city</code>.
	 */
	public void setCity(java.lang.String value) {
		setValue(18, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.city</code>.
	 */
	public java.lang.String getCity() {
		return (java.lang.String) getValue(18);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.state</code>.
	 */
	public void setState(java.lang.String value) {
		setValue(19, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.state</code>.
	 */
	public java.lang.String getState() {
		return (java.lang.String) getValue(19);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.zip</code>.
	 */
	public void setZip(java.lang.String value) {
		setValue(20, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.zip</code>.
	 */
	public java.lang.String getZip() {
		return (java.lang.String) getValue(20);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.country</code>.
	 */
	public void setCountry(java.lang.String value) {
		setValue(21, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.country</code>.
	 */
	public java.lang.String getCountry() {
		return (java.lang.String) getValue(21);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.is_default</code>.
	 */
	public void setIsDefault(java.lang.Byte value) {
		setValue(22, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.is_default</code>.
	 */
	public java.lang.Byte getIsDefault() {
		return (java.lang.Byte) getValue(22);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.is_deleted</code>.
	 */
	public void setIsDeleted(java.lang.Byte value) {
		setValue(23, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.is_deleted</code>.
	 */
	public java.lang.Byte getIsDeleted() {
		return (java.lang.Byte) getValue(23);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.additional_data</code>.
	 */
	public void setAdditionalData(java.lang.String value) {
		setValue(24, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.additional_data</code>.
	 */
	public java.lang.String getAdditionalData() {
		return (java.lang.String) getValue(24);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.created_date</code>.
	 */
	public void setCreatedDate(java.sql.Timestamp value) {
		setValue(25, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.created_date</code>.
	 */
	public java.sql.Timestamp getCreatedDate() {
		return (java.sql.Timestamp) getValue(25);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.updated_date</code>.
	 */
	public void setUpdatedDate(java.sql.Timestamp value) {
		setValue(26, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.updated_date</code>.
	 */
	public java.sql.Timestamp getUpdatedDate() {
		return (java.sql.Timestamp) getValue(26);
	}

	/**
	 * Setter for <code>killbill.payeezy_payment_methods.kb_tenant_id</code>.
	 */
	public void setKbTenantId(java.lang.String value) {
		setValue(27, value);
	}

	/**
	 * Getter for <code>killbill.payeezy_payment_methods.kb_tenant_id</code>.
	 */
	public java.lang.String getKbTenantId() {
		return (java.lang.String) getValue(27);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<org.jooq.types.UInteger> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PayeezyPaymentMethodsRecord
	 */
	public PayeezyPaymentMethodsRecord() {
		super(org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods.PAYEEZY_PAYMENT_METHODS);
	}

	/**
	 * Create a detached, initialised PayeezyPaymentMethodsRecord
	 */
	public PayeezyPaymentMethodsRecord(org.jooq.types.UInteger recordId, java.lang.String kbAccountId, java.lang.String kbPaymentMethodId, java.lang.String token, java.lang.String ccFirstName, java.lang.String ccLastName, java.lang.String ccType, java.lang.String ccExpMonth, java.lang.String ccExpYear, java.lang.String ccNumber, java.lang.String ccLast_4, java.lang.String ccStartMonth, java.lang.String ccStartYear, java.lang.String ccIssueNumber, java.lang.String ccVerificationValue, java.lang.String ccTrackData, java.lang.String address1, java.lang.String address2, java.lang.String city, java.lang.String state, java.lang.String zip, java.lang.String country, java.lang.Byte isDefault, java.lang.Byte isDeleted, java.lang.String additionalData, java.sql.Timestamp createdDate, java.sql.Timestamp updatedDate, java.lang.String kbTenantId) {
		super(org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods.PAYEEZY_PAYMENT_METHODS);

		setValue(0, recordId);
		setValue(1, kbAccountId);
		setValue(2, kbPaymentMethodId);
		setValue(3, token);
		setValue(4, ccFirstName);
		setValue(5, ccLastName);
		setValue(6, ccType);
		setValue(7, ccExpMonth);
		setValue(8, ccExpYear);
		setValue(9, ccNumber);
		setValue(10, ccLast_4);
		setValue(11, ccStartMonth);
		setValue(12, ccStartYear);
		setValue(13, ccIssueNumber);
		setValue(14, ccVerificationValue);
		setValue(15, ccTrackData);
		setValue(16, address1);
		setValue(17, address2);
		setValue(18, city);
		setValue(19, state);
		setValue(20, zip);
		setValue(21, country);
		setValue(22, isDefault);
		setValue(23, isDeleted);
		setValue(24, additionalData);
		setValue(25, createdDate);
		setValue(26, updatedDate);
		setValue(27, kbTenantId);
	}
}
