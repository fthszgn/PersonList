package com.example.newtab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    private PersonModel personOne;
    private PersonModel personTwo;
    private ImageView imagePersonOne;
    private TextView txtNameOne;
    private TextView txtCompanyOne;
    private ImageView imagePersonTwo;
    private TextView txtNameTwo;
    private TextView txtCompanyTwo;
    private boolean isViewCreated = false;
    private Realm realm;

    public ResultFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        imagePersonOne = view.findViewById(R.id.imageView_result_one);
        txtNameOne = view.findViewById(R.id.textView_name_one);
        txtCompanyOne = view.findViewById(R.id.textView_company_one);
        imagePersonTwo = view.findViewById(R.id.imageView_result_two);
        txtNameTwo = view.findViewById(R.id.textView_name_two);
        txtCompanyTwo = view.findViewById(R.id.textView_company_two);
        imagePersonOne.setImageResource(R.drawable.nophoto);
        imagePersonTwo.setImageResource(R.drawable.nophoto);
        isViewCreated = true;
        refreshPage();
        return view;
    }

    void setPersonOne(PersonModel personOne) {
        this.personOne = personOne;
        refreshPage();
    }

    void setPersonTwo(PersonModel personTwo) {
        this.personTwo = personTwo;
        refreshPage();
    }

    private void refreshPage() {
        if (isViewCreated) {
            MainActivity a = (MainActivity)getActivity();
            a.getFab().hide();
            if (personOne != null) {
                String imageUrlOne = Objects.requireNonNull(realm.where(PhotoModel.class).equalTo
                        (Const.PHOTO_ID, personOne.getImageId()).findFirst()).getPhotoUrl();
                Picasso.get()
                        .load(imageUrlOne)
                        .transform(new CircleTransform())
                        .into(imagePersonOne);
                txtNameOne.setText(personOne.getName());
                txtCompanyOne.setText(personOne.getCompany());
            } else {
                personOne = PersonModel.getNullModel();
            }
            if (personTwo != null) {
                String imageUrlTwo = Objects.requireNonNull(realm.where(PhotoModel.class).equalTo
                        (Const.PHOTO_ID, personTwo.getImageId()).findFirst()).getPhotoUrl();
                Picasso.get()
                        .load(imageUrlTwo)
                        .transform(new CircleTransform())
                        .into(imagePersonTwo);
                txtNameTwo.setText(personTwo.getName());
                txtCompanyTwo.setText(personTwo.getCompany());
            } else {
                personTwo = PersonModel.getNullModel();
            }
        }
    }
}


