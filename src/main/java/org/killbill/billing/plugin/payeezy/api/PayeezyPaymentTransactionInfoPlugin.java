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

package org.killbill.billing.plugin.payeezy.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.plugin.api.payment.PluginPaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyResponsesRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.google.common.base.Strings;

public class PayeezyPaymentTransactionInfoPlugin extends PluginPaymentTransactionInfoPlugin {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int ERROR_CODE_MAX_LENGTH = 32;

    public PayeezyPaymentTransactionInfoPlugin(final UUID kbPaymentId,
                                               final UUID kbTransactionId,
                                               final TransactionType transactionType,
                                               @Nullable final BigDecimal amount,
                                               @Nullable final Currency currency,
                                               final DateTime utcNow,
                                               final TransactionResponse response) {
        super(kbPaymentId,
              kbTransactionId,
              transactionType,
              amount,
              currency,
              getPaymentPluginStatus(response.getTransactionStatus(), response.getValidationStatus()),
              toGatewayError(response.getBankMessage() != null ? response.getBankMessage() : response.getExactMessage()),
              response.getBankResponseCode() != null ? response.getBankResponseCode() : response.getExactResponseCode(),
              response.getTransactionId(),
              response.getTransactionTag(),
              utcNow,
              utcNow,
              PayeezyModelPluginBase.buildPluginProperties(null));
    }

    public PayeezyPaymentTransactionInfoPlugin(final PayeezyResponsesRecord record) {
        super(UUID.fromString(record.getKbPaymentId()),
              UUID.fromString(record.getKbPaymentTransactionId()),
              TransactionType.valueOf(record.getKbTransactionType()),
              record.getAmount(),
              Strings.isNullOrEmpty(record.getCurrency()) ? null : Currency.valueOf(record.getCurrency()),
              getPaymentPluginStatus(record.getTransactionStatus(), record.getValidationStatus()),
              toGatewayError(record.getBankMessage() != null ? record.getBankMessage() : record.getGatewayMessage()),
              record.getBankRespCode() != null ? record.getBankRespCode() : record.getGatewayRespCode(),
              record.getTransactionId(),
              record.getTransactionTag(),
              new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
              new DateTime(record.getCreatedDate(), DateTimeZone.UTC),
              PayeezyModelPluginBase.buildPluginProperties(record.getAdditionalData()));
    }

    private static String toGatewayError(@Nullable final String rawMsg) {
        try {
            final Map map = objectMapper.readValue(rawMsg, Map.class);
            if (map.get("payeezyMessage") != null) {
                final Map payeezyMessage = objectMapper.readValue((String) map.get("payeezyMessage"), Map.class);
                if (payeezyMessage.get("Error") != null && payeezyMessage.get("Error") instanceof Map) {
                    final Collection messages = (Collection) (((Map) payeezyMessage.get("Error")).get("messages"));
                    if (messages != null && !messages.isEmpty()) {
                        return truncate((String) ((Map) messages.iterator().next()).get("description"));
                    }
                }
            }
        } catch (final Exception ignored) {
        }

        return truncate(rawMsg);
    }

    private static String truncate(@Nullable final String string) {
        if (string == null) {
            return null;
        } else if (string.length() <= ERROR_CODE_MAX_LENGTH) {
            return string;
        } else {
            return string.substring(0, ERROR_CODE_MAX_LENGTH);
        }
    }

    private static PaymentPluginStatus getPaymentPluginStatus(final String transactionStatus, final String validationStatus) {
        // TODO
        return "success".equals(validationStatus) ? PaymentPluginStatus.PROCESSED : PaymentPluginStatus.ERROR;
    }
}
