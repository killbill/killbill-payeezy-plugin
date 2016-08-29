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

package org.killbill.billing.plugin.payeezy.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.killbill.billing.plugin.util.http.HttpClient;
import org.killbill.billing.plugin.util.http.InvalidRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firstdata.payeezy.api.APIResourceConstants;
import com.firstdata.payeezy.models.transaction.CurrencyConversionResponse;
import com.firstdata.payeezy.models.transaction.TransactionRequest;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.google.common.collect.ImmutableMap;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class PayeezyClientWrapper extends HttpClient {

    public static final String EXCEPTION_CLASS = "exceptionClass";
    public static final String EXCEPTION_MESSAGE = "exceptionMessage";
    public static final String PAYEEZY_MESSAGE = "payeezyMessage";

    private static final Logger logger = LoggerFactory.getLogger(PayeezyClientWrapper.class);
    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String SHA1_PRNG = "SHA1PRNG";

    private final String apiKey;
    private final String token;
    private final String secret;

    public PayeezyClientWrapper(final String apiKey,
                                final String token,
                                final String secret,
                                final String url,
                                final String proxyHost,
                                final Integer proxyPort,
                                final Boolean strictSSL) throws GeneralSecurityException {
        super(url, null, null, proxyHost, proxyPort, strictSSL);
        this.apiKey = apiKey;
        this.token = token;
        this.secret = secret;
    }

    public TransactionResponse triggerInitialTransaction(final TransactionRequest transactionRequest) {
        try {
            return doCall("POST",
                          APIResourceConstants.PRIMARY_TRANSACTIONS,
                          mapper.writeValueAsString(transactionRequest),
                          ImmutableMap.<String, String>of(),
                          TransactionResponse.class);
        } catch (final InterruptedException e) {
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final ExecutionException e) {
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final TimeoutException e) {
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final IOException e) {
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final URISyntaxException e) {
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final InvalidRequest e) {
            String body;
            try {
                body = e.getResponse() != null ? e.getResponse().getResponseBody() : null;
            } catch (final IOException ignored) {
                body = null;
            }
            logger.warn("Unable to trigger initial transaction for transactionRequest='{}', body='{}'", transactionRequest, body, e);
            return toTransactionResponse(e, body);
        }
    }

    public TransactionResponse triggerFollowOnTransaction(final String id, final TransactionRequest transactionRequest) {
        try {
            return doCall("POST",
                          APIResourceConstants.PRIMARY_TRANSACTIONS + "/" + id,
                          mapper.writeValueAsString(transactionRequest),
                          ImmutableMap.<String, String>of(),
                          TransactionResponse.class);
        } catch (final InterruptedException e) {
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final ExecutionException e) {
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final TimeoutException e) {
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final IOException e) {
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final URISyntaxException e) {
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}'", transactionRequest, e);
            return toTransactionResponse(e);
        } catch (final InvalidRequest e) {
            String body;
            try {
                body = e.getResponse() != null ? e.getResponse().getResponseBody() : null;
            } catch (final IOException ignored) {
                body = null;
            }
            logger.warn("Unable to trigger follow-on transaction for transactionRequest='{}', body='{}'", transactionRequest, body, e);
            return toTransactionResponse(e, body);
        }
    }

    public CurrencyConversionResponse getDCC(final TransactionRequest transactionRequest) {
        try {
            return doCall("POST",
                          APIResourceConstants.EXCHANGE_RATE,
                          mapper.writeValueAsString(transactionRequest),
                          ImmutableMap.<String, String>of(),
                          CurrencyConversionResponse.class);
        } catch (final InterruptedException e) {
            logger.warn("Unable to get DCC for transactionRequest='{}'", transactionRequest, e);
            return toCurrencyConversionResponse(e);
        } catch (final ExecutionException e) {
            logger.warn("Unable to get DCC for transactionRequest='{}'", transactionRequest, e);
            return toCurrencyConversionResponse(e);
        } catch (final TimeoutException e) {
            logger.warn("Unable to get DCC for transactionRequest='{}'", transactionRequest, e);
            return toCurrencyConversionResponse(e);
        } catch (final IOException e) {
            logger.warn("Unable to get DCC for transactionRequest='{}'", transactionRequest, e);
            return toCurrencyConversionResponse(e);
        } catch (final URISyntaxException e) {
            logger.warn("Unable to get DCC for transactionRequest='{}'", transactionRequest, e);
            return toCurrencyConversionResponse(e);
        } catch (final InvalidRequest e) {
            String body;
            try {
                body = e.getResponse() != null ? e.getResponse().getResponseBody() : null;
            } catch (final IOException ignored) {
                body = null;
            }
            logger.warn("Unable to get DCC for transactionRequest='{}', body='{}'", transactionRequest, body, e);
            return toCurrencyConversionResponse(e, body);
        }
    }

    @Override
    protected <T> T doCall(final String verb, final String uri, final String body, final Map<String, String> options, final Class<T> clazz) throws InterruptedException, ExecutionException, TimeoutException, IOException, URISyntaxException, InvalidRequest {
        final String url = String.format("%s%s", this.url, uri);

        final AsyncHttpClient.BoundRequestBuilder builder = getBuilderWithHeaderAndQuery(verb, url, options);
        if (!GET.equals(verb) && !HEAD.equals(verb)) {
            if (body != null) {
                logger.info("Payeezy request: {}", body);
                builder.setBody(body);
            }
        }

        setHeaders(body, builder);

        return executeAndWait(builder, DEFAULT_HTTP_TIMEOUT_SEC, clazz);
    }

    @Override
    protected <T> T deserializeResponse(final Response response, final Class<T> clazz) throws IOException {
        final String responseBody = response.getResponseBody();
        logger.info("Payeezy response: {}", responseBody);
        return mapper.readValue(responseBody, clazz);
    }

    private void setHeaders(final String payload, final AsyncHttpClient.BoundRequestBuilder builder) {
        builder.addHeader(APIResourceConstants.SecurityConstants.APIKEY, apiKey);
        builder.addHeader(APIResourceConstants.SecurityConstants.TOKEN, token);
        builder.addHeader(APIResourceConstants.SecurityConstants.APISECRET, secret);

        builder.addHeader("User-Agent", "KillBill 1.0");
        builder.addHeader("Content-Type", "application/json");

        final long nonce;
        try {
            final SecureRandom sha1PRNG = SecureRandom.getInstance(SHA1_PRNG);
            nonce = Math.abs(sha1PRNG.nextLong());
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        final String nonceString = Long.toString(nonce);
        builder.addHeader(APIResourceConstants.SecurityConstants.NONCE, nonceString);

        final String timestamp = Long.toString(System.currentTimeMillis());
        builder.addHeader(APIResourceConstants.SecurityConstants.TIMESTAMP, timestamp);

        try {
            builder.addHeader(APIResourceConstants.SecurityConstants.AUTHORIZE, getMacValue(nonceString, timestamp, payload));
        } catch (final Exception e) {
            logger.warn("Unable to compute HMAC header", e);
        }
    }

    private String getMacValue(final String nonce, final String timeStamp, @Nullable final String payload) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        final Key secretKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA_256);
        mac.init(secretKey);

        final StringBuilder buff = new StringBuilder();
        buff.append(apiKey)
            .append(nonce)
            .append(timeStamp);
        if (token != null) {
            buff.append(token);
        }
        if (payload != null) {
            buff.append(payload);
        }

        final String bufferData = buff.toString();
        final byte[] macHash = mac.doFinal(bufferData.getBytes("UTF-8"));
        return new String(Base64.encodeBase64(toHex(macHash)));
    }

    private byte[] toHex(final byte[] arr) {
        final String hex = byteArrayToHex(arr);
        return hex.getBytes();
    }

    private String byteArrayToHex(final byte[] a) {
        final StringBuilder sb = new StringBuilder(a.length * 2);
        for (final byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private TransactionResponse toTransactionResponse(final Throwable e) {
        return toTransactionResponse(e, null);
    }

    private TransactionResponse toTransactionResponse(final Throwable e, @Nullable final String body) {
        final TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setExactMessage(getErrorMessage(e, body));
        return transactionResponse;
    }

    private CurrencyConversionResponse toCurrencyConversionResponse(final Throwable e) {
        return toCurrencyConversionResponse(e, null);
    }

    private CurrencyConversionResponse toCurrencyConversionResponse(final Throwable e, @Nullable final String body) {
        final CurrencyConversionResponse currencyConversionResponse = new CurrencyConversionResponse();
        currencyConversionResponse.setStatus(getErrorMessage(e, body));
        return currencyConversionResponse;
    }

    private String getErrorMessage(final Throwable e, @Nullable final String body) {
        final Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put(EXCEPTION_CLASS, e.getClass().getCanonicalName());
        bodyMap.put(EXCEPTION_MESSAGE, e.getMessage());
        if (body != null) {
            bodyMap.put(PAYEEZY_MESSAGE, body);
        }

        String messageJSON;
        try {
            messageJSON = mapper.writeValueAsString(bodyMap);
        } catch (final JsonProcessingException ignored) {
            messageJSON = null;
        }
        return messageJSON;
    }
}
