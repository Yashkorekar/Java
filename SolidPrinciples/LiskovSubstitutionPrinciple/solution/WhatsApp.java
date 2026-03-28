package SolidPrinciples.LiskovSubstitutionPrinciple.solution;

import java.util.Arrays;

public class WhatsApp implements SocialMedia, SocialVideoCallManager {

    @Override
    public void chatWithFriend() {
        System.out.println("WhatsApp chat is available");
    }

    @Override
    public void sendPhotosAndVideos() {
        System.out.println("WhatsApp can send photos and videos");
    }

    @Override
    public void groupVideoCall(String... users) {
        System.out.println("WhatsApp group video call with " + Arrays.toString(users));
    }
}
