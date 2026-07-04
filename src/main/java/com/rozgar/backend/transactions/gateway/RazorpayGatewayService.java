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
}

