package SolidPrinciples.LiskovSubstitutionPrinciple;

import java.util.Arrays;

public class Whatsapp implements SocialMedia {

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

    @Override
    public void publishPost(Object post) {
        throw new UnsupportedOperationException("WhatsApp does not behave like a feed-based post publisher here");
    }

    public static void main(String[] args) {
        SocialMedia instagram = new Instagram();
        SocialMedia whatsapp = new Whatsapp();

        instagram.publishPost("Launch photos");

        try {
            whatsapp.publishPost("Product update post");
        } catch (UnsupportedOperationException ex) {
            System.out.println("LSP violation example: " + ex.getMessage());
        }
    }
}
