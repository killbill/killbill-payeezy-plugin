killbill-payeezy-plugin
=======================

Plugin to use [Payeezy](https://www.payeezy.com/) as a gateway.

Release builds are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kill-bill.billing.plugin.java%22%20AND%20a%3A%22payeezy-plugin%22) with coordinates `org.kill-bill.billing.plugin.java:payeezy-plugin`.

Kill Bill compatibility
-----------------------

| Plugin version | Kill Bill version |
| -------------: | ----------------: |
| 0.1.y          | 0.17.z            |

Requirements
------------

The plugin needs a database. The latest version of the schema can be found [here](https://github.com/killbill/killbill-payeezy-plugin/blob/master/src/main/resources/ddl.sql).

Configuration
-------------

The following properties are required:

* `org.killbill.billing.plugin.payeezy.apiKey`: your API Key
* `org.killbill.billing.plugin.payeezy.token`: your token
* `org.killbill.billing.plugin.payeezy.paymentUrl`: Payment service url (i.e. `https://api-cert.payeezy.com` or `https://api.payeezy.com`)

The following properties are optional:

* `org.killbill.billing.plugin.payeezy.paymentConnectionTimeout`: Connection time-out in milliseconds
* `org.killbill.billing.plugin.payeezy.paymentReadTimeout`: Read time-out in milliseconds
* `org.killbill.billing.plugin.payeezy.proxyServer`: Proxy server address
* `org.killbill.billing.plugin.payeezy.proxyPort`: Proxy server port
* `org.killbill.billing.plugin.payeezy.proxyType`: Proxy server type (HTTP or SOCKS)
* `org.killbill.billing.plugin.payeezy.trustAllCertificates`: Whether to disable SSL certificates validation

These properties can be specified globally via System Properties or on a per tenant basis:

```
curl -v \
     -X POST \
     -u admin:password \
     -H 'X-Killbill-ApiKey: bob' \
     -H 'X-Killbill-ApiSecret: lazar' \
     -H 'X-Killbill-CreatedBy: admin' \
     -H 'Content-Type: text/plain' \
     -d 'org.killbill.billing.plugin.payeezy.paymentUrl=WWW
org.killbill.billing.plugin.payeezy.apiKey=XXX
org.killbill.billing.plugin.payeezy.token=YYY' \
     http://127.0.0.1:8080/1.0/kb/tenants/uploadPluginConfig/killbill-payeezy
```

Usage
-----

### Credit cards

Add a payment method:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '{
       "pluginName": "killbill-payeezy",
       "pluginInfo": {
         "properties": [
           {
             "key": "ccFirstName",
             "value": "John"
           },
           {
             "key": "ccLastName",
             "value": "Smith"
           },
           {
             "key": "ccExpirationMonth",
             "value": 10
           },
           {
             "key": "ccExpirationYear",
             "value": 2020
           },
           {
             "key": "ccNumber",
             "value": 4788250000028291
           },
           {
             "key": "ccType",
             "value": "visa"
           },
           {
             "key": "ccVerificationValue",
             "value": 123
           }
         ]
       }
     }' \
     "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/paymentMethods?isDefault=true"
```

Notes:
* Make sure to replace *ACCOUNT_ID* with the id of the Kill Bill account
* Test data is available [here](https://github.com/payeezy/testing_payeezy/blob/master/payeezy_testdata042015.pdf)

To trigger a payment:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
     -X POST \
     --data-binary '{"transactionType":"AUTHORIZE","amount":"5","currency":"USD"}' \
    "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/payments
```

Notes:
* Make sure to replace *ACCOUNT_ID* with the id of the Kill Bill account

You can verify the state of the transaction by listing the payments:

```
curl -v \
     -u admin:password \
     -H "X-Killbill-ApiKey: bob" \
     -H "X-Killbill-ApiSecret: lazar" \
     -H "Content-Type: application/json" \
     -H "X-Killbill-CreatedBy: demo" \
    "http://127.0.0.1:8080/1.0/kb/accounts/<ACCOUNT_ID>/payments?withPluginInfo=true"
```

Plugin properties
-----------------

| Key                      | Description                                   |
| -----------------------: | :-------------------------------------------- |
| ccNumber                 | Credit card number                            |
| ccType                   | Credit card brand                             |
| ccFirstName              | Credit card holder first name                 |
| ccLastName               | Credit card holder last name                  |
| ccExpirationMonth        | Credit card expiration month                  |
| ccExpirationYear         | Credit card expiration year                   |
| ccStartMonth             | Credit card start month                       |
| ccStartYear              | Credit card start year                        |
| ccVerificationValue      | CVC/CVV/CVN                                   |
| ddNumber                 | Direct Debit number                           |
| ddHolderName             | Direct Debit holder name                      |
| ddBic                    | Direct Debit bank identification code         |
| ddMandate                | Direct Debit mandate reference                |
| email                    | Purchaser email                               |
| address1                 | Billing address first line                    |
| address2                 | Billing address second line                   |
| city                     | Billing address city                          |
| zip                      | Billing address zip code                      |
| state                    | Billing address state                         |
| country                  | Billing address country                       |
| PaReq                    | 3D-Secure Pa Request                          |
| PaRes                    | 3D-Secure Pa Response                         |
| MD                       | 3D-Secure Message Digest                      |
| TermUrl                  | 3D-Secure Term URL                            |
| threeDThreshold          | Minimum amount for triggering 3D-Secure       |
| userAgent                | User-Agent for 3D-Secure Browser Info         |
| acceptHeader             | Accept-Header for 3D-Secure Browser Info      |
