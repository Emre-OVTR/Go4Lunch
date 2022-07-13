package emre.turhal.myapplicationtest.restaurants_list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import emre.turhal.myapplicationtest.databinding.FragmentRestaurantItemBinding;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final List<ResultDetails> mResultDetails;
    private final String mLocation;

    public RestaurantAdapter(List<ResultDetails> items, String location){

        mResultDetails = items;
        mLocation = location;
    }


    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentRestaurantItemBinding view = FragmentRestaurantItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false );
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {

        holder.updateWithData(this.mResultDetails.get(position), this.mLocation);


    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mResultDetails != null) itemCount = mResultDetails.size();
        return itemCount;
    }

    public ResultDetails getRestaurantDetails(int position) {
        return this.mResultDetails.get(position);
    }
}
