package emre.turhal.myapplicationtest.models;

public class Booking {

    private String restaurantName;
    private String restaurantId;
    private String workmateUid;
    private String bookingDate;

    public Booking(){

    }

    public Booking(String restaurantName, String restaurantPlaceId, String workmateUid, String bookingDate) {
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantPlaceId;
        this.workmateUid = workmateUid;
        this.bookingDate = bookingDate;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getWorkmateUid() {
        return workmateUid;
    }

    public void setWorkmateUid(String workmateUid) {
        this.workmateUid = workmateUid;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
