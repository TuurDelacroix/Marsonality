package be.howest.ti.mars.logic.domain;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {

    JsonObject params = new JsonObject();
    Subscription subscription;

    @BeforeEach
    void setup() {
        params.put("endpoint", "https://updates.push.services.mozilla.com/wpush/v2/gAAAAABjiL-1MR19S73LS4x1-8o2QJnxjnjjP5e7vrMhdIJ43GF1WBabABfEHk-KrD9Vn84caYXrx2ovTF95smc3PfLRDcNDaFokK6MyX43VZVvMzI78aC9XZYQAWiUbb_HEOvkwz1gdMimRLmbxAlXqyfgsWsB_2PsITT0Lmivp_HhEFLwZL7M");
        params.put("key", "BPXkxBStMKXLpBrVMlfBgpdkM3pYkHurfB6bXNqef70F/u8L/DT3F0KvoDVtFI6LY2DZYs5GdQBIGVZk27G22uc=");
        params.put("auth", "ViNoN9a3LaMSB6y1czBKug==");

        subscription = new Subscription(params);
    }

    @Test
    void twoSubscriptionsAreEqual() {
        Subscription subscription1 = new Subscription(params);

        assertEquals(subscription, subscription1);
    }

    @Test
    void getAuth() {
        assertEquals(params.getString("auth"), subscription.getAuth());
    }

    @Test
    void getAuthAsBytes() {
        byte[] authAsBytes = Base64.getDecoder().decode(params.getString("auth"));

        assertArrayEquals(authAsBytes, subscription.getAuthAsBytes());
    }

    @Test
    void getKey() {
        assertEquals(params.getString("key"), subscription.getKey());
    }

    @Test
    void getKeyAsBytes() {
        byte[] keyAsBytes = Base64.getDecoder().decode(params.getString("key"));

        assertArrayEquals(keyAsBytes, subscription.getKeyAsBytes());
    }

    @Test
    void getEndpoint() {
        assertEquals(params.getString("endpoint"), subscription.getEndpoint());
    }

    @Test
    void gettingPrivateKeyDoesNotThrowExceptions() {
        assertDoesNotThrow(() -> {
            subscription.getUserPublicKey();
        });
    }
}