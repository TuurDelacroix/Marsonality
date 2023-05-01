package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.MarsPushException;
import io.vertx.core.json.JsonObject;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

/**
 * DefaultMarsController is the default implementation for the MarsController interface.
 * The controller shouldn't even know that it is used in an API context..
 *
 * This class and all other classes in the logic-package (or future sub-packages)
 * should use 100% plain old Java Objects (POJOs). The use of Json, JsonObject or
 * Strings that contain encoded/json data should be avoided here.
 * Keep libraries and frameworks out of the logic packages as much as possible.
 * Do not be afraid to create your own Java classes if needed.
 */
public class DefaultMarsController implements MarsController {
    private final List<Subscription> subscriptions;

    private final PushService ps;

    public DefaultMarsController() {
        subscriptions = new ArrayList<>();
        ps = new PushService();
        try{
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            ps.setSubject("mailto:jean-baptiste.van.parys@student.howest.be");
            ps.setPrivateKey("XW3GeXCSYwRKd9x7GGRm-U5NVE7W8QUR2nW8nkXZqqY=");
            ps.setPublicKey("BEm8_ELP1kwa1OyBj_kQ22fjGQ3ZkJIjIFlD-Ton056lVo_jz-lIfGsuV3CvUi1UN1AXkfDPNLEqHvPp77a2s_c=");
        } catch (Exception e) {
            throw new MarsPushException("Failed to Create Push service",e);
        }

    }

    @Override
    public void postSubscription(Subscription sub) {
       //find a beter way to store subscription
        if (!subscriptions.contains(sub)) {
            subscriptions.add(sub);
        }
    }

    @Override
    public void postNotification(JsonObject notification) {
        subscriptions.forEach(sub -> sendNotification(sub, notification));
        Repositories.getH2Repo().insertNotification(notification);
    }

    private void sendNotification(Subscription subscription, JsonObject payload) {
        try {
            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getUserPublicKey(),
                    subscription.getAuthAsBytes(),
                    payload.toString().getBytes()
            );
                    ps.send(notification);
        } catch (JoseException | ExecutionException | IOException | GeneralSecurityException | InterruptedException e) { //NOSONAR
            throw new MarsPushException("push notification failed", e);
        }
    }

    public List<Martian> getMartians() {
        return Repositories.getH2Repo().getMartians();
    }

    @Override
    public Martian getMartian(int marsId) {
        return Repositories.getH2Repo().getMartian(marsId);
    }

    @Override
    public List<Profile> getProfiles() {
        return Repositories.getH2Repo().getProfiles();
    }

    @Override
    public List<Profile> getProfilesOfMartian(int marsId) {
        return Repositories.getH2Repo().getProfilesOfMartian(marsId);
    }

    @Override
    public Profile getProfileOfMartian(int marsId, String profileName) {
        return Repositories.getH2Repo().getProfileOfMartian(marsId, profileName);
    }

    @Override
    public int getNextProfileSlot(List<Profile> profilesOfMarsId) {
        return profilesOfMarsId.size();
    }

    @Override
    public String calculatePriceOfProfile(int profileSlot) {
        return String.valueOf((int) (Math.pow(2, profileSlot) * 100));
    }

    @Override
    public Profile unlockProfile(int marsId, String profileName, String price) {
        return new Profile(profileName, marsId, price);
    }

    @Override
    public void storeNewProfile(Profile newProfile) {
        Repositories.getH2Repo().storeProfileOfMartian(newProfile);
    }

    @Override
    public Profile getDefaultProfileOfMartian(int marsId) {
        return Repositories.getH2Repo().getDefaultProfileOfMartian(marsId);
    }

    @Override
    public Profile getActiveProfileOfMartian(int marsId) {
        return Repositories.getH2Repo().getActiveProfileOfMartian(marsId);
    }

    @Override
    public List<Trait> getTraits() {
        return Repositories.getH2Repo().getTraits();
    }

    @Override
    public List<Trait> getTraits(String traitType) {
        return Repositories.getH2Repo().getTraits(traitType);
    }

    @Override
    public List<Trait> getTraitsOfMartiansProfile(int marsId, String profileName) {
        return Repositories.getH2Repo().getTraitsOfMartiansProfile(marsId, profileName);
    }

    @Override
    public Trait getTrait(String traitName) {
        return Repositories.getH2Repo().getTrait(traitName);
    }

    @Override
    public Trait addTraitToProfile(int marsId, String profileName, String traitName) {
        return Repositories.getH2Repo().addTraitToProfile(marsId, profileName, traitName);
    }

    @Override
    public Trait removeTraitFromProfile(int marsId, String profileName, String traitName) {
        return Repositories.getH2Repo().removeTraitFromProfile(marsId, profileName, traitName);
    }

    @Override
    public void setTraitsOfMartians() {
        Repositories.getH2Repo().setTraitsOfMartians();
    }

    @Override
    public List<Chip> getChips() {
        return Repositories.getH2Repo().getChips();
    }

    @Override
    public List<Trait> getTraitsOfMartian(int marsId) {
        return Repositories.getH2Repo().getTraitsOfMartian(marsId);
    }

    @Override
    public Trait buyTrait(int marsId, String traitName) {
        return Repositories.getH2Repo().buyTrait(marsId, traitName);
    }

    @Override
    public List<Trait> getTraitsOfMartianByTraitType(int marsId, String traitType) {
        return Repositories.getH2Repo().getTraitsOfMartianByTraitType(marsId, traitType);
    }

    @Override
    public List<JsonObject> getNotifications() {
        return Repositories.getH2Repo().getNotifications();
    }

    @Override
    public Map<String, Integer> getProfilesUsage(List<Profile> profiles) {
        Map<String, Integer> profileUsages = new HashMap<>();
        profiles.forEach(profile -> profileUsages.put(profile.getName(), profile.getUsages()));
        return profileUsages;
    }

    @Override
    public Profile activateProfile(int marsId, String profileName) {
        return Repositories.getH2Repo().activateProfile(marsId, profileName);
    }
}
