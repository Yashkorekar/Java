package SolidPrinciples.LiskovSubstitutionPrinciple.solution;

import java.util.Arrays;

public class Instagram implements SocialMedia, PostMediaManager, SocialVideoCallManager {

    @Override
    public void chatWithFriend() {
        System.out.println("Instagram chat is available");
    }

    @Override
    public void sendPhotosAndVideos() {
        System.out.println("Instagram can send photos and videos");
    }

    @Override
    public void publishPost(Object post) {
        System.out.println("Instagram published post: " + post);
    }

    @Override
    public void groupVideoCall(String... users) {
        System.out.println("Instagram group video call with " + Arrays.toString(users));
    }
}
