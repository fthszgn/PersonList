package com.example.newtab;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ResultFragment resultFragment;
    private int pos;
    private ViewPager viewPager;
    private PersonOneFragment personOneFragment;
    private PersonTwoFragment personTwoFragment;
    private Button refreshButton;
    private SharedPreferences.Editor editor;
    private FloatingActionButton fab;
    private ImageButton locationButton;
    private double latitude;
    private double longitude;
    private Bitmap selectedImage;
    private Realm realm;
    private SharedPreferences sharedPreferences;
    private EditText editTextSearch;
    private List<PhotoModel> photoModelList;
    private List<VideoModel> videoModelList;
    private ProgressDialog dialog;
    private List<PersonModel> personModelList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRealm();
        initViews();
        if (sharedPreferences.getBoolean(Const.IS_FIRST, false)) {
            downloadImagesData();
        } else {
            setupFragments();
        }
        location();
    }

    private void initViews() {
        dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);
        Button buttonLogOut = findViewById(R.id.buttonLogOut);
        locationButton = findViewById(R.id.locationButton);
        refreshButton = findViewById(R.id.refreshButton);
        fab = findViewById(R.id.fab);
        editTextSearch = findViewById(R.id.edittextSearch);
        personModelList = new ArrayList<>();
        photoModelList = new ArrayList<>();
        videoModelList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(
                Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(Const.CHECK, false);
                editor.apply();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        realm.deleteAll();
                    }
                });
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                final EditText firstNameEditText = dialog.findViewById(R.id.firstEdit);
                final EditText lastNameEditText = dialog.findViewById(R.id.lastEdit);
                Button addButton = dialog.findViewById(R.id.addButton);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String firstName = firstNameEditText.getText().toString();
                        final String lastName = lastNameEditText.getText().toString();
                        if (firstName.matches("") || lastName.matches("")) {
                            Toast.makeText(MainActivity.this,
                                    R.string.empty_message, Toast.LENGTH_LONG).show();
                        } else {
                            String name = firstNameEditText.getText().toString()
                                    + " " + lastNameEditText.getText().toString();
                            if (pos == 0) {
                                final PersonModel personModel = new PersonModel
                                        (2, 1, name, Const.COMPANY_NAME);
                                Snackbar.make(viewPager,
                                        R.string.add_person, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action, null).show();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.copyToRealm(personModel);
                                        personOneFragment.updateData();
                                    }
                                });
                            }
                            if (pos == 1) {
                                final PersonModel personModel = new PersonModel
                                        (3, 2, name, Const.COMPANY_NAME);
                                Snackbar.make(viewPager,
                                        R.string.add_person, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.action, null).show();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(@NonNull Realm realm) {
                                        realm.copyToRealm(personModel);
                                        personTwoFragment.updateData();
                                    }
                                });
                            }

                            dialog.dismiss();
                        }
                    }

                });
                Button cancelButton = dialog.findViewById(R.id.cancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        });
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        editTextSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                return false;
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0) {
                    dialog.show();
                    downloadPersonChain(pos + 1);

                } else if (pos == 1) {
                    dialog.show();
                    downloadPersonChain(pos + 1);
                }
            }
        });

    }

    public void setPersonObject(int index, PersonModel person) {
        if (index == 1) {
            resultFragment.setPersonOne(person);
        } else if (index == 2) {
            resultFragment.setPersonTwo(person);
        }
    }

    private void viewPagerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                if (pos == 2) {
                    fab.hide();
                    refreshButton.setVisibility(View.INVISIBLE);
                    editTextSearch.setVisibility(View.GONE);
                } else {
                    refreshButton.setVisibility(View.VISIBLE);
                    fab.show();
                    editTextSearch.setVisibility(View.VISIBLE);
                    if (pos == 1) {
                        refreshButton.setBackgroundResource(R.drawable.refreshgreen);
                    } else if (pos == 0) {
                        refreshButton.setBackgroundResource(R.drawable.refreshblack);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public int getPos() {
        return pos;
    }


    public void location() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED && checkSelfPermission(Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        assert mLocationManager != null;
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1, mLocationListener);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Longitude: " + longitude + " Latitude: " +
                        latitude, Snackbar.LENGTH_LONG)
                        .setAction("Location", null).show();
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void getImageFromAlbum(int i) {

        if (i == 1) {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 1);
        }
        if (i == 2) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    selectedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    final Bitmap finalSelectedImage = rotateImage(selectedImage,90);
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.image_dialog);
                    final ImageView imageView = dialog.findViewById(R.id.imageView_bitmap);
                    imageView.setImageBitmap(finalSelectedImage);
                    dialog.show();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageData = data.getData();
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap
                                (this.getContentResolver(), imageData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.image_dialog);
                final ImageView imageView = dialog.findViewById(R.id.imageView_bitmap);
                imageView.setImageBitmap(selectedImage);
                dialog.show();
                break;
        }
    }

    public void playVideo(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void filter(String text) {
        if (pos == 0) {
            if (text.length() > 2) {
                List<PersonModel> filteredList = new ArrayList<>();
                for (PersonModel item : personOneFragment.getPersonModelList()) {
                    if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                personOneFragment.getRAdapter().filterList(filteredList);
            } else {
                personOneFragment.getRAdapter().filterList(personOneFragment.getPersonModelList());
            }
        }
        if (pos == 1) {
            if (text.length() > 2) {
                List<PersonModel> filteredList = new ArrayList<>();
                for (PersonModel item : personTwoFragment.getPersonModelList()) {
                    if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                personTwoFragment.getrAdapter().filterList(filteredList);
            } else {
                personTwoFragment.getrAdapter().filterList(personTwoFragment.getPersonModelList());
            }
        }

    }

    private void downloadImagesData() {
        editor.putBoolean(Const.IS_FIRST, false);
        editor.apply();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        service.photos().enqueue(new Callback<PhotoModelService>() {
            @Override
            public void onResponse(@NonNull Call<PhotoModelService> call,
                                   @NonNull Response<PhotoModelService> response) {
                for (int i = 0; i < 6; i++) {
                    assert response.body() != null;
                    photoModelList.add(response.body().getImages().get(i));
                }
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        realm.copyToRealm(photoModelList);
                    }
                });
                downloadPersonChain(0);
            }

            @Override
            public void onFailure(@NonNull Call<PhotoModelService> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, (CharSequence) t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadVideoData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubService service = retrofit.create(GitHubService.class);
        service.videos().enqueue(new Callback<VideoModelService>() {
            @Override
            public void onResponse(@NonNull Call<VideoModelService> call,
                                   @NonNull Response<VideoModelService> response) {
                for (int i = 0; i < 4; i++) {
                    assert response.body() != null;
                    videoModelList.add(response.body().getVideos().get(i));
                }
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        realm.copyToRealm(videoModelList);
                    }
                });
                resultFragment = new ResultFragment();
                personOneFragment = new PersonOneFragment();
                personTwoFragment = new PersonTwoFragment();
                List<Fragment> fragmentList = new ArrayList<>();
                fragmentList.add(personOneFragment);
                fragmentList.add(personTwoFragment);
                fragmentList.add(resultFragment);
                final CustomVPAdapter customVPAdapter =
                        new CustomVPAdapter(MainActivity.this,
                                getSupportFragmentManager(), fragmentList);
                viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(customVPAdapter);
                TabLayout tabs = findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPagerListener();
            }

            @Override
            public void onFailure(@NonNull Call<VideoModelService> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, (CharSequence) t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public PersonOneFragment getPersonOneFragment() {
        return personOneFragment;
    }

    public PersonTwoFragment getPersonTwoFragment() {
        return personTwoFragment;
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    public void initRealm() {
        Realm.init(MainActivity.this);
        RealmConfiguration config = new RealmConfiguration.Builder().name(Const.REALM_DB_NAME).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public void downloadPersonChain(final int a) {
        personModelList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final GitHubService service = retrofit.create(GitHubService.class);
        service.listRepos().enqueue(new Callback<JsonModel>() {
            @Override
            public void onResponse(@NonNull Call<JsonModel> call,
                                   @NonNull Response<JsonModel> response) {
                assert response.body() != null;
                List<PersonModel> personList = response.body().getUsers();
                personModelList.addAll(personList);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        deleteDb(a);
                        dialog.dismiss();
                    }
                });
                if (a == 0)
                    downloadVideoData();
            }

            @Override
            public void onFailure(@NonNull Call<JsonModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, (CharSequence) t,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteDb(int a) {
        if (a == 0) {
            RealmResults<PersonModel> realmResults = realm.where(PersonModel.class).findAll();
            if (realmResults.size() > 0) {
                realm.deleteAll();
            }

        } else {
            RealmResults<PersonModel> realmResults = realm.where(PersonModel.class)
                    .equalTo(Const.FRAGMENT_ID, a).findAll();
            realmResults.deleteAllFromRealm();
        }
        insertDb(a);
    }

    public void insertDb(int a) {
        if (a == 0) {
            realm.copyToRealm(personModelList);
        } else {
            List<PersonModel> tempPersonList = realm.where(PersonModel.class).equalTo(Const.FRAGMENT_ID, a).findAll();
            realm.copyToRealm(tempPersonList);
            dialog.dismiss();
        }
        updatePage(a);
    }

    public void updatePage(int a) {
        if (a == 1) {
            personOneFragment.updateData();
        } else if (a == 2) {
            personTwoFragment.updateData();
        }

    }

    public void setupFragments() {
        resultFragment = new ResultFragment();
        personOneFragment = new PersonOneFragment();
        personTwoFragment = new PersonTwoFragment();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(personOneFragment);
        fragmentList.add(personTwoFragment);
        fragmentList.add(resultFragment);
        final CustomVPAdapter customVPAdapter =
                new CustomVPAdapter(MainActivity.this, getSupportFragmentManager(),
                        fragmentList);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(customVPAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPagerListener();
        dialog.dismiss();
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}

//    @Override
//    public void onBackPressed() {
//System.out.println("asdasdasd");
//           View view = this.getCurrentFocus();
//           if(view != null){
//               InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//               assert imm != null;
//               imm.hideSoftInputFromWindow(view.getWindowToken(),0);
//           }
//
//        super.onBackPressed();
//    }
//
//



