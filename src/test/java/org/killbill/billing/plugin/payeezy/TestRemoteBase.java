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

package org.killbill.billing.plugin.payeezy;

import java.util.Properties;
import java.util.UUID;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.osgi.libs.killbill.OSGIConfigPropertiesService;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.plugin.TestUtils;
import org.killbill.billing.plugin.TestWithEmbeddedDBBase;
import org.killbill.billing.plugin.payeezy.api.PayeezyCurrencyConversionPluginApi;
import org.killbill.billing.plugin.payeezy.api.PayeezyPaymentPluginApi;
import org.killbill.billing.plugin.payeezy.client.PayeezyClientWrapper;
import org.killbill.billing.plugin.payeezy.core.PayeezyActivator;
import org.killbill.billing.plugin.payeezy.core.PayeezyConfigurationHandler;
import org.killbill.billing.plugin.payeezy.dao.PayeezyDao;
import org.killbill.billing.util.callcontext.CallContext;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;

public abstract class TestRemoteBase extends TestWithEmbeddedDBBase {

    public static final String DEFAULT_COUNTRY = "US";
    public static final Currency DEFAULT_CURRENCY = Currency.USD;
    public static final String CC_NUMBER = "4012000033330026";
    public static final String CC_EXPIRATION_DATE = "1020";
    public static final String CC_EXPIRATION_MONTH = "10";
    public static final String CC_EXPIRATION_YEAR = "2020";
    public static final String CC_VERIFICATION_VALUE = "737";
    public static final String CC_TYPE = "visa";

    // To run these tests, you need a properties file in the classpath (e.g. src/test/resources/payeezy.properties)
    // See README.md for details on the required properties
    private static final String PROPERTIES_FILE_NAME = "payeezy.properties";

    protected CallContext context;
    protected Account account;
    protected PayeezyPaymentPluginApi payeezyPaymentPluginApi;
    protected PayeezyCurrencyConversionPluginApi payeezyCurrencyConversionPluginApi;
    protected OSGIKillbillAPI killbillApi;
    protected PayeezyConfigurationHandler payeezyConfigurationHandler;
    protected PayeezyClientWrapper payeezyClientWrapper;
    protected PayeezyDao payeezyDao;

    @BeforeClass(groups = "slow")
    public void setUpBeforeClass() throws Exception {
        super.setUpBeforeClass();

        final Clock clock = new DefaultClock();

        context = Mockito.mock(CallContext.class);
        Mockito.when(context.getTenantId()).thenReturn(UUID.randomUUID());

        account = TestUtils.buildAccount(DEFAULT_CURRENCY, DEFAULT_COUNTRY);
        killbillApi = TestUtils.buildOSGIKillbillAPI(account);

        TestUtils.buildPaymentMethod(account.getId(), account.getPaymentMethodId(), PayeezyActivator.PLUGIN_NAME, killbillApi);

        payeezyDao = new PayeezyDao(embeddedDB.getDataSource());

        final OSGIKillbillLogService logService = TestUtils.buildLogService();
        payeezyConfigurationHandler = new PayeezyConfigurationHandler(PayeezyActivator.PLUGIN_NAME, killbillApi, logService);

        final Properties properties = TestUtils.loadProperties(PROPERTIES_FILE_NAME);
        payeezyClientWrapper = payeezyConfigurationHandler.createConfigurable(properties);

        payeezyConfigurationHandler.setDefaultConfigurable(payeezyClientWrapper);

        final OSGIConfigPropertiesService configPropertiesService = Mockito.mock(OSGIConfigPropertiesService.class);
        payeezyPaymentPluginApi = new PayeezyPaymentPluginApi(payeezyConfigurationHandler, killbillApi, configPropertiesService, logService, clock, payeezyDao);
        payeezyCurrencyConversionPluginApi = new PayeezyCurrencyConversionPluginApi(payeezyConfigurationHandler);
    }
}
