package SolidPrinciples.LiskovSubstitutionPrinciple;

import java.util.Arrays;

public class Facebook implements SocialMedia {

    @Override
    public void chatWithFriend() {
        System.out.println("Facebook chat is available");
    }

    @Override
    public void sendPhotosAndVideos() {
        System.out.println("Facebook can send photos and videos");
    }

    @Override
    public void groupVideoCall(String... users) {
        System.out.println("Facebook group video call with " + Arrays.toString(users));
    }

    @Override
    public void publishPost(Object post) {
        System.out.println("Facebook published post: " + post);
    }
}
