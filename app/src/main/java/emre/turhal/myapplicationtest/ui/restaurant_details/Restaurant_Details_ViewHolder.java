package emre.turhal.myapplicationtest.ui.restaurant_details;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.databinding.ActivityRestaurantDetailsItemBinding;
import emre.turhal.myapplicationtest.models.Workmate;


public class Restaurant_Details_ViewHolder extends RecyclerView.ViewHolder {

    ActivityRestaurantDetailsItemBinding binding;

    public Restaurant_Details_ViewHolder(ActivityRestaurantDetailsItemBinding itemView) {
        super(itemView.getRoot());
        binding = itemView;
    }

    public void updateWithData(Workmate results) {
        RequestManager glide = Glide.with(itemView);
        if (!(results.getUrlPicture() == null)) {
            glide.load(results.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(binding.detailMainPicture);
        } else {
            glide.load(R.drawable.ic_anon_user_48dp).apply(RequestOptions.circleCropTransform()).into(binding.detailMainPicture);
        }
        binding.detailTextviewUsername.setText(itemView.getResources().getString(R.string.restaurant_recyclerview, results.getName()));
        changeTextColor();
    }

    private void changeTextColor() {
        int mColor = itemView.getContext().getResources().getColor(R.color.colorBlack);
        binding.detailTextviewUsername.setTextColor(mColor);
    }
}