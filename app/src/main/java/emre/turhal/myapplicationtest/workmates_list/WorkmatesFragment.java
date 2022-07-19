package emre.turhal.myapplicationtest.workmates_list;

import static com.firebase.ui.auth.ui.email.EmailLinkFragment.TAG;

import static emre.turhal.myapplicationtest.utils.GetTodayDate.getTodayDate;
import static emre.turhal.myapplicationtest.utils.ShowToastSnack.showToast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import emre.turhal.myapplicationtest.BaseFragment;
import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.manager.BookingManager;
import emre.turhal.myapplicationtest.manager.UserManager;
import emre.turhal.myapplicationtest.models.Workmate;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.restaurant_details.DetailsActivity;
import emre.turhal.myapplicationtest.utils.ItemClickSupport;


public class WorkmatesFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private List<Workmate> mWorkmateList = new ArrayList<>();
    private WorkmatesAdapter mWorkmatesAdapter;
    private BookingManager mBookingManager = BookingManager.getInstance();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        Context context = view.getContext();
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        this.mWorkmatesAdapter = new WorkmatesAdapter(this.mWorkmateList);
        this.mRecyclerView.setAdapter(this.mWorkmatesAdapter);
        requireActivity().setTitle(getString(R.string.Titre_Toolbar_workmates));
        configureOnClickRecyclerView();
        return view;
    }

    private void initList(){
        UserManager.getWorkmateCollection()
                .get()
                .addOnCompleteListener(task -> {
                    mWorkmateList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mWorkmateList.add(document.toObject(Workmate.class));
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    Collections.sort(mWorkmateList, new Comparator<Workmate>() {
                        public int compare(Workmate obj1, Workmate obj2) {
                            return obj1.getName().compareToIgnoreCase(obj2.getName());
                        }
                    });
                    mRecyclerView.setAdapter(new WorkmatesAdapter(mWorkmateList));
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_workmates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Workmate result = mWorkmatesAdapter.getWorkmates(position);
                    checkBooking(result);
                });
    }

    private void startRestaurantDetails(String placeId) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("PlaceDetailResult", placeId);
        startActivity(intent);
    }

    private void checkBooking(Workmate workmate){
        mBookingManager.getBooking(workmate.getUid(), getTodayDate()).addOnCompleteListener(bookingTask -> {
            if (bookingTask.isSuccessful()){
                if (!(bookingTask.getResult().isEmpty())) {
                    for (QueryDocumentSnapshot booking : bookingTask.getResult()) {
                        startRestaurantDetails(Objects.requireNonNull(booking.getData().get("restaurantId")).toString());
                    }
                } else {
                    showToast(getContext(), getResources().getString(R.string.hasnt_decided, workmate.getName()), Toast.LENGTH_SHORT);
                }
            }
        });
    }


}