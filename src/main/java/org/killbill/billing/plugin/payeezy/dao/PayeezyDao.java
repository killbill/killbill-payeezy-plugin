/*
 * Copyright 2016 Groupon, Inc
 * Copyright 2016 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.plugin.payeezy.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.jooq.impl.DSL;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.plugin.api.payment.PluginPaymentPluginApi;
import org.killbill.billing.plugin.dao.payment.PluginPaymentDao;
import org.killbill.billing.plugin.payeezy.api.PayeezyPaymentPluginApi;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyResponses;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyResponsesRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import static org.killbill.billing.plugin.payeezy.dao.gen.Tables.PAYEEZY_PAYMENT_METHODS;
import static org.killbill.billing.plugin.payeezy.dao.gen.Tables.PAYEEZY_RESPONSES;

public class PayeezyDao extends PluginPaymentDao<PayeezyResponsesRecord, PayeezyResponses, PayeezyPaymentMethodsRecord, PayeezyPaymentMethods> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Joiner JOINER = Joiner.on(",");

    public PayeezyDao(final DataSource dataSource) throws SQLException {
        super(PayeezyResponses.PAYEEZY_RESPONSES, PayeezyPaymentMethods.PAYEEZY_PAYMENT_METHODS, dataSource);
    }

    public static Map fromAdditionalData(final String additionalData) {
        if (additionalData == null) {
            return ImmutableMap.of();
        }

        try {
            return objectMapper.readValue(additionalData, Map.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toAdditionalData(final Object additionalData) {
        if (additionalData == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(additionalData);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Payment methods

    @Override
    public void addPaymentMethod(final UUID kbAccountId, final UUID kbPaymentMethodId, final boolean isDefault, final Map<String, String> properties, final DateTime utcNow, final UUID kbTenantId) throws SQLException {
        /* Clone our properties, what we have been given might be unmodifiable */
        final Map<String, String> clonedProperties = new HashMap<String, String>(properties);

        /* Extract and remove known values from the properties map that will become "additional data" */
        final String token = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_TOKEN);
        final String ccFirstName = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_FIRST_NAME);
        final String ccLastName = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_LAST_NAME);
        final String ccType = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_TYPE);
        final String ccExpirationMonth = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_EXPIRATION_MONTH);
        final String ccExpirationYear = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_EXPIRATION_YEAR);
        final String ccNumber = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_NUMBER);
        final String ccStartMonth = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_START_MONTH);
        final String ccStartYear = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_START_YEAR);
        final String ccIssueNumber = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_ISSUE_NUMBER);
        final String ccVerificationValue = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_VERIFICATION_VALUE);
        final String ccTrackData = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CC_TRACK_DATA);
        final String ddHolderName = clonedProperties.remove(PayeezyPaymentPluginApi.PROPERTY_DD_HOLDER_NAME);
        final String ddIban = clonedProperties.remove(PayeezyPaymentPluginApi.PROPERTY_DD_ACCOUNT_NUMBER);
        final String ddBic = clonedProperties.remove(PayeezyPaymentPluginApi.PROPERTY_DD_BANK_IDENTIFIER_CODE);
        final String ddMandate = clonedProperties.remove(PayeezyPaymentPluginApi.PROPERTY_DD_MANDATE);
        final String address1 = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_ADDRESS1);
        final String address2 = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_ADDRESS2);
        final String city = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_CITY);
        final String state = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_STATE);
        final String zip = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_ZIP);
        final String country = clonedProperties.remove(PluginPaymentPluginApi.PROPERTY_COUNTRY);

        /* Calculate last 4 digits of the credit card number */
        final String ccLast4 = ccNumber == null ? null : ccNumber.substring(ccNumber.length() - 4, ccNumber.length());

        /* Calculate the additional data to store */
        final String additionalData = asString(clonedProperties);

        /* Store computed data */
        execute(dataSource.getConnection(),
                new WithConnectionCallback<Void>() {
                    @Override
                    public Void withConnection(final Connection conn) throws SQLException {
                        DSL.using(conn, dialect, settings)
                           .insertInto(paymentMethodsTable,
                                       PAYEEZY_PAYMENT_METHODS.KB_ACCOUNT_ID,
                                       PAYEEZY_PAYMENT_METHODS.KB_PAYMENT_METHOD_ID,
                                       PAYEEZY_PAYMENT_METHODS.TOKEN,
                                       PAYEEZY_PAYMENT_METHODS.CC_FIRST_NAME,
                                       PAYEEZY_PAYMENT_METHODS.CC_LAST_NAME,
                                       PAYEEZY_PAYMENT_METHODS.CC_TYPE,
                                       PAYEEZY_PAYMENT_METHODS.CC_EXP_MONTH,
                                       PAYEEZY_PAYMENT_METHODS.CC_EXP_YEAR,
                                       PAYEEZY_PAYMENT_METHODS.CC_NUMBER,
                                       PAYEEZY_PAYMENT_METHODS.CC_LAST_4,
                                       PAYEEZY_PAYMENT_METHODS.CC_START_MONTH,
                                       PAYEEZY_PAYMENT_METHODS.CC_START_YEAR,
                                       PAYEEZY_PAYMENT_METHODS.CC_ISSUE_NUMBER,
                                       PAYEEZY_PAYMENT_METHODS.CC_VERIFICATION_VALUE,
                                       PAYEEZY_PAYMENT_METHODS.CC_TRACK_DATA,
                                       PAYEEZY_PAYMENT_METHODS.DD_HOLDER_NAME,
                                       PAYEEZY_PAYMENT_METHODS.DD_BIC,
                                       PAYEEZY_PAYMENT_METHODS.DD_IBAN,
                                       PAYEEZY_PAYMENT_METHODS.DD_MANDATE,
                                       PAYEEZY_PAYMENT_METHODS.ADDRESS1,
                                       PAYEEZY_PAYMENT_METHODS.ADDRESS2,
                                       PAYEEZY_PAYMENT_METHODS.CITY,
                                       PAYEEZY_PAYMENT_METHODS.STATE,
                                       PAYEEZY_PAYMENT_METHODS.ZIP,
                                       PAYEEZY_PAYMENT_METHODS.COUNTRY,
                                       PAYEEZY_PAYMENT_METHODS.IS_DEFAULT,
                                       PAYEEZY_PAYMENT_METHODS.IS_DELETED,
                                       PAYEEZY_PAYMENT_METHODS.ADDITIONAL_DATA,
                                       PAYEEZY_PAYMENT_METHODS.CREATED_DATE,
                                       PAYEEZY_PAYMENT_METHODS.UPDATED_DATE,
                                       PAYEEZY_PAYMENT_METHODS.KB_TENANT_ID)
                           .values(kbAccountId.toString(),
                                   kbPaymentMethodId.toString(),
                                   token,
                                   ccFirstName,
                                   ccLastName,
                                   ccType,
                                   ccExpirationMonth,
                                   ccExpirationYear,
                                   ccNumber,
                                   ccLast4,
                                   ccStartMonth,
                                   ccStartYear,
                                   ccIssueNumber,
                                   ccVerificationValue,
                                   ccTrackData,
                                   ddHolderName,
                                   ddBic,
                                   ddIban,
                                   ddMandate,
                                   address1,
                                   address2,
                                   city,
                                   state,
                                   zip,
                                   country,
                                   fromBoolean(isDefault),
                                   FALSE,
                                   additionalData,
                                   toTimestamp(utcNow),
                                   toTimestamp(utcNow),
                                   kbTenantId.toString())
                           .execute();
                        return null;
                    }
                });
    }

    // Responses

    public PayeezyResponsesRecord addResponse(final UUID kbAccountId,
                                              final UUID kbPaymentId,
                                              final UUID kbPaymentTransactionId,
                                              final TransactionType transactionType,
                                              final BigDecimal amount,
                                              final Currency currency,
                                              final TransactionResponse response,
                                              final DateTime utcNow,
                                              final UUID kbTenantId) throws SQLException {
        final String additionalData = toAdditionalData(response);

        return execute(dataSource.getConnection(),
                       new WithConnectionCallback<PayeezyResponsesRecord>() {
                           @Override
                           public PayeezyResponsesRecord withConnection(final Connection conn) throws SQLException {
                               DSL.using(conn, dialect, settings)
                                  .insertInto(PAYEEZY_RESPONSES,
                                              PAYEEZY_RESPONSES.KB_ACCOUNT_ID,
                                              PAYEEZY_RESPONSES.KB_PAYMENT_ID,
                                              PAYEEZY_RESPONSES.KB_PAYMENT_TRANSACTION_ID,
                                              PAYEEZY_RESPONSES.KB_TRANSACTION_TYPE,
                                              PAYEEZY_RESPONSES.AMOUNT,
                                              PAYEEZY_RESPONSES.CURRENCY,
                                              PAYEEZY_RESPONSES.AVS,
                                              PAYEEZY_RESPONSES.CVV2,
                                              PAYEEZY_RESPONSES.METHOD,
                                              PAYEEZY_RESPONSES.CARD_TYPE,
                                              PAYEEZY_RESPONSES.CARDHOLDER_NAME,
                                              PAYEEZY_RESPONSES.CARD_NUMBER,
                                              PAYEEZY_RESPONSES.EXP_DATE,
                                              PAYEEZY_RESPONSES.TOKEN_TYPE,
                                              PAYEEZY_RESPONSES.TOKEN_DATA,
                                              PAYEEZY_RESPONSES.TRANSACTION_STATUS,
                                              PAYEEZY_RESPONSES.VALIDATION_STATUS,
                                              PAYEEZY_RESPONSES.TRANSACTION_TYPE,
                                              PAYEEZY_RESPONSES.TRANSACTION_ID,
                                              PAYEEZY_RESPONSES.TRANSACTION_TAG,
                                              PAYEEZY_RESPONSES.MERCHANT_REF,
                                              PAYEEZY_RESPONSES.BANK_RESP_CODE,
                                              PAYEEZY_RESPONSES.BANK_MESSAGE,
                                              PAYEEZY_RESPONSES.GATEWAY_RESP_CODE,
                                              PAYEEZY_RESPONSES.GATEWAY_MESSAGE,
                                              PAYEEZY_RESPONSES.CORRELATION_ID,
                                              PAYEEZY_RESPONSES.ADDITIONAL_DATA,
                                              PAYEEZY_RESPONSES.CREATED_DATE,
                                              PAYEEZY_RESPONSES.KB_TENANT_ID)
                                  .values(kbAccountId.toString(),
                                          kbPaymentId.toString(),
                                          kbPaymentTransactionId.toString(),
                                          transactionType.toString(),
                                          amount,
                                          currency,
                                          response.getAvs(),
                                          response.getCvv2(),
                                          response.getMethod(),
                                          response.getCard() == null ? null : response.getCard().getType(),
                                          response.getCard() == null ? null : response.getCard().getName(),
                                          response.getCard() == null ? null : response.getCard().getNumber(),
                                          response.getCard() == null ? null : response.getCard().getExpiryDt(),
                                          response.getToken() == null ? null : response.getToken().getTokenType(),
                                          response.getToken() == null ? null : response.getToken().getTokenData(),
                                          response.getTransactionStatus(),
                                          response.getValidationStatus(),
                                          response.getTransactionType(),
                                          response.getTransactionId(),
                                          response.getTransactionTag(),
                                          null,
                                          response.getBankResponseCode(),
                                          response.getBankMessage(),
                                          response.getExactResponseCode(),
                                          response.getExactMessage(),
                                          response.getCorrID(),
                                          additionalData,
                                          toTimestamp(utcNow),
                                          kbTenantId.toString())
                                  .execute();

                               return DSL.using(conn, dialect, settings)
                                         .selectFrom(PAYEEZY_RESPONSES)
                                         .where(PAYEEZY_RESPONSES.KB_PAYMENT_TRANSACTION_ID.equal(kbPaymentTransactionId.toString()))
                                         .and(PAYEEZY_RESPONSES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                         .orderBy(PAYEEZY_RESPONSES.RECORD_ID.desc())
                                         .limit(1)
                                         .fetchOne();
                           }
                       });
    }

    // Assumes that the last transaction was successful
    public PayeezyResponsesRecord getSuccessfulAuthorizationResponse(final UUID kbPaymentId, final UUID kbTenantId) throws SQLException {
        return execute(dataSource.getConnection(),
                       new WithConnectionCallback<PayeezyResponsesRecord>() {
                           @Override
                           public PayeezyResponsesRecord withConnection(final Connection conn) throws SQLException {
                               return DSL.using(conn, dialect, settings)
                                         .selectFrom(responsesTable)
                                         .where(PAYEEZY_RESPONSES.KB_PAYMENT_ID.equal(kbPaymentId.toString()))
                                         .and(PAYEEZY_RESPONSES.KB_TRANSACTION_TYPE.in(TransactionType.AUTHORIZE.toString(), TransactionType.PURCHASE.toString(), TransactionType.CAPTURE.toString()))
                                         .and(PAYEEZY_RESPONSES.KB_TENANT_ID.equal(kbTenantId.toString()))
                                         .orderBy(PAYEEZY_RESPONSES.RECORD_ID.desc())
                                         .limit(1)
                                         .fetchOne();
                           }
                       });
    }
}
