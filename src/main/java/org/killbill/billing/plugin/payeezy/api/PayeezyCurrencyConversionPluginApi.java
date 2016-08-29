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
import java.util.Set;

import org.joda.time.DateTime;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.currency.api.CurrencyConversion;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.currency.api.CurrencyConversionException;
import org.killbill.billing.plugin.payeezy.client.PayeezyClientWrapper;
import org.killbill.billing.plugin.payeezy.core.PayeezyConfigurationHandler;
import org.killbill.billing.plugin.util.KillBillMoney;

import com.firstdata.payeezy.models.transaction.CurrencyConversionResponse;
import com.firstdata.payeezy.models.transaction.TransactionRequest;
import com.google.common.collect.Sets;

public class PayeezyCurrencyConversionPluginApi implements CurrencyConversionApi {

    private final PayeezyConfigurationHandler payeezyConfigurationHandler;

    public PayeezyCurrencyConversionPluginApi(final PayeezyConfigurationHandler payeezyConfigurationHandler) {
        this.payeezyConfigurationHandler = payeezyConfigurationHandler;
    }

    @Override
    public Set<Currency> getBaseRates() throws CurrencyConversionException {
        // Actual list is here: https://developer.payeezy.com/faqs/what-currencies-does-payeezy-support
        return Sets.newHashSet(Currency.values());
    }

    @Override
    public CurrencyConversion getCurrentCurrencyConversion(final Currency currency) throws CurrencyConversionException {
        final PayeezyClientWrapper payeezyClientWrapper = payeezyConfigurationHandler.getConfigurable(null);

        final TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setType("merchant_rate");
        transactionRequest.setAmount(String.valueOf(KillBillMoney.toMinorUnits(currency.name(), new BigDecimal("1"))));
        transactionRequest.setCurrency(currency.name());

        final CurrencyConversionResponse currencyConversionResponse = payeezyClientWrapper.getDCC(transactionRequest);
        return new PayeezyCurrencyConversion(currencyConversionResponse);
    }

    @Override
    public CurrencyConversion getCurrencyConversion(final Currency baseCurrency, final DateTime dateConversion) throws CurrencyConversionException {
        return getCurrentCurrencyConversion(baseCurrency);
    }
}
