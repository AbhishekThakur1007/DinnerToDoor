package com.example.food.AdminSide.EmployeesDetails;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MemberDialog extends DialogFragment {

    ImageButton Back;
    View view;
    List<Employees> employees;
    RecyclerView Members;
    TextView HaiYaaNhi;
    MemberRecycler memberRecycler;
    DatabaseReference databaseReference;
    long Count;
    SQlDataAdminInformation sQlDataAdminInformation;
    String GetDetails;
    Context Context;

    public void MemberDialog(){}

    public  MemberDialog(Context context){
        this.Context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Holo_Light);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.member_info, container, false);

        Back = view.findViewById(R.id.MemberBack);
        HaiYaaNhi = view.findViewById(R.id.HaiYaaNhi);
        Members = view.findViewById(R.id.MembersRecycler);
        Members.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Staff");

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        /*Check();*/
        Display();
        return view;

    }

    private void Check() {
        // Get Employee count from Sql

        final int i = sQlDataAdminInformation.EmployeesCount();

        // Get Count from Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     Count =  snapshot.getChildrenCount();
                }else{
                    Count = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Check
        if(Count == i){
            // Members Present
            if(i > 0){
                Display();
                Members.setVisibility(View.VISIBLE);
                HaiYaaNhi.setVisibility(View.GONE);
            }else{
                Members.setVisibility(View.GONE);
                HaiYaaNhi.setVisibility(View.VISIBLE);
            }
        }else{
            //Add New
            sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
            List<String> list1 = new ArrayList<>();

            //Get Name From SQL
            list1 = sQlDataAdminInformation.Employees();

            //For Name From Db
            final List<String> list = new ArrayList<>();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            list.add(Objects.requireNonNull(snapshot1.child("employeeName").getValue()).toString());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Get New Members Name
            List<String> list2 = new ArrayList<>();
            for(int j = 0; j <= list.size(); j++ ){
                 String Name1 = list.get(j);
                 for(int z = 0; z <= list1.size(); z++ ){
                     if(list1.get(z).equals(Name1)){
                         break;
                     }else{
                         list2.add(Name1);
                     }
                 }
            }

            for (int c =0; c <= list2.size(); c++ ){
                GetDetails = list2.get(c);
                databaseReference.orderByChild(GetDetails).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Name = Objects.requireNonNull(snapshot.child("employeeName").getValue()).toString();
                        String Age = Objects.requireNonNull(snapshot.child("age").getValue()).toString();
                        String Address = Objects.requireNonNull(snapshot.child("address").getValue()).toString();
                        String Phone = Objects.requireNonNull(snapshot.child("number").getValue()).toString();
                        Employees employees = new Employees(Name,Phone,Age,Address);
                        sQlDataAdminInformation.AddEmployee(employees);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            Display();
        }

    }

    private void Display() {
        employees = new ArrayList<>();
        sQlDataAdminInformation = new SQlDataAdminInformation(getContext());
        Employees employees2 = new Employees("Deepak","9815229895","24","Avanti Vihar, Punjab");
        Employees employees1 = new Employees("Sahil","9815229895","27","Rail Vihar, Punjab");
        employees.add(employees2);
        employees.add(employees1);
        memberRecycler = new MemberRecycler(Context,employees,getActivity());
        Members.setAdapter(memberRecycler);
    }

}
