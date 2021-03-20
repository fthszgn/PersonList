package com.example.newtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonTwoFragment extends Fragment {
    private CustomRVAdapter rAdapter;
    private MainActivity activity;
    private List<PersonModel> personModelList;
    private Realm realm;
    private RecyclerView recyclerView;

    public PersonTwoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        personModelList = new ArrayList<>();
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_two, container, false);
        RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                .equalTo(Const.FRAGMENT_ID, 2).findAll();
        personModelList.addAll(realmResults);
        rAdapter = new CustomRVAdapter(personModelList, FragmentType.frTwo,
                (MainActivity) getActivity());
        recyclerView = view.findViewById(R.id.recycler_view_person_two);
        RecyclerView.LayoutManager rLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(rLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);
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
                .equalTo(Const.FRAGMENT_ID, 2).findAll();
        personModelList.addAll(realmResults);
        rAdapter = new
                CustomRVAdapter(personModelList, FragmentType.frTwo, (MainActivity) getActivity());
        recyclerView.setAdapter(rAdapter);
        rAdapter.notifyDataSetChanged();
    }


    CustomRVAdapter getrAdapter() {
        return rAdapter;
    }

    List<PersonModel> getPersonModelList() {
        return personModelList;
    }
    boolean isEmptyTwo() {
        realm = Realm.getDefaultInstance();
        RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                .equalTo(Const.FRAGMENT_ID, 1).findAll();
        return realmResults.size() == 0;
    }
}
