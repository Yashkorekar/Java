package SolidPrinciples.LiskovSubstitutionPrinciple;

public interface SocialMedia {

    void chatWithFriend();

    void sendPhotosAndVideos();

    void groupVideoCall(String... users);

    void publishPost(Object post);
}
