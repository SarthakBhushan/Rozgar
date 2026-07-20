package com.rozgar.backend.transactions.gateway;

import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Service
public class RazorpayGatewayService {

    @Value("${razorpay.key.id:rzp_test_placeholder}")
    private String keyId;

    @Value("${razorpay.key.secret:placeholder_secret}")
    private String keySecret;


    public String createLinkedAccount(String businessName, String accountNumber, String ifsc, String accountHolder){
        try{
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject request = new JSONObject();
            request.put("email", businessName.toLowerCase().replaceAll("\\s+", "") + "@rozgar.in");
            request.put("profile", new JSONObject()
                    .put("category", "route")
                    .put("subcategory", "b2b")
                    .put("addresses", new JSONObject()
                            .put("registered", new JSONObject()
                                    .put("street1", "India")
                                    .put("city", "Mumbai")
                                    .put("state", "Maharashtra")
                                    .put("postal_code", 400001)
                                    .put("country", "IN"))));
            request.put("legal_business_name", businessName);
            request.put("legal_info", new JSONObject()
                    .put("pan", "")
                    .put("gst", ""));

            // Create account
            com.razorpay.Account account = client.account.create(request);
            String accountId = account.get("id");
            // Add bank account to the linked account
            JSONObject bankRequest = new JSONObject();
            bankRequest.put("ifsc_code", ifsc);
            bankRequest.put("beneficiary_name", accountHolder);
            bankRequest.put("account_number", accountNumber);
            client.stakeholder.create(accountId, bankRequest);

            return accountId;

        } catch (Exception e) {
            log.error("Failed to create Razorpay linked account",e);
            return null;
        }
    }
    public String createRazorpayOrder(BigDecimal amount, String currency, String receipt){
        try{
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            com.razorpay.Order order = client.orders.create(orderRequest);
            String razorpayOrderId = order.get("id");
            log.info("Razorpay order created: {}", razorpayOrderId);
            return razorpayOrderId;
        } catch (Exception e) {
            log.error("Razorpay order creation failed",e);
            throw new RuntimeException("Payment initiation failed:" + e.getMessage());
        }
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature){
        try{
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac =Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(hash);
            boolean valid = generatedSignature.equals(razorpaySignature);
            log.info("Payment signature verification: {}", valid ? "VALID" : "INVALID");
            return valid;
        }catch (NoSuchAlgorithmException | InvalidKeyException e){
            log.error("Payment signature verification failed", e);
            return false;
        }
    }

    public void transferToSeller(String paymentId, String linkedAccountId, BigDecimal amount) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject transferRequest = new JSONObject();
            transferRequest.put("account", linkedAccountId);
            transferRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            transferRequest.put("currency", "INR");
            client.payments.transfer(paymentId, transferRequest);
            log.info("Transfer of {} to {} successful", amount, linkedAccountId);
        } catch (Exception e) {
            log.error("Razorpay transfer failed", e);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }
    }
}

