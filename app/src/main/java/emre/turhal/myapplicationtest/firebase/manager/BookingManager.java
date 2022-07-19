package emre.turhal.myapplicationtest.firebase.manager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import emre.turhal.myapplicationtest.firebase.repository.BookingRepository;

public class BookingManager {

    private static volatile BookingManager instance;
    private static BookingRepository bookingRepository;

    public BookingManager() {
        bookingRepository = BookingRepository.getInstance();
    }

    public static BookingManager getInstance() {
        BookingManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(BookingRepository.class) {
            if (instance == null) {
                instance = new BookingManager();
            }
            return instance;
        }
    }

    public static Task<DocumentReference> createBooking(String restaurantName, String restaurantPlaceId, String workmateUid, String bookingDate){
       return bookingRepository.createBooking(restaurantName, restaurantPlaceId, workmateUid, bookingDate);
    }

    public  Task<QuerySnapshot> getBooking(String workmateUid, String bookingDate ){
         return bookingRepository.getBooking(workmateUid, bookingDate);
    }

    public static Task<QuerySnapshot> getTodayBooking(String restaurantPlaceId, String bookingDate){
       return bookingRepository.getTodayBooking(restaurantPlaceId, bookingDate);
    }

    public void deleteBooking(String bookingId){
          bookingRepository.deleteBooking(bookingId);
    }
}
