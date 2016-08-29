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

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.currency.api.CurrencyConversion;
import org.killbill.billing.currency.api.CurrencyConversionException;
import org.killbill.billing.currency.api.Rate;
import org.killbill.billing.plugin.payeezy.TestRemoteBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPayeezyCurrencyConversionPluginApi extends TestRemoteBase {

    @Test(groups = "slow", enabled = false, description = "Default US sandbox doesn't support DCC")
    public void testCurrencyConversion() throws Exception {
        // Assumes merchant currency is USD
        doDCC(Currency.EUR, 1.12, 0.1);
        doDCC(Currency.GBP, 1.31, 0.1);
    }

    private void doDCC(final Currency baseCurrency, final double expectedValue, final double delta) throws CurrencyConversionException {
        final CurrencyConversion currencyConversion = payeezyCurrencyConversionPluginApi.getCurrentCurrencyConversion(baseCurrency);

        Assert.assertEquals(currencyConversion.getBaseCurrency(), baseCurrency);
        Assert.assertEquals(currencyConversion.getRates().size(), 1);

        final Rate rate = currencyConversion.getRates().iterator().next();
        Assert.assertEquals(rate.getBaseCurrency(), baseCurrency);
        Assert.assertEquals(rate.getValue().doubleValue(), expectedValue, delta);
    }
}
