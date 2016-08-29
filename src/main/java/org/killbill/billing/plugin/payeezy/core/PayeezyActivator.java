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

package org.killbill.billing.plugin.payeezy.core;

import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.osgi.api.OSGIPluginProperties;
import org.killbill.billing.osgi.libs.killbill.KillbillActivatorBase;
import org.killbill.billing.payment.plugin.api.PaymentPluginApi;
import org.killbill.billing.plugin.api.notification.PluginConfigurationEventHandler;
import org.killbill.billing.plugin.payeezy.api.PayeezyCurrencyConversionPluginApi;
import org.killbill.billing.plugin.payeezy.api.PayeezyPaymentPluginApi;
import org.killbill.billing.plugin.payeezy.client.PayeezyClientWrapper;
import org.killbill.billing.plugin.payeezy.dao.PayeezyDao;
import org.killbill.clock.Clock;
import org.killbill.clock.DefaultClock;
import org.osgi.framework.BundleContext;

public class PayeezyActivator extends KillbillActivatorBase {

    public static final String PLUGIN_NAME = "killbill-payeezy";

    private PayeezyConfigurationHandler payeezyConfigurationHandler;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        final Clock clock = new DefaultClock();
        final PayeezyDao dao = new PayeezyDao(dataSource.getDataSource());

        // Register the servlet
        final PayeezyServlet payeezyServlet = new PayeezyServlet();
        registerServlet(context, payeezyServlet);

        payeezyConfigurationHandler = new PayeezyConfigurationHandler(PLUGIN_NAME, killbillAPI, logService);

        final PayeezyClientWrapper globalPayeezyClient = payeezyConfigurationHandler.createConfigurable(configProperties.getProperties());
        payeezyConfigurationHandler.setDefaultConfigurable(globalPayeezyClient);

        // Register the payment plugin
        final PaymentPluginApi paymentPluginApi = new PayeezyPaymentPluginApi(payeezyConfigurationHandler, killbillAPI, configProperties, logService, clock, dao);
        registerPaymentPluginApi(context, paymentPluginApi);

        // Register the currency conversion plugin
        final CurrencyConversionApi currencyConversionApi = new PayeezyCurrencyConversionPluginApi(payeezyConfigurationHandler);
        registerCurrencyConversionApi(context, currencyConversionApi);

        registerHandlers();
    }

    public void registerHandlers() {
        final PluginConfigurationEventHandler handler = new PluginConfigurationEventHandler(payeezyConfigurationHandler);
        dispatcher.registerEventHandlers(handler);
    }

    private void registerServlet(final BundleContext context, final HttpServlet servlet) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, Servlet.class, servlet, props);
    }

    private void registerPaymentPluginApi(final BundleContext context, final PaymentPluginApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, PaymentPluginApi.class, api, props);
    }

    private void registerCurrencyConversionApi(final BundleContext context, final CurrencyConversionApi api) {
        final Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(OSGIPluginProperties.PLUGIN_NAME_PROP, PLUGIN_NAME);
        registrar.registerService(context, CurrencyConversionApi.class, api, props);
    }
}
