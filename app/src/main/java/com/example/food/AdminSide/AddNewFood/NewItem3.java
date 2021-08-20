package com.example.food.AdminSide.AddNewFood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;
import com.example.food.Common.Food_Constructor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class NewItem3 extends DialogFragment implements DialogInterface.OnClickListener {

    View view3;
    ImageButton Back2,Cancel2;
    ImageView SelectedPhoto;
    TextView NoImage;
    Button Change,UploadData;
    String MenuId,NameFinal,DescriptionFinal,PriceFinal,DiscountFinal,ImageLinkGetFromBundle,WhatToDo,Options;
    Bundle Getbundle;
    Food_Constructor foodConstructor;

    // Image Data
    private static final int PICK_Food_IMAGE_REQUEST = 99;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageTask storageTask;
    SQlDataAdminInformation sQlDataAdminInformation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    boolean CheckButtonClicked,Updated;
    TextView Heading;

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
        Getbundle = getArguments();
        assert Getbundle != null;
        WhatToDo = Getbundle.getString("WhatToDo2");
        Options = Getbundle.getString("Options1");
        MenuId = Getbundle.getString("MenuID2");
        NameFinal = Getbundle.getString("Name1");
        DescriptionFinal = Getbundle.getString("Description1");
        PriceFinal = Getbundle.getString("Price1");
        DiscountFinal = Getbundle.getString("Discount1");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view3 = inflater.inflate(R.layout.new_item3_layout,container,false);

        //Image Button
        Back2 = view3.findViewById(R.id.NewItem3);
        Cancel2 = view3.findViewById(R.id.Cancel3);

        // Heading
        Heading = view3.findViewById(R.id.NewHeaing3);

        //Image
        NoImage = view3.findViewById(R.id.NoImage);
        SelectedPhoto = view3.findViewById(R.id.inspect_foodimage);

        //Button
        Change = view3.findViewById(R.id.button_select_food);
        UploadData = view3.findViewById(R.id.UploadFinal);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Realtime
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Foods");

        if(WhatToDo.equals("Update")){
            Display();
        }else{
            Heading.setText("Upload Photo");
        }

        Cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog();
            }
        });

        Back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        UploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(WhatToDo.equals("New")){
                if(SelectedPhoto.getDrawable() == null){
                    Toast.makeText(getActivity(),"Select Photo",Toast.LENGTH_SHORT).show();
                }
                //Upload
                else{
                    //1
                    Update();
                }
              }else if(WhatToDo.equals("Update")){
                    CheckUpdate();
                }
            }
        });

        return view3;
    }

    private void CheckUpdate() {

        databaseReference.child(MenuId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("name").getValue().toString().equals(NameFinal)){
                    databaseReference.child(MenuId).child("name").setValue(NameFinal);
                }
                if(snapshot.child("description").exists()){
                    if(!DescriptionFinal.equalsIgnoreCase("No") && !snapshot.child("description").getValue().toString().equals(DescriptionFinal)){
                        databaseReference.child(MenuId).child("description").setValue(DescriptionFinal);
                    }else if(DescriptionFinal.equalsIgnoreCase("No")){
                        databaseReference.child(MenuId).child("description").setValue(null);
                    }
                }else{
                    if(!DescriptionFinal.equals("No")){
                        databaseReference.child(MenuId).child("description").setValue(DescriptionFinal);
                    }
                }

                if(CheckButtonClicked){
                    Update();
                    CheckButtonClicked = false;
                }

                if(Options.equals("No")){
                    if(snapshot.child("price").exists()){
                        if(!snapshot.child("price").getValue().toString().equals(PriceFinal)){
                            databaseReference.child(MenuId).child("price").setValue(PriceFinal);
                        }
                    }

                    if(snapshot.child("discount").exists()){
                        if(!DiscountFinal.equalsIgnoreCase("No") &&  !snapshot.child("discount").getValue().toString().equals(DiscountFinal)){
                        databaseReference.child(MenuId).child("discount").setValue(DiscountFinal);
                        }else{
                            databaseReference.child(MenuId).child("discount").setValue(null);
                        }

                    }else{
                        if(DiscountFinal.equals("No")){
                            databaseReference.child(MenuId).child("discount").setValue(null);
                        }else{
                            databaseReference.child(MenuId).child("discount").setValue(DiscountFinal);
                        }
                    }

                }else{ //Logic Pending

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getActivity().getSupportFragmentManager().popBackStack("food1", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Objects.requireNonNull(getDialog()).dismiss();
        Toast.makeText(getContext(),"Updated",Toast.LENGTH_SHORT).show();
    }

    private void Display() {
        ImageLinkGetFromBundle = Getbundle.getString("ImageLink");
        NoImage.setVisibility(View.INVISIBLE);
        Picasso.get().load(ImageLinkGetFromBundle).into(SelectedPhoto);
        Change.setText("Change");
        Heading.setText("Change Photo");
    }

    //2
    private void UploadToFirebaseStorage() {

       if(WhatToDo.equals("Update")){
           final String imageReplace = ImageLinkGetFromBundle;
           if(ImageLinkGetFromBundle.contains("firebasestorage")){
               StorageReference replace = FirebaseStorage.getInstance().getReferenceFromUrl(imageReplace);
               replace.delete();
            }
       }

    }

    private void Update() {
        if (storageTask != null && storageTask.isInProgress()) {
            Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
        }else{
            if(saveUri != null){
                String Name = UUID.randomUUID().toString();
                final StorageReference reference = storageReference.child("Food Photos/" + Name);
                storageTask = reference.putFile(saveUri).
                        addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(WhatToDo.equals("New")){
                                            SendToConstructor(uri.toString());
                                        }else if(WhatToDo.equals("Update") && !Updated){
                                            databaseReference.child(MenuId).child("image").setValue(uri.toString());
                                            UploadToFirebaseStorage();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    }
                });
            }
        }
    }

    //3
    private void SendToConstructor(String Link){
        sQlDataAdminInformation = new SQlDataAdminInformation(getActivity());
        if(!DescriptionFinal.equals("No")){
            if(Options.equals("No")){
                if(!DiscountFinal.equals("No")){
                    foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,DescriptionFinal,null,PriceFinal,DiscountFinal);
                }else{
                    foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,DescriptionFinal,null,PriceFinal,null);
                }
            }else{
                foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,DescriptionFinal, sQlDataAdminInformation.SizeDetails(),null,null);
            }
        }else {
            if(Options.equals("No")){
                if(!DiscountFinal.equals("No")){
                    foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,null,null,PriceFinal,DiscountFinal);
                }else{
                    foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,null,null,PriceFinal,null);
                }
            }else{
                foodConstructor = new Food_Constructor(MenuId,NameFinal,Link,null, sQlDataAdminInformation.SizeDetails(),null,null);
            }
        }
        uploadToDatabase(foodConstructor);
    }

    //4
    private void uploadToDatabase(Food_Constructor foodConstructor) {
        long Parent = System.currentTimeMillis();
        databaseReference.child(String.valueOf(Parent).substring(0,6)).setValue(foodConstructor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getActivity().getSupportFragmentManager().popBackStack("food1", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Objects.requireNonNull(getDialog()).dismiss();
                Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();
                SQlDataAdminInformation sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
                sQlDataAdminInformation.DeleteSize(true,null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        if(WhatToDo.equals("New")){
            dialog.setTitle("Cancel New Item").setIcon(R.drawable.delete);
        }else{
            dialog.setTitle("Cancel Update").setIcon(R.drawable.delete);
        }
        dialog.setMessage("Continue ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().popBackStack("food1", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                dialog.dismiss();
                SQlDataAdminInformation s = new SQlDataAdminInformation(getContext());
                s.DeleteSize(true,null);
                Objects.requireNonNull(getDialog()).dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void backPressed() {
        getActivity().getSupportFragmentManager().popBackStack("New2",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Objects.requireNonNull(getDialog()).dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toast.makeText(getActivity(),"Destroyed",Toast.LENGTH_SHORT).show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_Food_IMAGE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_Food_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null) {
            saveUri = data.getData();
            NoImage.setVisibility(View.INVISIBLE);
            SelectedPhoto.setImageURI(saveUri);
            Change.setText("Change");
            CheckButtonClicked = true;
        }else{
            Toast.makeText(getActivity(),"Something Went Wrong.",Toast.LENGTH_SHORT).show();
        }
    }
}
