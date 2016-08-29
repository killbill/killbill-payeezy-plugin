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
import org.joda.time.DateTimeZone;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.currency.api.CurrencyConversion;
import org.killbill.billing.currency.api.Rate;

import com.firstdata.payeezy.models.transaction.CurrencyConversionResponse;
import com.google.common.collect.ImmutableSet;

public class PayeezyCurrencyConversion implements CurrencyConversion {

    private final CurrencyConversionResponse currencyConversionResponse;

    public PayeezyCurrencyConversion(final CurrencyConversionResponse currencyConversionResponse) {
        this.currencyConversionResponse = currencyConversionResponse;
    }

    @Override
    public Currency getBaseCurrency() {
        final String foreignCurrency = currencyConversionResponse.getForeignCurrency();
        return foreignCurrency == null ? null : Currency.valueOf(foreignCurrency);
    }

    @Override
    public Set<Rate> getRates() {
        final Rate rate = new PayeezyRate();
        return ImmutableSet.<Rate>of(rate);
    }

    private final class PayeezyRate implements Rate {

        @Override
        public Currency getBaseCurrency() {
            final String foreignCurrency = currencyConversionResponse.getForeignCurrency();
            return foreignCurrency == null ? null : Currency.valueOf(foreignCurrency);
        }

        @Override
        public Currency getCurrency() {
            // Merchant account currency
            return null;
        }

        @Override
        public BigDecimal getValue() {
            return new BigDecimal(currencyConversionResponse.getAmount());
        }

        @Override
        public DateTime getConversionDate() {
            return new DateTime(currencyConversionResponse.getSourceTimestamp(), DateTimeZone.UTC);
        }
    }
}
