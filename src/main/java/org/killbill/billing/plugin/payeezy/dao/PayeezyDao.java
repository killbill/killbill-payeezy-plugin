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
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.jooq.impl.DSL;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.plugin.dao.payment.PluginPaymentDao;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyResponses;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyResponsesRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

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
