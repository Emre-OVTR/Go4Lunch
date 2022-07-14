package emre.turhal.myapplicationtest.models;

import javax.annotation.Nullable;

public class Workmate {

    private String name;
    private String urlPicture;
    private String uid;
    private boolean notification;

    public Workmate(){
    }

    public Workmate(String urlPicture, String name, String uid) {
        this.name = name;
        this.urlPicture = urlPicture;
        this.uid = uid;
        this.notification = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}


