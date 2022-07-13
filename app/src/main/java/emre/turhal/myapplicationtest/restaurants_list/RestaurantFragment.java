package emre.turhal.myapplicationtest.restaurants_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import emre.turhal.myapplicationtest.BaseFragment;
import emre.turhal.myapplicationtest.databinding.FragmentRestaurantBinding;
import emre.turhal.myapplicationtest.restaurant_details.DetailsActivity;
import emre.turhal.myapplicationtest.MainActivity;
import emre.turhal.myapplicationtest.R;
import emre.turhal.myapplicationtest.models.googleplaces_gson.ResultDetails;
import emre.turhal.myapplicationtest.utils.ItemClickSupport;


public class RestaurantFragment extends BaseFragment {

    private FragmentRestaurantBinding mBinding;
    private RecyclerView mRecyclerView;
    private MainActivity mMainActivity;
    private RestaurantAdapter mRecyclerViewAdapter;




    public RestaurantFragment(){

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = FragmentRestaurantBinding.inflate(getLayoutInflater());
        mMainActivity =(MainActivity) requireActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = mBinding.getRoot();
        Context context = view.getContext();

        mMainActivity.mLiveData.observe(getViewLifecycleOwner(), resultDetails -> configureRecyclerView());

        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        setHasOptionsMenu(true);
        configureOnClickRecyclerView();
        return view;
    }

    private void configureRecyclerView() {
        mRecyclerViewAdapter = new RestaurantAdapter(mMainActivity.mLiveData.getValue(), mMainActivity.mShareViewModel.getCurrentUserPositionFormatted());
        List<ResultDetails> mResult = mMainActivity.mLiveData.getValue();
        if (mResult != null) {
            Collections.sort(mResult);
        }
        RestaurantAdapter mViewAdapter = new RestaurantAdapter(mResult, mMainActivity.mShareViewModel.getCurrentUserPositionFormatted());
        this.mRecyclerView.setAdapter(mViewAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_restaurant_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    ResultDetails result = mRecyclerViewAdapter.getRestaurantDetails(position);
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("PlaceDetailResult", result.getPlaceId());
                    startActivity(intent);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        configureRecyclerView();
    }




}