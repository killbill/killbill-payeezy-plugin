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
import java.sql.SQLException;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.GatewayNotification;
import org.killbill.billing.payment.plugin.api.HostedPaymentPageFormDescriptor;
import org.killbill.billing.payment.plugin.api.PaymentMethodInfoPlugin;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.api.payment.PluginPaymentPluginApi;
import org.killbill.billing.plugin.payeezy.client.PayeezyClientWrapper;
import org.killbill.billing.plugin.payeezy.core.PayeezyConfigurationHandler;
import org.killbill.billing.plugin.payeezy.dao.PayeezyDao;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyPaymentMethods;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.PayeezyResponses;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyResponsesRecord;
import org.killbill.billing.plugin.util.KillBillMoney;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.clock.Clock;
import org.osgi.service.log.LogService;

import com.firstdata.payeezy.models.transaction.Address;
import com.firstdata.payeezy.models.transaction.Card;
import com.firstdata.payeezy.models.transaction.DebitCard;
import com.firstdata.payeezy.models.transaction.PaymentMethod;
import com.firstdata.payeezy.models.transaction.TransactionRequest;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.google.common.base.MoreObjects;

public class PayeezyPaymentPluginApi extends PluginPaymentPluginApi<PayeezyResponsesRecord, PayeezyResponses, PayeezyPaymentMethodsRecord, PayeezyPaymentMethods> {

    public static final String PROPERTY_EMAIL = "email";

    // SEPA
    public static final String PROPERTY_DD_HOLDER_NAME = "ddHolderName";
    public static final String PROPERTY_DD_ACCOUNT_NUMBER = "ddNumber";
    public static final String PROPERTY_DD_BANK_IDENTIFIER_CODE = "ddBic";
    public static final String PROPERTY_DD_MANDATE = "ddMandate";

    private final PayeezyConfigurationHandler payeezyConfigurationHandler;
    private final PayeezyDao dao;

    public PayeezyPaymentPluginApi(final PayeezyConfigurationHandler payeezyConfigurationHandler,
                                   final OSGIKillbillAPI killbillApi,
                                   final OSGIConfigPropertiesService osgiConfigPropertiesService,
                                   final OSGIKillbillLogService logService,
                                   final Clock clock,
                                   final PayeezyDao dao) throws JAXBException {
        super(killbillApi, osgiConfigPropertiesService, logService, clock, dao);
        this.payeezyConfigurationHandler = payeezyConfigurationHandler;
        this.dao = dao;
    }

    @Override
    protected PaymentTransactionInfoPlugin buildPaymentTransactionInfoPlugin(final PayeezyResponsesRecord adyenResponsesRecord) {
        return new PayeezyPaymentTransactionInfoPlugin(adyenResponsesRecord);
    }

    @Override
    protected PaymentMethodPlugin buildPaymentMethodPlugin(final PayeezyPaymentMethodsRecord paymentMethodsRecord) {
        return new PayeezyPaymentMethodPlugin(paymentMethodsRecord);
    }

    @Override
    protected PaymentMethodInfoPlugin buildPaymentMethodInfoPlugin(final PayeezyPaymentMethodsRecord paymentMethodsRecord) {
        return new PayeezyPaymentMethodInfoPlugin(paymentMethodsRecord);
    }

    @Override
    protected String getPaymentMethodId(final PayeezyPaymentMethodsRecord paymentMethodsRecord) {
        return paymentMethodsRecord.getKbPaymentMethodId();
    }

    @Override
    public PaymentTransactionInfoPlugin authorizePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeInitialTransaction(TransactionType.AUTHORIZE,
                                         kbAccountId,
                                         kbPaymentId,
                                         kbTransactionId,
                                         kbPaymentMethodId,
                                         amount,
                                         currency,
                                         properties,
                                         context);
    }

    @Override
    public PaymentTransactionInfoPlugin capturePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeFollowUpTransaction(TransactionType.CAPTURE,
                                          kbAccountId,
                                          kbPaymentId,
                                          kbTransactionId,
                                          kbPaymentMethodId,
                                          amount,
                                          currency,
                                          properties,
                                          context);
    }

    @Override
    public PaymentTransactionInfoPlugin purchasePayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeInitialTransaction(TransactionType.PURCHASE,
                                         kbAccountId,
                                         kbPaymentId,
                                         kbTransactionId,
                                         kbPaymentMethodId,
                                         amount,
                                         currency,
                                         properties,
                                         context);
    }

    @Override
    public PaymentTransactionInfoPlugin voidPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeFollowUpTransaction(TransactionType.VOID,
                                          kbAccountId,
                                          kbPaymentId,
                                          kbTransactionId,
                                          kbPaymentMethodId,
                                          null,
                                          null,
                                          properties,
                                          context);
    }

    @Override
    public PaymentTransactionInfoPlugin creditPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeInitialTransaction(TransactionType.CREDIT, kbAccountId, kbPaymentId, kbTransactionId, kbPaymentMethodId, amount, currency, properties, context);
    }

    @Override
    public PaymentTransactionInfoPlugin refundPayment(final UUID kbAccountId, final UUID kbPaymentId, final UUID kbTransactionId, final UUID kbPaymentMethodId, final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        return executeFollowUpTransaction(TransactionType.REFUND,
                                          kbAccountId,
                                          kbPaymentId,
                                          kbTransactionId,
                                          kbPaymentMethodId,
                                          amount,
                                          currency,
                                          properties,
                                          context);
    }

    @Override
    public HostedPaymentPageFormDescriptor buildFormDescriptor(final UUID kbAccountId, final Iterable<PluginProperty> customFields, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        throw new UnsupportedOperationException("To be implemented");
    }

    @Override
    public GatewayNotification processNotification(final String notification, final Iterable<PluginProperty> properties, final CallContext context) throws PaymentPluginApiException {
        // TODO Store notifications
        return new PayeezyGatewayNotification();
    }

    private PaymentTransactionInfoPlugin executeInitialTransaction(final TransactionType transactionType,
                                                                   final UUID kbAccountId,
                                                                   final UUID kbPaymentId,
                                                                   final UUID kbTransactionId,
                                                                   final UUID kbPaymentMethodId,
                                                                   final BigDecimal amount,
                                                                   final Currency currency,
                                                                   final Iterable<PluginProperty> properties,
                                                                   final TenantContext context) throws PaymentPluginApiException {
        return executeInitialTransaction(transactionType,
                                         new TransactionExecutor() {
                                             @Override
                                             public TransactionResponse execute(final TransactionRequest transactionRequest) {
                                                 final PayeezyClientWrapper payeezyClientWrapper = payeezyConfigurationHandler.getConfigurable(context.getTenantId());
                                                 if (hasPreviousPayeezyResponseRecord(kbPaymentId, kbTransactionId.toString(), context)) {
                                                     // We are completing a 3D-S payment
                                                     throw new UnsupportedOperationException("To be implemented");
                                                 } else {
                                                     // We are creating a new transaction (AUTHORIZE, PURCHASE or CREDIT)
                                                     return payeezyClientWrapper.triggerInitialTransaction(transactionRequest);
                                                 }
                                             }
                                         },
                                         kbAccountId,
                                         kbPaymentId,
                                         kbTransactionId,
                                         kbPaymentMethodId,
                                         amount,
                                         currency,
                                         properties,
                                         context);
    }

    private PaymentTransactionInfoPlugin executeInitialTransaction(final TransactionType transactionType,
                                                                   final TransactionExecutor transactionExecutor,
                                                                   final UUID kbAccountId,
                                                                   final UUID kbPaymentId,
                                                                   final UUID kbTransactionId,
                                                                   final UUID kbPaymentMethodId,
                                                                   final BigDecimal amount,
                                                                   final Currency currency,
                                                                   final Iterable<PluginProperty> properties,
                                                                   final TenantContext context) throws PaymentPluginApiException {
        final Account account = getAccount(kbAccountId, context);
        final PayeezyPaymentMethodsRecord nonNullPaymentMethodsRecord = getPayeezyPaymentMethodsRecord(kbPaymentMethodId, context);

        // Pull extra properties from the payment method
        final Iterable<PluginProperty> additionalPropertiesFromRecord = buildPaymentMethodPlugin(nonNullPaymentMethodsRecord).getProperties();
        //noinspection unchecked
        final Iterable<PluginProperty> mergedProperties = PluginProperties.merge(additionalPropertiesFromRecord, properties);
        final DateTime utcNow = clock.getUTCNow();

        final TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setReferenceNo(kbTransactionId.toString());
        transactionRequest.setTransactionType(transactionType.name().toLowerCase());
        if (amount != null && currency != null) {
            transactionRequest.setAmount(String.valueOf(KillBillMoney.toMinorUnits(currency.name(), amount)));
        }
        if (currency != null) {
            transactionRequest.setCurrency(currency.name());
        }
        transactionRequest.setRecurring(null);

        addCCData(transactionRequest, nonNullPaymentMethodsRecord, properties);
        addDDData(transactionRequest, nonNullPaymentMethodsRecord, properties);

        final Address address = new Address();
        address.setFirstName(PluginProperties.getValue(PROPERTY_CC_FIRST_NAME, nonNullPaymentMethodsRecord.getCcFirstName(), properties));
        address.setLastName(PluginProperties.getValue(PROPERTY_CC_LAST_NAME, nonNullPaymentMethodsRecord.getCcLastName(), properties));
        address.setName(PluginProperties.getValue(PROPERTY_DD_HOLDER_NAME, nonNullPaymentMethodsRecord.getDdHolderName(), properties));
        address.setEmail(PluginProperties.getValue(PROPERTY_EMAIL, account.getEmail(), properties));
        address.setCity(PluginProperties.getValue(PROPERTY_CITY, nonNullPaymentMethodsRecord.getCity(), properties));
        address.setCountry(PluginProperties.getValue(PROPERTY_COUNTRY, nonNullPaymentMethodsRecord.getCountry(), properties));
        address.setAddressLine1(PluginProperties.getValue(PROPERTY_ADDRESS1, nonNullPaymentMethodsRecord.getAddress1(), properties));
        address.setAddressLine2(PluginProperties.getValue(PROPERTY_ADDRESS2, nonNullPaymentMethodsRecord.getAddress2(), properties));
        address.setState(PluginProperties.getValue(PROPERTY_STATE, nonNullPaymentMethodsRecord.getState(), properties));
        address.setZip(PluginProperties.getValue(PROPERTY_ZIP, nonNullPaymentMethodsRecord.getZip(), properties));
        transactionRequest.setBilling(address);

        String payeezyPaymentMethod = PluginProperties.getValue("paymentMethod", null, mergedProperties);
        if (payeezyPaymentMethod == null) {
            payeezyPaymentMethod = transactionRequest.getDebitCard() != null ? "direct_debit" : PaymentMethod.CREDIT_CARD.getValue();
        }

        transactionRequest.setPaymentMethod(payeezyPaymentMethod);

        final TransactionResponse response = transactionExecutor.execute(transactionRequest);
        try {
            dao.addResponse(kbAccountId, kbPaymentId, kbTransactionId, transactionType, amount, currency, response, utcNow, context.getTenantId());
            return new PayeezyPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, transactionType, amount, currency, utcNow, response);
        } catch (final SQLException e) {
            throw new PaymentPluginApiException("Payment went through, but we encountered a database error. Payment details: " + response.toString(), e);
        }
    }

    private void addCCData(final TransactionRequest transactionRequest, final PayeezyPaymentMethodsRecord nonNullPaymentMethodsRecord, final Iterable<PluginProperty> properties) {
        boolean isNonEmpty = false;

        final Card card = new Card();

        final String ccType = PluginProperties.getValue(PROPERTY_CC_TYPE, nonNullPaymentMethodsRecord.getCcType(), properties);
        isNonEmpty = isNonEmpty || ccType != null;
        card.setType(ccType);

        final String ccVerificationValue = PluginProperties.getValue(PROPERTY_CC_VERIFICATION_VALUE, nonNullPaymentMethodsRecord.getCcVerificationValue(), properties);
        isNonEmpty = isNonEmpty || ccVerificationValue != null;
        card.setCvv(ccVerificationValue);

        final String ccFirstName = PluginProperties.getValue(PROPERTY_CC_FIRST_NAME, nonNullPaymentMethodsRecord.getCcFirstName(), properties);
        final String ccLastName = PluginProperties.getValue(PROPERTY_CC_LAST_NAME, nonNullPaymentMethodsRecord.getCcLastName(), properties);
        isNonEmpty = isNonEmpty || ccFirstName != null || ccLastName != null;
        if (ccFirstName != null || ccLastName != null) {
            card.setName(String.format("%s%s", ccFirstName == null ? "" : ccFirstName + " ", ccLastName));
        }

        final String ccNumber = PluginProperties.getValue(PROPERTY_CC_NUMBER, nonNullPaymentMethodsRecord.getCcNumber(), properties);
        isNonEmpty = isNonEmpty || ccNumber != null;
        card.setNumber(ccNumber);

        final String ccExpirationMonth = PluginProperties.getValue(PROPERTY_CC_EXPIRATION_MONTH, nonNullPaymentMethodsRecord.getCcExpMonth(), properties);
        final String ccExpirationYear = PluginProperties.getValue(PROPERTY_CC_EXPIRATION_YEAR, nonNullPaymentMethodsRecord.getCcExpYear(), properties);
        if (ccExpirationMonth != null && ccExpirationYear != null) {
            isNonEmpty = true;
            card.setExpiryDt(String.format("%s%s", ccExpirationMonth, ccExpirationYear.substring(ccExpirationYear.length() - 2, ccExpirationYear.length())));
        }

        if (isNonEmpty) {
            transactionRequest.setCard(card);
        }
    }

    private void addDDData(final TransactionRequest transactionRequest, final PayeezyPaymentMethodsRecord nonNullPaymentMethodsRecord, final Iterable<PluginProperty> properties) {
        boolean isNonEmpty = false;

        final DebitCard debitCard = new DebitCard();

        final String bic = PluginProperties.getValue(PROPERTY_DD_BANK_IDENTIFIER_CODE, nonNullPaymentMethodsRecord.getDdBic(), properties);
        isNonEmpty = isNonEmpty || bic != null;
        debitCard.setBic(bic);

        final String iban = PluginProperties.getValue(PROPERTY_DD_ACCOUNT_NUMBER, nonNullPaymentMethodsRecord.getDdIban(), properties);
        isNonEmpty = isNonEmpty || iban != null;
        debitCard.setIban(iban);

        final String mandateReference = PluginProperties.getValue(PROPERTY_DD_MANDATE, nonNullPaymentMethodsRecord.getDdMandate(), properties);
        isNonEmpty = isNonEmpty || mandateReference != null;
        debitCard.setMandateReference(mandateReference);

        if (isNonEmpty) {
            transactionRequest.setDebitCard(debitCard);
        }
    }

    private PaymentTransactionInfoPlugin executeFollowUpTransaction(final TransactionType transactionType,
                                                                    final UUID kbAccountId,
                                                                    final UUID kbPaymentId,
                                                                    final UUID kbTransactionId,
                                                                    final UUID kbPaymentMethodId,
                                                                    final BigDecimal amount,
                                                                    final Currency currency,
                                                                    final Iterable<PluginProperty> properties,
                                                                    final TenantContext context) throws PaymentPluginApiException {
        return executeFollowUpTransaction(transactionType,
                                          new TransactionExecutor() {
                                              @Override
                                              public TransactionResponse execute(final String id, final TransactionRequest transactionRequest) {
                                                  final PayeezyClientWrapper payeezyClientWrapper = payeezyConfigurationHandler.getConfigurable(context.getTenantId());
                                                  return payeezyClientWrapper.triggerFollowOnTransaction(id, transactionRequest);
                                              }
                                          },
                                          kbAccountId,
                                          kbPaymentId,
                                          kbTransactionId,
                                          kbPaymentMethodId,
                                          amount,
                                          currency,
                                          properties,
                                          context);
    }

    private PaymentTransactionInfoPlugin executeFollowUpTransaction(final TransactionType transactionType,
                                                                    final TransactionExecutor transactionExecutor,
                                                                    final UUID kbAccountId,
                                                                    final UUID kbPaymentId,
                                                                    final UUID kbTransactionId,
                                                                    final UUID kbPaymentMethodId,
                                                                    @Nullable final BigDecimal amount,
                                                                    @Nullable final Currency currency,
                                                                    final Iterable<PluginProperty> properties,
                                                                    final TenantContext context) throws PaymentPluginApiException {
        final Account account = getAccount(kbAccountId, context);
        final PayeezyPaymentMethodsRecord nonNullPaymentMethodsRecord = getPayeezyPaymentMethodsRecord(kbPaymentMethodId, context);

        final String transactionId;
        final String transactionTag;
        final BigDecimal nonNullAmount;
        final String nonNullCurrency;
        final String method;
        try {
            final PayeezyResponsesRecord previousResponse = dao.getSuccessfulAuthorizationResponse(kbPaymentId, context.getTenantId());
            if (previousResponse == null) {
                throw new PaymentPluginApiException(null, "Unable to retrieve previous payment response for kbTransactionId " + kbTransactionId);
            }
            transactionId = previousResponse.getTransactionId();
            transactionTag = previousResponse.getTransactionTag();
            nonNullAmount = previousResponse.getAmount();
            nonNullCurrency = previousResponse.getCurrency();
            method = previousResponse.getMethod();
        } catch (final SQLException e) {
            throw new PaymentPluginApiException("Unable to retrieve previous payment response for kbTransactionId " + kbTransactionId, e);
        }

        final DateTime utcNow = clock.getUTCNow();

        final TransactionRequest followUpRequest = new TransactionRequest();
        followUpRequest.setReferenceNo(kbTransactionId.toString());
        followUpRequest.setTransactionTag(transactionTag);
        followUpRequest.setTransactionType(transactionType.name().toLowerCase());
        followUpRequest.setCurrency(currency == null ? nonNullCurrency : currency.name());
        followUpRequest.setAmount(String.valueOf(KillBillMoney.toMinorUnits(followUpRequest.getCurrency(), amount == null ? nonNullAmount : amount)));
        followUpRequest.setPaymentMethod(method);

        final TransactionResponse response = transactionExecutor.execute(transactionId, followUpRequest);
        try {
            dao.addResponse(kbAccountId, kbPaymentId, kbTransactionId, transactionType, amount, currency, response, utcNow, context.getTenantId());
            return new PayeezyPaymentTransactionInfoPlugin(kbPaymentId, kbTransactionId, transactionType, amount, currency, utcNow, response);
        } catch (final SQLException e) {
            throw new PaymentPluginApiException("Payment went through, but we encountered a database error. Payment details: " + (response.toString()), e);
        }
    }

    private PayeezyPaymentMethodsRecord emptyRecord(@Nullable final UUID kbPaymentMethodId) {
        final PayeezyPaymentMethodsRecord record = new PayeezyPaymentMethodsRecord();
        if (kbPaymentMethodId != null) {
            record.setKbPaymentMethodId(kbPaymentMethodId.toString());
        }
        return record;
    }

    private PayeezyPaymentMethodsRecord getPayeezyPaymentMethodsRecord(@Nullable final UUID kbPaymentMethodId, final TenantContext context) {
        PayeezyPaymentMethodsRecord paymentMethodsRecord = null;

        if (kbPaymentMethodId != null) {
            try {
                paymentMethodsRecord = dao.getPaymentMethod(kbPaymentMethodId, context.getTenantId());
            } catch (final SQLException e) {
                logService.log(LogService.LOG_WARNING, "Failed to retrieve payment method " + kbPaymentMethodId, e);
            }
        }

        return MoreObjects.firstNonNull(paymentMethodsRecord, emptyRecord(kbPaymentMethodId));
    }

    private boolean hasPreviousPayeezyResponseRecord(final UUID kbPaymentId, final String kbPaymentTransactionId, final TenantContext context) {
        try {
            final PayeezyResponsesRecord previousAuthorizationResponse = dao.getSuccessfulAuthorizationResponse(kbPaymentId, context.getTenantId());
            return previousAuthorizationResponse != null && previousAuthorizationResponse.getKbPaymentTransactionId().equals(kbPaymentTransactionId);
        } catch (final SQLException e) {
            logService.log(LogService.LOG_ERROR, "Failed to get previous PayeezyResponsesRecord", e);
            return false;
        }
    }

    private abstract static class TransactionExecutor {

        public TransactionResponse execute(final TransactionRequest transactionRequest) {
            throw new UnsupportedOperationException();
        }

        public TransactionResponse execute(final String id, final TransactionRequest transactionRequest) {
            throw new UnsupportedOperationException();
        }
    }
}
