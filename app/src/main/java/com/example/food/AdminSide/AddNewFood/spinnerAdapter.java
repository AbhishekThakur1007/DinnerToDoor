package com.example.food.AdminSide.AddNewFood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.food.AdminSide.SQlDataAdminInformation;
import com.example.food.R;

import java.util.ArrayList;

public class spinnerAdapter extends BaseAdapter {

    View view;
    Context context;
    private ArrayList<String> list;
    EditText EditName;
    TextView Name;
    ImageButton Edit,Delete,Save;
    RelativeLayout layoutEdit1,SQLButtonLay1;
    boolean editText;
    int Spinner;
    ViewSwitcher SwitcherTest;

    public spinnerAdapter(ArrayList<String> list,int i , Context context) {
        this.list = list;
        this.context = context;
        this.Spinner = i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        view = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_layout, null);
        }

        // Layout
        layoutEdit1 = view.findViewById(R.id.layoutEdit);
        SQLButtonLay1 = view.findViewById(R.id.SQLButtonLay);

        //Switcher
        SwitcherTest = view.findViewById(R.id.SwitcherTest);
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);

        SwitcherTest.setInAnimation(in);
        SwitcherTest.setOutAnimation(out);

        // Text View
        Name = view.findViewById(R.id.Name);
        Name.setText(list.get(position));

        // Image Button
        Edit = view.findViewById(R.id.editName1);
        Delete = view.findViewById(R.id.delteSpinner);
        Save = view.findViewById(R.id.saveChange);

        // Edit Text
        EditName = view.findViewById(R.id.EditLay);

        if(position == 0){
            Edit.setVisibility(View.INVISIBLE);
            Delete.setVisibility(View.INVISIBLE);
            EditName.setVisibility(View.INVISIBLE);
            Save.setVisibility(View.INVISIBLE);
        }

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*OpenEdit(editText);*/
                SwitcherTest.showNext();
                Toast.makeText(context,"No New Data",Toast.LENGTH_SHORT).show();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogDisplay(position);
            }
        });

        return view;
    }

    private void AlertDialogDisplay(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Delete").setIcon(R.drawable.delete);
        dialog.setMessage("Continue ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQlDataAdminInformation sQlDataAdminInformation = new SQlDataAdminInformation(context);
                long Check = sQlDataAdminInformation.DeleteFromSpinner(Spinner,list.get(position));
                if(Check > 0){
                    notifyDataSetChanged();
                    list.remove(position);
                    Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void OpenEdit(boolean editText1) {
        if(!editText1){
            Name.setVisibility(View.GONE);
            SQLButtonLay1.setVisibility(View.GONE);
            EditName.setText(Name.getText().toString());
            layoutEdit1.setVisibility(View.VISIBLE);
            editText = true;
        }else{
            Name.setVisibility(View.VISIBLE);
            SQLButtonLay1.setVisibility(View.VISIBLE);
            layoutEdit1.setVisibility(View.GONE);
            editText = false;
        }
    }

}
