package com.example.newtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonOneFragment extends Fragment {
    private CustomRVAdapter rAdapter;
    private boolean isFirstCreated;
    private Realm realm;
    private List<PersonModel> personModelList;
    private MainActivity activity;
    private RecyclerView recyclerView;

    public PersonOneFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        realm = Realm.getDefaultInstance();
        isFirstCreated = true;
        personModelList = new ArrayList<>();
        RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                .equalTo(Const.FRAGMENT_ID, 1).findAll();
        personModelList.addAll(realmResults);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_one, container, false);

        if (isFirstCreated) {
            rAdapter = new CustomRVAdapter(personModelList,
                    FragmentType.frOne, (MainActivity) getActivity());
            rAdapter.notifyDataSetChanged();
            recyclerView = view.findViewById(R.id.recycler_view_person_one);
            RecyclerView.LayoutManager rLayoutManager =
                    new LinearLayoutManager(Objects.requireNonNull(getActivity())
                            .getApplicationContext());
            recyclerView.setLayoutManager(rLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(rAdapter);

            isFirstCreated = false;

        } else {
            recyclerView = view.findViewById(R.id.recycler_view_person_one);
            RecyclerView.LayoutManager rLayoutManager =
                    new LinearLayoutManager(Objects.requireNonNull(getActivity())
                            .getApplicationContext());
            recyclerView.setLayoutManager(rLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(rAdapter);

        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    activity.getFab().hide();
                } else {
                    activity.getFab().show();
                }
            }
        });
        return view;
    }
    void updateData() {
        personModelList.clear();
        RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                .equalTo(Const.FRAGMENT_ID, 1).findAll();
        personModelList.addAll(realmResults);
        rAdapter = new
                CustomRVAdapter(personModelList, FragmentType.frOne, (MainActivity) getActivity());
        recyclerView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
    }

    CustomRVAdapter getRAdapter() {
        return rAdapter;
    }
    List<PersonModel> getPersonModelList() {
        return personModelList;
    }
    boolean isEmptyOne() {
        realm = Realm.getDefaultInstance();
        RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                .equalTo(Const.FRAGMENT_ID, 1).findAll();
        return realmResults.size() == 0;
    }

}
