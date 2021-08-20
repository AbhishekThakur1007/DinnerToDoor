package com.example.food.AdminSide.AddNewFood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


public class NewItem1 extends DialogFragment implements DialogInterface.OnClickListener {

    String MenuId,whatTodo,ImageLink,Price,discount;
    View view;
    TextInputLayout NameLay,DesLay;
    TextInputEditText Name,Description;
    Button Next1;
    boolean UpdateMe,Dis;
    ImageButton Back1,Cancel1;
    private static final String NewItem2 = "NewItem2";
    FragmentManager fragmentManager;
    TextView NewHeaing1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //From Firebase
    String dis,Pri,Size,Quan;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }


    private void NewItem1(){
        // Required empty public constructor
    }

    public void NewItem1(String menuId,String WhatToDo) {
        this.MenuId = menuId;
        this.whatTodo = WhatToDo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Foods").child(MenuId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
         view = inflater.inflate(R.layout.new_item1_layout,container,false);

        // Edit Text
        Name = view.findViewById(R.id.foodUploadfinal);
        Description = view.findViewById(R.id.DescriptionType);

         // Heading
         NewHeaing1 = view.findViewById(R.id.NewHeaing1);
         if(whatTodo.equals("Update")){
             fromWhere();

         }else if (whatTodo.equals("New")){
             NewHeaing1.setText("Add new food");
             setFocusNew(Name);
         }
         // Layout Edit Text
         NameLay = view.findViewById(R.id.LayNewFoodName);
         DesLay = view.findViewById(R.id.LayTypeDes);

         //Image Button
        Back1 = view.findViewById(R.id.NewItem1);
        Cancel1 = view.findViewById(R.id.Cancel1);

        // Next
        Next1 = view.findViewById(R.id.Yes2);

        Cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog();
            }
        });

        Back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        fragmentManager = requireActivity().getSupportFragmentManager();

          Next1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!Check()){
                 }else{
                     Bundle bundle = new Bundle();
                     String finalName = Name.getText().toString().substring(0,1).toUpperCase() + Name.getText().toString().substring(1).toLowerCase();
                     bundle.putString("Name", Objects.requireNonNull(finalName));
                     bundle.putString("MenuID1", MenuId);
                     bundle.putString("whatTodo1",whatTodo);
                     if(Objects.requireNonNull(Description.getText()).toString().length() > 3){
                         bundle.putString("Description",Objects.requireNonNull(Description.getText()).toString());
                     }else if(Objects.requireNonNull(Description.getText()).toString().length() == 0){
                         bundle.putString("Description","No");
                     }
                     // Send if Update
                     if(whatTodo.equals("Update")){
                         bundle.putString("imageLink",ImageLink);
                         if(!UpdateMe){
                             bundle.putString("Price",Price);
                             if(Dis){
                                 bundle.putString("Discount",discount);
                             }else{
                                 bundle.putString("Discount","No");
                             }
                             bundle.putString("NoOptions","No");
                         }else{
                             bundle.putString("NoOptions","Yes");
                         }
                     }

                     NewItem2 newItem2 = new NewItem2();
                     newItem2.setArguments(bundle);
                     if(fragmentManager.findFragmentByTag(NewItem2)!= null){
                         fragmentManager.popBackStack("New1",0);
                     }else{
                         newItem2.show(fragmentManager.beginTransaction().addToBackStack("New1"),NewItem2);
                     }
                 }
             }
         });

        return view;
    }

    public void setFocusNew(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    public void fromWhere(){
        NewHeaing1.setText("Update Data");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("name").exists()){
                Name.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                }
                //SetFocusNew(Name);
                if(snapshot.child("description").exists()){
                    Description.setText(Objects.requireNonNull(snapshot.child("description").getValue()).toString());
                }
                if(snapshot.child("image").exists()){
                    ImageLink = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                }

                if(snapshot.child("options").exists()){ // Pending Logic
                    UpdateRecycler();
                    UpdateMe = true;
                }else{
                    UpdateMe = false;
                    if(snapshot.child("price").exists()){
                        Price = Objects.requireNonNull(snapshot.child("price").getValue()).toString();
                    }

                    if(snapshot.child("discount").exists()){
                         Dis= true;
                        discount = Objects.requireNonNull(snapshot.child("discount").getValue()).toString();
                    }else{
                        Dis= false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateRecycler() {
        final SQlDataAdminInformation sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
        final DatabaseReference databaseReference1 = databaseReference.child("options");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    if(snapshot1.child("price").exists()){
                        Pri = snapshot1.child("price").getValue().toString();
                    }
                    if(snapshot1.child("discount").exists()){
                        dis = snapshot1.child("discount").getValue().toString();
                    }
                    if(snapshot1.child("sizeCategory").exists()){
                        Size = snapshot1.child("sizeCategory").getValue().toString();
                        Quan = snapshot1.child("quantity").getValue().toString();
                        try {
                            SizeAvailable sizeAvailable = new SizeAvailable(Size,Quan,Pri,dis);
                            sQlDataAdminInformation.SizeInsert(sizeAvailable);
                        }catch (Exception ignored){}

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SQlDataAdminInformation s = new SQlDataAdminInformation(getContext());
        s.DeleteSize(true,null);
    }

    private void AlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        if(whatTodo.equals("New")){
            dialog.setTitle("Cancel New Item").setIcon(R.drawable.delete);
        }else{
            dialog.setTitle("Cancel Update").setIcon(R.drawable.delete);
        }

        dialog.setMessage("Continue ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        getActivity().getSupportFragmentManager().popBackStack("food1",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Objects.requireNonNull(getDialog()).dismiss();
    }

    private boolean Check() {
        if(Objects.requireNonNull(Name.getText()).toString().length() == 0){
            NameLay.setErrorEnabled(true);
            NameLay.setError("Name Cannot be empty");
            setFocusNew(Name);
            return false;
        }else{
            NameLay.setErrorEnabled(false);
            NameLay.setError(null);
            return true;
        }
    }

    private boolean Check2() {
        if(Objects.requireNonNull(Description.getText()).toString().length() == 0){
            DesLay.setErrorEnabled(true);
            DesLay.setError("Name Cannot be empty");
            return false;
        }else{
            DesLay.setErrorEnabled(false);
            DesLay.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
