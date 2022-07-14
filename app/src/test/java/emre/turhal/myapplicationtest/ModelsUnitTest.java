package emre.turhal.myapplicationtest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import emre.turhal.myapplicationtest.models.Booking;
import emre.turhal.myapplicationtest.models.Workmate;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ModelsUnitTest {
    private Workmate workmate;
    private Booking booking;

    @Before
    public void setUp() {
        workmate = new Workmate("photoURL", "esteban", "123456789");
        booking = new Booking("rialto", "000000", "123456789", "14-07-22");
    }

    @Test
    public void testGetWorkmatesData() {
        assertEquals("123456789", workmate.getUid());
        assertEquals("esteban", workmate.getName());
        assertEquals("photoURL", workmate.getUrlPicture());
        assertFalse(workmate.isNotification());


    }

    @Test
    public void testGetBookingData() {
        assertEquals("14-07-22", booking.getBookingDate());
        assertEquals("123456789", booking.getWorkmateUid());
        assertEquals("000000", booking.getRestaurantId());
        assertEquals("rialto", booking.getRestaurantName());
    }

    @Test
    public void testSetBookingData() {

        booking.setBookingDate("11-11-11");
        booking.setRestaurantId("123456");
        booking.setRestaurantName("la grange");
        booking.setWorkmateUid("111111");

        assertEquals("11-11-11", booking.getBookingDate());
        assertEquals("111111", booking.getWorkmateUid());
        assertEquals("123456", booking.getRestaurantId());
        assertEquals("la grange", booking.getRestaurantName());

    }

    @Test
    public void testSetWorkmateData() {
        workmate.setName("pascal");
        workmate.setUid("666666");
        workmate.setUrlPicture("photo.com");
        workmate.setNotification(true);

        assertEquals("pascal", workmate.getName());
        assertEquals("666666", workmate.getUid());
        assertEquals("photo.com", workmate.getUrlPicture());
        assertTrue(workmate.isNotification());

    }
}