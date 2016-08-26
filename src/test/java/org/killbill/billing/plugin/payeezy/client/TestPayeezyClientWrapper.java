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

import java.util.UUID;

import org.killbill.billing.plugin.payeezy.TestRemoteBase;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.firstdata.payeezy.models.transaction.Card;
import com.firstdata.payeezy.models.transaction.PaymentMethod;
import com.firstdata.payeezy.models.transaction.TransactionRequest;
import com.firstdata.payeezy.models.transaction.TransactionResponse;
import com.firstdata.payeezy.models.transaction.TransactionType;

public class TestPayeezyClientWrapper extends TestRemoteBase {

    @Test(groups = "slow")
    public void testAuthorizeAndCapture() throws Exception {
        final Card card = new Card();
        card.setName("Not Provided");
        card.setType(CC_TYPE);
        card.setCvv(CC_VERIFICATION_VALUE);
        card.setExpiryDt(CC_EXPIRATION_DATE);
        card.setNumber(CC_NUMBER);

        final TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("100");
        transactionRequest.setTransactionType(TransactionType.AUTHORIZE.name().toLowerCase());
        transactionRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD.getValue());
        transactionRequest.setReferenceNo(UUID.randomUUID().toString());
        transactionRequest.setCurrency(DEFAULT_CURRENCY.toString());
        transactionRequest.setCard(card);

        final TransactionResponse authResponse = payeezyClientWrapper.triggerInitialTransaction(transactionRequest);
        Assert.assertEquals(authResponse.getTransactionStatus(), "approved");
        Assert.assertEquals(authResponse.getValidationStatus(), "success");
        Assert.assertEquals(authResponse.getTransactionType(), "authorize");
        Assert.assertNotNull(authResponse.getTransactionId());
        Assert.assertNotNull(authResponse.getTransactionTag());
        Assert.assertNotNull(authResponse.getCard());
        Assert.assertEquals(authResponse.getCard().getType(), "visa");
        Assert.assertEquals(authResponse.getMethod(), "credit_card");
        Assert.assertEquals(authResponse.getAmount(), transactionRequest.getAmount());
        Assert.assertEquals(authResponse.getCurrency(), transactionRequest.getCurrency());

        // Capture the credit card authorization
        final TransactionRequest captureRequest = new TransactionRequest();
        captureRequest.setTransactionTag(authResponse.getTransactionTag());
        captureRequest.setTransactionType(TransactionType.CAPTURE.name().toLowerCase());
        captureRequest.setPaymentMethod(transactionRequest.getPaymentMethod());
        captureRequest.setAmount(transactionRequest.getAmount());
        captureRequest.setCurrency(transactionRequest.getCurrency());

        final TransactionResponse captureResponse = payeezyClientWrapper.triggerFollowOnTransaction(authResponse.getTransactionId(), captureRequest);
        Assert.assertEquals(captureResponse.getTransactionStatus(), "approved");
        Assert.assertEquals(captureResponse.getValidationStatus(), "success");
        Assert.assertEquals(captureResponse.getTransactionType(), "capture");
        Assert.assertNotNull(captureResponse.getTransactionId());
        Assert.assertNotNull(captureResponse.getTransactionTag());
        Assert.assertNull(captureResponse.getCard());
        Assert.assertEquals(captureResponse.getMethod(), "credit_card");
        Assert.assertEquals(captureResponse.getAmount(), transactionRequest.getAmount());
        Assert.assertEquals(captureResponse.getCurrency(), transactionRequest.getCurrency());
    }
}
