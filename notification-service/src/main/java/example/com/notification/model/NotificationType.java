package example.com.notification.model;

public enum NotificationType implements EnumeratedEntityField{
    EMAIL("EML"),
    PHONE("PHN"),
    TELEGRAM("TGM");

    private final String code;

    NotificationType(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
