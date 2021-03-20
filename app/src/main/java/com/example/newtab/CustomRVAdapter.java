package com.example.newtab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class CustomRVAdapter extends RecyclerView.Adapter<CustomRVAdapter.ViewHolder> {
    private FragmentType fr;
    private List<PersonModel> personList;
    private PersonModel lastSelected;
    private MainActivity activity;
    private int currentPage = 0;
    private Realm realm;

    CustomRVAdapter(List<PersonModel> personList, FragmentType fr, MainActivity activity) {
        this.personList = personList;
        this.fr = fr;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemViewRowOne;
        View itemViewRowTwo;
        itemViewRowOne = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_one_person, parent, false);
        itemViewRowTwo = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_two_person, parent, false);
        switch (fr) {
            case frOne:
                currentPage = 1;
                return new ViewHolder(itemViewRowOne);
            case frTwo:
                currentPage = 2;
                return new ViewHolder(itemViewRowTwo);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        realm = Realm.getDefaultInstance();
        Log.v("item no: ", String.valueOf(position));
        final PersonModel person = personList.get(position);
        if(person.getVideoId() > 3)
        holder.camera.setVisibility(View.INVISIBLE);
        else{
            holder.camera.setVisibility(View.VISIBLE);
            final String videoUrl = Objects.requireNonNull(realm.where(VideoModel.class)
                    .equalTo(Const.VIDEO_ID, person.getVideoId()).findFirst()).getVideoUrl();
            holder.camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.playVideo(videoUrl);
                }
            });
        }

        String imageUrl = Objects.requireNonNull(realm.where(PhotoModel.class)
                .equalTo(Const.PHOTO_ID, person.getImageId()).findFirst()).getPhotoUrl();

        Picasso.get()
                .load(imageUrl)
                .transform(new CircleTransform())
                .into(holder.personImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e) {
                    }
                });
        holder.name.setText(person.getName());
        holder.company.setText(person.getCompany());
        holder.personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setTitle("Make Your Choose")
                        .setMessage("How way do you want to set image")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                activity.getImageFromAlbum(2);
                            }
                        })

                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.getImageFromAlbum(1);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
//               activity.getImageFromAlbum();
//               holder.personImage.setImageBitmap(selectedImage);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        person.setSelected(!person.isSelected());
                    }
                });

                if (person.isSelected()) {
                    if (lastSelected != null && lastSelected != person) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                lastSelected.setSelected(false);
                            }
                        });
                    }
                    activity.setPersonObject(currentPage, person);

                } else {
                    activity.setPersonObject(currentPage, PersonModel.getNullModel());
                }
                lastSelected = person;
                notifyDataSetChanged();

                Log.v("PERSON_STATE: ", String.valueOf(person.isSelected()));
            }
        });
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete Person");
                builder.setMessage("Are You Sure to Delete This Person?");
                builder.setPositiveButton("No", null);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(activity, "Person Is Removed", Toast.LENGTH_SHORT).show();
                        if (activity.getPersonOneFragment().isEmptyOne() && activity.getPos() == 0) {
                            Snackbar.make(activity.getViewPager(),
                                    "THERE IS NO ANY PERSON !!", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", null).show();
                        }
                        if (activity.getPersonTwoFragment().isEmptyTwo() && activity.getPos() == 1) {
                            Snackbar.make(activity.getViewPager(),
                                    "THERE IS NO ANY PERSON !!", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", null).show();
                        }
                        if (activity.getPos() == 0) {
                            final RealmResults<PersonModel> realmResults = realm.where(PersonModel
                                    .class).equalTo(Const.FRAGMENT_ID, 1).findAll();
                            final PersonModel person = realmResults.get(position);
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    person.deleteFromRealm();
                                    activity.getPersonOneFragment().updateData();
                                    activity.setPersonObject(1,PersonModel.getNullModel());

                                }
                            });

                        } else {
                            final RealmResults<PersonModel> realmResults = realm.where(PersonModel.
                                    class).equalTo(Const.FRAGMENT_ID, 2).findAll();
                            final PersonModel person = realmResults.get(position);
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    assert person != null;
                                    person.deleteFromRealm();
                                    activity.getPersonTwoFragment().updateData();
                                    activity.setPersonObject(2,PersonModel.getNullModel());

                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView company;
        ImageView personImage;
        ImageView imageDelete;
        ProgressBar progressBar;
        ImageView camera;

        private ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            company = itemView.findViewById(R.id.company);
            personImage = itemView.findViewById(R.id.PersonImage);
            camera = itemView.findViewById(R.id.camera);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

    void filterList(List<PersonModel> filteredList) {
        personList = filteredList;
        notifyDataSetChanged();
    }

}
