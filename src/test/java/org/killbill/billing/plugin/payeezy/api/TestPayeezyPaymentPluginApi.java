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
import java.util.List;

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.payment.api.Payment;
import org.killbill.billing.payment.api.PaymentApiException;
import org.killbill.billing.payment.api.PaymentMethodPlugin;
import org.killbill.billing.payment.api.PaymentTransaction;
import org.killbill.billing.payment.api.PluginProperty;
import org.killbill.billing.payment.api.TransactionType;
import org.killbill.billing.payment.plugin.api.PaymentPluginApiException;
import org.killbill.billing.payment.plugin.api.PaymentPluginStatus;
import org.killbill.billing.payment.plugin.api.PaymentTransactionInfoPlugin;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.api.PluginProperties;
import org.killbill.billing.plugin.api.payment.PluginPaymentPluginApi;
import org.killbill.billing.plugin.payeezy.TestRemoteBase;
import org.killbill.billing.plugin.payeezy.dao.PayeezyDao;
import org.killbill.billing.plugin.payeezy.dao.gen.tables.records.PayeezyPaymentMethodsRecord;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class TestPayeezyPaymentPluginApi extends TestRemoteBase {

    private final Iterable<PluginProperty> propertiesWithCCInfo = PluginProperties.buildPluginProperties(ImmutableMap.<String, String>builder()
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_CC_TYPE, CC_TYPE)
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_CC_LAST_NAME, "Dupont")
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_CC_NUMBER, CC_NUMBER)
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_CC_EXPIRATION_MONTH, String.valueOf(CC_EXPIRATION_MONTH))
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_CC_EXPIRATION_YEAR, String.valueOf(CC_EXPIRATION_YEAR))
                                                                                                                 .build());

    private final Iterable<PluginProperty> propertiesWithTokenInfo = PluginProperties.buildPluginProperties(ImmutableMap.<String, String>builder()
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_CC_TYPE, CC_TYPE)
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_TOKEN, "2537446225198291")
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_CC_FIRST_NAME, "John")
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_CC_LAST_NAME, "Smith")
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_CC_EXPIRATION_MONTH, String.valueOf(CC_EXPIRATION_MONTH))
                                                                                                                    .put(PayeezyPaymentPluginApi.PROPERTY_CC_EXPIRATION_YEAR, String.valueOf(CC_EXPIRATION_YEAR))
                                                                                                                    .build());

    private final Iterable<PluginProperty> propertiesWithDDInfo = PluginProperties.buildPluginProperties(ImmutableMap.<String, String>builder()
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_DD_HOLDER_NAME, "John Smith")
                                                                                                                 .put(PluginPaymentPluginApi.PROPERTY_ADDRESS1, "3 Prinzregentenstrasse")
                                                                                                                 .put(PluginPaymentPluginApi.PROPERTY_CITY, "Munich")
                                                                                                                 .put(PluginPaymentPluginApi.PROPERTY_STATE, "Neuhausen-Nymphenburg")
                                                                                                                 .put(PluginPaymentPluginApi.PROPERTY_ZIP, "80331")
                                                                                                                 .put(PluginPaymentPluginApi.PROPERTY_COUNTRY, "Germany")
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_DD_ACCOUNT_NUMBER, "DE34500100600032121604")
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_DD_BANK_IDENTIFIER_CODE, "PBNKDEFFXXX")
                                                                                                                 .put(PayeezyPaymentPluginApi.PROPERTY_DD_MANDATE, "ABCD1234")
                                                                                                                 .build());

    private static PaymentMethodPlugin payeezyPaymentMethodPlugin(final String paymentMethodId, final String additionalData) {
        final PayeezyPaymentMethodsRecord record = new PayeezyPaymentMethodsRecord();
        record.setKbPaymentMethodId(paymentMethodId);
        record.setIsDefault(PayeezyDao.TRUE);
        if (!Strings.isNullOrEmpty(additionalData)) {
            record.setAdditionalData(additionalData);
        }
        return new PayeezyPaymentMethodPlugin(record);
    }

    @Test(groups = "slow")
    public void testAuthorizeCaptureAndRefundToken() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithTokenInfo, context);

        final Payment payment = doAuthorize(BigDecimal.TEN, account.getCurrency(), PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of()));
        doCapture(payment, BigDecimal.TEN);
        doRefund(payment, BigDecimal.TEN);
    }

    @Test(groups = "slow")
    public void testAuthorizeCaptureAndRefund() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithCCInfo, context);

        final Payment payment = doAuthorize(BigDecimal.TEN, account.getCurrency(), PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(PayeezyPaymentPluginApi.PROPERTY_CC_VERIFICATION_VALUE, CC_VERIFICATION_VALUE)));
        doCapture(payment, BigDecimal.TEN);
        doRefund(payment, BigDecimal.TEN);
    }

    @Test(groups = "slow")
    public void testAuthorizeAndVoid() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithCCInfo, context);

        final Payment payment = doAuthorize(BigDecimal.TEN, account.getCurrency(), PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(PayeezyPaymentPluginApi.PROPERTY_CC_VERIFICATION_VALUE, CC_VERIFICATION_VALUE)));
        doVoid(payment);
    }

    @Test(groups = "slow")
    public void testPurchaseAndRefund() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithCCInfo, context);

        final Payment payment = doPurchase(BigDecimal.TEN, account.getCurrency(), PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of(PayeezyPaymentPluginApi.PROPERTY_CC_VERIFICATION_VALUE, CC_VERIFICATION_VALUE)));
        doRefund(payment, BigDecimal.TEN);
    }

    @Test(groups = "slow", enabled = false, description = "Not supported on sandbox but verified on https://developer.payeezy.com/payeezy-api/apis/post/transactions-2")
    public void testPurchaseAndRefundDD() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithDDInfo, context);

        final Payment payment = doPurchase(BigDecimal.TEN, Currency.EUR, PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of()));
        doRefund(payment, BigDecimal.TEN);
    }

    @Test(groups = "slow", enabled = false, description = "Not supported on sandbox but verified on https://developer.payeezy.com/payeezy-api/apis/post/transactions-2")
    public void testCreditDD() throws Exception {
        payeezyPaymentPluginApi.addPaymentMethod(account.getId(), account.getPaymentMethodId(), payeezyEmptyPaymentMethodPlugin(), true, propertiesWithDDInfo, context);

        doCredit(BigDecimal.TEN, Currency.EUR, PluginProperties.buildPluginProperties(ImmutableMap.<String, String>of()));
    }

    private Payment doAuthorize(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException, PaymentApiException {
        return doPluginCall(amount,
                            currency,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.authorizePayment(account.getId(),
                                                                                    payment.getId(),
                                                                                    paymentTransaction.getId(),
                                                                                    payment.getPaymentMethodId(),
                                                                                    paymentTransaction.getAmount(),
                                                                                    paymentTransaction.getCurrency(),
                                                                                    pluginProperties,
                                                                                    context);
                                }
                            });
    }

    private Payment doPurchase(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException, PaymentApiException {
        return doPluginCall(amount,
                            currency,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.purchasePayment(account.getId(),
                                                                                   payment.getId(),
                                                                                   paymentTransaction.getId(),
                                                                                   payment.getPaymentMethodId(),
                                                                                   paymentTransaction.getAmount(),
                                                                                   paymentTransaction.getCurrency(),
                                                                                   pluginProperties,
                                                                                   context);
                                }
                            });
    }

    private Payment doCredit(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException, PaymentApiException {
        return doPluginCall(amount,
                            currency,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.creditPayment(account.getId(),
                                                                                 payment.getId(),
                                                                                 paymentTransaction.getId(),
                                                                                 payment.getPaymentMethodId(),
                                                                                 paymentTransaction.getAmount(),
                                                                                 paymentTransaction.getCurrency(),
                                                                                 pluginProperties,
                                                                                 context);
                                }
                            });
    }

    private Payment doCapture(final Payment payment, final BigDecimal amount) throws PaymentPluginApiException {
        return doCapture(payment, amount, ImmutableList.<PluginProperty>of());
    }

    private Payment doCapture(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
        return doPluginCall(payment,
                            amount,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.capturePayment(account.getId(),
                                                                                  payment.getId(),
                                                                                  paymentTransaction.getId(),
                                                                                  payment.getPaymentMethodId(),
                                                                                  paymentTransaction.getAmount(),
                                                                                  paymentTransaction.getCurrency(),
                                                                                  pluginProperties,
                                                                                  context);
                                }
                            });
    }

    private Payment doRefund(final Payment payment, final BigDecimal amount) throws PaymentPluginApiException {
        return doRefund(payment, amount, ImmutableList.<PluginProperty>of());
    }

    private Payment doRefund(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
        return doPluginCall(payment,
                            amount,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.refundPayment(account.getId(),
                                                                                 payment.getId(),
                                                                                 paymentTransaction.getId(),
                                                                                 payment.getPaymentMethodId(),
                                                                                 paymentTransaction.getAmount(),
                                                                                 paymentTransaction.getCurrency(),
                                                                                 pluginProperties,
                                                                                 context);
                                }
                            });
    }

    private Payment doVoid(final Payment payment) throws PaymentPluginApiException {
        return doVoid(payment, ImmutableList.<PluginProperty>of());
    }

    private Payment doVoid(final Payment payment, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
        return doPluginCall(payment,
                            null,
                            pluginProperties,
                            new PluginCall() {
                                @Override
                                public PaymentTransactionInfoPlugin execute(final Payment payment, final PaymentTransaction paymentTransaction, final Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException {
                                    return payeezyPaymentPluginApi.voidPayment(account.getId(),
                                                                               payment.getId(),
                                                                               paymentTransaction.getId(),
                                                                               payment.getPaymentMethodId(),
                                                                               pluginProperties,
                                                                               context);
                                }
                            });
    }

    private Payment doPluginCall(final BigDecimal amount, final Currency currency, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException, PaymentApiException {
        final Payment payment = TestUtils.buildPayment(account.getId(), account.getPaymentMethodId(), currency, killbillApi);
        return doPluginCall(payment, amount, pluginProperties, pluginCall);
    }

    private Payment doPluginCall(final Payment payment, final BigDecimal amount, final Iterable<PluginProperty> pluginProperties, final PluginCall pluginCall) throws PaymentPluginApiException {
        final PaymentTransaction paymentTransaction = TestUtils.buildPaymentTransaction(payment, null, amount, payment.getCurrency());

        final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin = pluginCall.execute(payment, paymentTransaction, pluginProperties);
        TestUtils.updatePaymentTransaction(paymentTransaction, paymentTransactionInfoPlugin);

        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPlugin);

        assertEquals(paymentTransactionInfoPlugin.getStatus(), PaymentPluginStatus.PROCESSED);
        assertEquals(paymentTransaction.getPaymentInfoPlugin().getStatus(), PaymentPluginStatus.PROCESSED);

        return payment;
    }

    private void verifyPaymentTransactionInfoPlugin(final Payment payment, final PaymentTransaction paymentTransaction, final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin) throws PaymentPluginApiException {
        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPlugin, true);

        // Verify we can fetch the details
        final List<PaymentTransactionInfoPlugin> paymentTransactionInfoPlugins = payeezyPaymentPluginApi.getPaymentInfo(account.getId(), paymentTransactionInfoPlugin.getKbPaymentId(), ImmutableList.<PluginProperty>of(), context);
        final PaymentTransactionInfoPlugin paymentTransactionInfoPluginFetched = Iterables.<PaymentTransactionInfoPlugin>find(Lists.<PaymentTransactionInfoPlugin>reverse(paymentTransactionInfoPlugins),
                                                                                                                              new Predicate<PaymentTransactionInfoPlugin>() {
                                                                                                                                  @Override
                                                                                                                                  public boolean apply(final PaymentTransactionInfoPlugin input) {
                                                                                                                                      return input.getKbTransactionPaymentId().equals(paymentTransaction.getId());
                                                                                                                                  }
                                                                                                                              });
        verifyPaymentTransactionInfoPlugin(payment, paymentTransaction, paymentTransactionInfoPluginFetched, true);
    }

    private void verifyPaymentTransactionInfoPlugin(final Payment payment, final PaymentTransaction paymentTransaction, final PaymentTransactionInfoPlugin paymentTransactionInfoPlugin, final boolean authorizedProcessed) {
        assertEquals(paymentTransactionInfoPlugin.getKbPaymentId(), payment.getId());
        assertEquals(paymentTransactionInfoPlugin.getKbTransactionPaymentId(), paymentTransaction.getId());
        assertEquals(paymentTransactionInfoPlugin.getTransactionType(), paymentTransaction.getTransactionType());
        if (TransactionType.VOID.equals(paymentTransaction.getTransactionType())) {
            assertNull(paymentTransactionInfoPlugin.getAmount());
            assertNull(paymentTransactionInfoPlugin.getCurrency());
        } else {
            assertEquals(paymentTransactionInfoPlugin.getAmount().compareTo(paymentTransaction.getAmount()), 0);
            assertEquals(paymentTransactionInfoPlugin.getCurrency(), paymentTransaction.getCurrency());
        }
        assertNotNull(paymentTransactionInfoPlugin.getCreatedDate());
        assertNotNull(paymentTransactionInfoPlugin.getEffectiveDate());

        final String expectedGatewayErrorCode;
        final PaymentPluginStatus expectedPaymentPluginStatus;
        switch (paymentTransaction.getTransactionType()) {
            case PURCHASE:
            case AUTHORIZE:
                expectedGatewayErrorCode = "100";
                expectedPaymentPluginStatus = PaymentPluginStatus.PROCESSED;
                break;
            case CAPTURE:
                expectedGatewayErrorCode = "100";
                expectedPaymentPluginStatus = PaymentPluginStatus.PROCESSED;
                break;
            case REFUND:
                expectedGatewayErrorCode = "100";
                expectedPaymentPluginStatus = PaymentPluginStatus.PROCESSED;
                break;
            case VOID:
                expectedGatewayErrorCode = "100";
                expectedPaymentPluginStatus = PaymentPluginStatus.PROCESSED;
                break;
            default:
                expectedGatewayErrorCode = "100";
                expectedPaymentPluginStatus = PaymentPluginStatus.PROCESSED;
                break;
        }
        assertEquals(paymentTransactionInfoPlugin.getGatewayError(), "Approved");
        assertEquals(paymentTransactionInfoPlugin.getGatewayErrorCode(), expectedGatewayErrorCode);
        assertEquals(paymentTransactionInfoPlugin.getStatus(), expectedPaymentPluginStatus);

        assertNotNull(paymentTransactionInfoPlugin.getFirstPaymentReferenceId());
        assertNotNull(paymentTransactionInfoPlugin.getSecondPaymentReferenceId());
    }

    private PaymentMethodPlugin payeezyEmptyPaymentMethodPlugin() {
        return payeezyPaymentMethodPlugin(account.getPaymentMethodId().toString(), null);
    }

    private interface PluginCall {

        PaymentTransactionInfoPlugin execute(Payment payment, PaymentTransaction paymentTransaction, Iterable<PluginProperty> pluginProperties) throws PaymentPluginApiException;
    }

}
