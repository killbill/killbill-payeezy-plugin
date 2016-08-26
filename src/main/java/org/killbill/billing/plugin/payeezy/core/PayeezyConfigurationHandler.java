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

import java.security.GeneralSecurityException;
import java.util.Properties;

import org.killbill.billing.osgi.libs.killbill.OSGIKillbillAPI;
import org.killbill.billing.osgi.libs.killbill.OSGIKillbillLogService;
import org.killbill.billing.plugin.api.notification.PluginTenantConfigurableConfigurationHandler;
import org.killbill.billing.plugin.payeezy.client.PayeezyClientWrapper;

import com.google.common.annotations.VisibleForTesting;

public class PayeezyConfigurationHandler extends PluginTenantConfigurableConfigurationHandler<PayeezyClientWrapper> {

    private static final String PROPERTY_PREFIX = "org.killbill.billing.plugin.payeezy.";

    public PayeezyConfigurationHandler(final String pluginName,
                                       final OSGIKillbillAPI osgiKillbillAPI,
                                       final OSGIKillbillLogService osgiKillbillLogService) {
        super(pluginName, osgiKillbillAPI, osgiKillbillLogService);
    }

    @Override
    @VisibleForTesting
    public PayeezyClientWrapper createConfigurable(final Properties properties) {
        final String proxyPort = properties.getProperty(PROPERTY_PREFIX + "proxyPort");
        try {
            return new PayeezyClientWrapper(properties.getProperty(PROPERTY_PREFIX + "apiKey"),
                                            properties.getProperty(PROPERTY_PREFIX + "token"),
                                            properties.getProperty(PROPERTY_PREFIX + "secret"),
                                            properties.getProperty(PROPERTY_PREFIX + "paymentUrl"),
                                            properties.getProperty(PROPERTY_PREFIX + "proxyHost"),
                                            proxyPort == null ? null : Integer.valueOf(proxyPort),
                                            Boolean.valueOf(properties.getProperty(PROPERTY_PREFIX + "trustAllCertificates", "false")));
        } catch (final GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}

