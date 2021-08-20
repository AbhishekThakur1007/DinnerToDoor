package com.example.food.AdminSide.AddNewFood;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewItem2 extends DialogFragment implements DialogInterface.OnClickListener{

    View view1;
    ImageButton Back2,Cancel2;
    RecyclerView ShowAdded1;
    String Price,Discount,WhatToDO,OptionsCheck,ImageLink;
    Spinner Quantity,Size;
    FragmentManager fragmentManager;
    TextInputLayout PriceLay,DiscLay;
    TextInputEditText PriceEdit,DiscountEdit;
    Button Next,Add2;
    ImageButton EditQuantity,EditSize;
    int check;
    private static final String NewItem3 = "NewItem3";
    Bundle getBundle;
    String Unit,SizeString;
    TextView NoItem;
    AddOnsSqliteRecycler addOnsSqliteRecycler;

    // Adapter for List View
    ArrayList<String> List =new ArrayList<>();
    ArrayList<SizeAvailable> list;
    spinnerAdapter arrayAdapter;
    ListView SqlData;
    ArrayAdapter<String> arrayAdapter1,arrayAdapter2;

    //Dialog Hooks
    TextView Heading;
    Button Cancel,Save;
    TextInputLayout AddEditOption;
    TextInputEditText AddEdit;
    Button ButtonCustom1;
    boolean checkEdit,checkStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
        getBundle = getArguments();
        assert getBundle != null;
        WhatToDO = getBundle.getString("whatTodo1");
        OptionsCheck = getBundle.getString("NoOptions");
        if(savedInstanceState != null){
            Price = savedInstanceState.getString("PriceEdit");
            Discount = savedInstanceState.getString("DiscountEdit");
            PriceEdit.setText(Price);
            DiscountEdit.setText(Discount);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PriceEdit", Objects.requireNonNull(PriceEdit.getText()).toString());
        outState.putString("DiscountEdit", Objects.requireNonNull(DiscountEdit.getText()).toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view1 = inflater.inflate(R.layout.new_item2_layout,container,false);

        //Image Button
        Back2 = view1.findViewById(R.id.NewItem2);
        Cancel2 = view1.findViewById(R.id.Cancel2);

        //Recycler View
        ShowAdded1 = view1.findViewById(R.id.ShowAdded);
        ShowAdded1.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        fragmentManager = getActivity().getSupportFragmentManager();

        //Spinner
        Size = view1.findViewById(R.id.SpinnerForSize);
        Quantity = view1.findViewById(R.id.SpinnerQuantity);

        //Add multiple items
        Add2 = view1.findViewById(R.id.Add2);

        //Edit Button
        EditSize = view1.findViewById(R.id.edit_food0);
        EditQuantity = view1.findViewById(R.id.edit_food2);

        // Layout Edit
        PriceLay = view1.findViewById(R.id.LayTypePrice);
        DiscLay = view1.findViewById(R.id.LayTypeDis);

        // Edit Text
        PriceEdit = view1.findViewById(R.id.PriceType);
        DiscountEdit = view1.findViewById(R.id.DiscountEdit);

        // Next2
        Next = view1.findViewById(R.id.Next2);

        if(WhatToDO.equals("Update")){
            Display();

        }else if(WhatToDO.equals("New")){

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

        Size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SizeString = Size.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Unit = Quantity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkDetails()){
                    return;
                }
                if(Size.getSelectedItem().toString().equals("None")){
                    Toast.makeText(getContext(),"Please Select Add-On",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(Quantity.getSelectedItem().toString().equals("None")){
                        Toast.makeText(getContext(),"Please Select Quantity",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DisplayInRecycler(Size.getSelectedItem().toString(),Quantity.getSelectedItem().toString(),PriceEdit.getText().toString(),DiscountEdit.getText().toString());
                }
            }
        });

        EditSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 1;
                OpenDialog(check);
            }
        });

        EditQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = 2;
                OpenDialog(check);
            }
        });

        if(checkStatus){
            int g = addOnsSqliteRecycler.Update();
            if(g == 0){
                NoItem.setVisibility(View.VISIBLE);
            }
            checkStatus = false;
        }

        SpinnerUpdate();
        NoItem = view1.findViewById(R.id.NoItem);
        SQlDataAdminInformation sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
        final int i = sQlDataAdminInformation.GetSizeCount();
        if(i > 0){
            UpdateinRecyclyer();
        }

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQlDataAdminInformation sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
                final int j = sQlDataAdminInformation.GetSizeCount();
                if(j == 0 && !checkDetails() && WhatToDO.equals("New")){
                        return;
                }else if (j == 0 && !checkDetails()){
                    return;
                }

                if(j == 0){
                        OptionsCheck = "No";
                    }else{
                        OptionsCheck = "Yes";
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("MenuID2",getBundle.getString("MenuID1"));
                    bundle.putString("Name1",getBundle.getString("Name"));
                    bundle.putString("WhatToDo2",WhatToDO);
                    bundle.putString("ImageLink",ImageLink);
                    bundle.putString("Options1",OptionsCheck);
                    if(!getBundle.getString("Description").equals("No")){
                        bundle.putString("Description1",getBundle.getString("Description"));
                    }else{
                        bundle.putString("Description1","No");
                    }
                    bundle.putString("Price1", Objects.requireNonNull(PriceEdit.getText()).toString());
                    if(Objects.requireNonNull(DiscountEdit.getText()).toString().length() > 0){
                        bundle.putString("Discount1", Objects.requireNonNull(DiscountEdit.getText()).toString());
                    }else if(Objects.requireNonNull(DiscountEdit.getText()).toString().length() == 0 ){
                        bundle.putString("Discount1","No");
                    }
                    NewItem3 newItem3 = new NewItem3();
                    newItem3.setArguments(bundle);
                    if(fragmentManager.findFragmentByTag(NewItem3)!= null){
                        fragmentManager.popBackStack("New2",0);
                    }else {
                        newItem3.show(fragmentManager.beginTransaction().addToBackStack("New2").hide(NewItem2.this), NewItem3);
                    }
            }
        });

        return view1;
    }

    private void DisplayInRecycler(String toString, String toString1, String toString2, String toString3) {
        SizeAvailable sizeAvailable = new SizeAvailable(toString,toString1,toString2,toString3);
        SQlDataAdminInformation db = new SQlDataAdminInformation(getContext());
        if(db.GetSizeCount() == 0){
            long Check =  db.SizeInsert(sizeAvailable);
            if(Check > 0){
                UpdateinRecyclyer();
            }
        }else{
            int i = db.CheckIfAvail(sizeAvailable);
            if(i != 0 ){
                Toast.makeText(getContext(),"Already Present",Toast.LENGTH_SHORT).show();
             return;
            }
            long Check =  db.SizeInsert(sizeAvailable);
            if(Check > 0){
                UpdateinRecyclyer();
            }
        }

    }

    private void GetFromFirebaseList(){
        SQlDataAdminInformation db = new SQlDataAdminInformation(getContext());
        FirebaseDatabase.getInstance().getReference("Foods").child(getBundle.getString("MenuID1")).child("options").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot Snapshot: snapshot.getChildren()){
                    SizeAvailable available = new SizeAvailable(Snapshot.child("sizeCategory").toString(),Snapshot.child("quantity").toString(),Snapshot.child("price").toString(),Snapshot.child("discount").toString());
                    db.SizeInsert(available);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void UpdateinRecyclyer() {

        SQlDataAdminInformation db = new SQlDataAdminInformation(getContext());
        list = new ArrayList<>(db.SizeDetails());
        addOnsSqliteRecycler = new AddOnsSqliteRecycler(getContext(),getActivity(),list);
        ShowAdded1.setAdapter(addOnsSqliteRecycler);
        NoItem.setVisibility(View.GONE);
        ShowAdded1.setVisibility(View.VISIBLE);
        checkStatus = true;
    }

    private void backPressed() {
        getActivity().getSupportFragmentManager().popBackStack("New1",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Objects.requireNonNull(getDialog()).dismiss();
    }

    private void AlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        if(WhatToDO.equals("New")){
            dialog.setTitle("Cancel New Item").setIcon(R.drawable.delete);
        }else{
            dialog.setTitle("Cancel Update").setIcon(R.drawable.delete);
        }
        dialog.setMessage("Continue ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().popBackStack("food1",FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

    private boolean checkDetails() {
        if(Objects.requireNonNull(PriceEdit.getText()).toString().length() == 0 || Integer.parseInt(PriceEdit.getText().toString()) < 10){
            PriceLay.setErrorEnabled(true);
            PriceLay.setError("Please Enter Price");
            setFocusNew(PriceEdit);
            return false;
        }else{
            PriceLay.setErrorEnabled(false);
            PriceLay.setError(null);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void OpenDialog(final int check) {
        final SQlDataAdminInformation sqlDataAdminInformation = new SQlDataAdminInformation(getActivity());

        final Dialog dialog = new Dialog(getActivity());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.spinner_edit);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Heading for list
        Heading = dialog.findViewById(R.id.SpinnerEdit);

        //List
        SqlData = dialog.findViewById(R.id.listSpinner);

        // Button
        Cancel = dialog.findViewById(R.id.cancel);
        Save = dialog.findViewById(R.id.Yes2);

        // Layout
        AddEditOption = dialog.findViewById(R.id.AddNew);
        // Edit text
        AddEdit = dialog.findViewById(R.id.changes);

        ButtonCustom1 = dialog.findViewById(R.id.NewAdd);
        ImageButton Close = dialog.findViewById(R.id.closeDialog);

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SpinnerUpdate();
            }
        });

        if(check == 1){
            List = sqlDataAdminInformation.inflateSpinner(1);
            Heading.setText("Add-On Name");
        }
        else if(check == 2){
            List = sqlDataAdminInformation.inflateSpinner(2);
            Heading.setText("Edit Units/Quantity");
        }

        arrayAdapter = new spinnerAdapter(List,check,getContext());
        SqlData.setAdapter(arrayAdapter);

        ButtonCustom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEdit.getText().clear();
                EditTextDislpay(checkEdit);
            }
        });

        AddEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 2){
                    Save.setText("Add");
                }else{
                    Save.setText("Save");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(AddEdit.getText()).toString().length() > 1){
                    String EditText = AddEdit.getText().toString();
                    if(check == 1){
                        long Check1 =  sqlDataAdminInformation.AddNewSpinnerInCategory(EditText);
                        if(Check1 != -1){
                            Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
                            List.add(EditText);
                            AddEdit.getText().clear();
                            arrayAdapter = new spinnerAdapter(List,check,getContext());
                            SqlData.setAdapter(arrayAdapter);
                        }else{
                            Toast.makeText(getActivity(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(check == 2){
                        long Check1 = sqlDataAdminInformation.AddNewSpinnerInQuantity(EditText);
                        if(Check1 != -1){
                            Toast.makeText(getActivity(),"Added",Toast.LENGTH_SHORT).show();
                            List.add(EditText);
                            arrayAdapter = new spinnerAdapter(List,check,getContext());
                            SqlData.setAdapter(arrayAdapter);
                            AddEdit.getText().clear();
                        }else{
                            Toast.makeText(getActivity(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                SpinnerUpdate();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEdit.getText().clear();
                EditTextDislpay(checkEdit);
            }
        });
        dialog.show();
    }

    public void setFocusNew(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        editText.setSelection(Objects.requireNonNull(editText.getText()).toString().length());
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    //Check
    public void Display(){
        if(OptionsCheck.equals("No")){
            PriceEdit.setText(getBundle.getString("Price"));
            /*setFocusNew(PriceEdit);*/
            if(!getBundle.getString("Discount").equals("No")){
                DiscountEdit.setText(getBundle.getString("Discount"));
            }
        }else if (OptionsCheck.equals("Yes")){
            GetFromFirebaseList();
        }
        ImageLink = getBundle.getString("imageLink");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkStatus){
            int g = addOnsSqliteRecycler.Update();
            if(g == 0){
                NoItem.setVisibility(View.VISIBLE);
            }
        }
    }

    private void SpinnerUpdate() {
        SQlDataAdminInformation sqlDataAdminInformation = new SQlDataAdminInformation(getActivity());
        arrayAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, sqlDataAdminInformation.inflateSpinner(1));
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        Size.setAdapter(arrayAdapter1);
        arrayAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, sqlDataAdminInformation.inflateSpinner(2));
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        Quantity.setAdapter(arrayAdapter2);
    }

    private void EditTextDislpay(boolean state) {
        if(!state){
            AddEditOption.setVisibility(View.VISIBLE);
            checkEdit = true;
        }else{
            AddEditOption.setVisibility(View.GONE);
            checkEdit = false;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

}
