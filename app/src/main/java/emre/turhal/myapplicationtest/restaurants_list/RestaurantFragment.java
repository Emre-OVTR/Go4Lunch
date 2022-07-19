package emre.turhal.myapplicationtest.restaurants_list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView view = mBinding.getRoot();
        Context context = view.getContext();

        mMainActivity.mLiveData.observe(getViewLifecycleOwner(), resultDetails -> configureRecyclerView());

        mRecyclerView = view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        requireActivity().setTitle(getString(R.string.Title_Hungry));
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


    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.activity_main_appbar, menu);

        SearchManager searchManager = (SearchManager) requireContext().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.menu_activity_main_search);
        SearchView mSearchView = new SearchView(Objects.requireNonNull(((MainActivity) requireContext()).getSupportActionBar()).getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(mSearchView);
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) requireContext()).getComponentName()));
        mSearchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    mMainActivity.googleAutoCompleteSearch(query);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mSearchView.clearFocus();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.search_too_short), Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 2) {
                    mMainActivity.googleAutoCompleteSearch(query);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                } else if (query.length() == 0) {
                    mMainActivity.searchByCurrentPosition();
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }



}