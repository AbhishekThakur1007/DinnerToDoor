package com.example.food.UserSide.UserPayment;

import android.app.Activity;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PaymentsUtil {

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    private static JSONObject getTokenizationSpec() throws JSONException {

        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", " PAYMENT_GATEWAY ");

        JSONObject parameters = new JSONObject();
        parameters.put("gateway","example").
        put("gatewayMerchantId","5640-7437-6970");

        tokenizationSpecification.put("parameters",parameters);
        return tokenizationSpecification;
    }

    // 3 Step //Supported cards
    private static JSONArray getAllowedCardNetworks() {

        return new JSONArray()
                .put("VISA")
                .put("MASTERCARD");
    }
    // Secure
    private static JSONArray getAllowedCardAuthMethods(){
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    // 4 = (Step 3 + step 2)  Step  My app payment method accepted
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    //Get Token info (Step 4+ Step 1)
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getTokenizationSpec());
        return cardPaymentMethod;
    }

    //Step 7: Create a PaymentDataRequest object
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", "India");
        transactionInfo.put("currencyCode", "INR");
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }
    //Merchant Details
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Indo-Chinese Food");
    }

    //Finally get all data (Step 4 + Step 6 + Step 7 + Step 1)
    public static Optional<JSONObject> getPaymentDataRequest(Integer price) {

        final String Price = Integer.toString(price);

        try {
            JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo",PaymentsUtil.getTransactionInfo(Price));
            paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());
            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            return Optional.empty();
        }
    }

}

