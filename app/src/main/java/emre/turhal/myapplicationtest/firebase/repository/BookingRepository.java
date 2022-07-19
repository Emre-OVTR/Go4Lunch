package emre.turhal.myapplicationtest.firebase.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import emre.turhal.myapplicationtest.models.Booking;

public final class BookingRepository {

    private static volatile BookingRepository instance;
    private static final String COLLECTION_BOOKING = "booking";

    public static CollectionReference getBookingCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_BOOKING);
    }

    public static BookingRepository getInstance() {
        BookingRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (BookingRepository.class) {
            if (instance == null) {
                instance = new BookingRepository();
            }
            return instance;
        }
    }

    public  Task<DocumentReference> createBooking(String restaurantName, String restaurantPlaceId, String workmateUid, String bookingDate) {
        Booking bookingToCreate = new Booking(restaurantName, restaurantPlaceId, workmateUid, bookingDate);
        return BookingRepository.getBookingCollection().add(bookingToCreate);
    }

    public  Task<QuerySnapshot> getBooking(String userId, String bookingDate) {
        return BookingRepository.getBookingCollection().whereEqualTo("workmateUid", userId).whereEqualTo("bookingDate", bookingDate).get();
    }

    public  Task<QuerySnapshot> getTodayBooking(String restaurantPlaceId, String bookingDate) {
        return BookingRepository.getBookingCollection().whereEqualTo("restaurantId", restaurantPlaceId).whereEqualTo("bookingDate", bookingDate).get();
    }

    public static Task<Void> deleteBooking(String bookingId) {
        return BookingRepository.getBookingCollection().document(bookingId).delete();
    }
}
