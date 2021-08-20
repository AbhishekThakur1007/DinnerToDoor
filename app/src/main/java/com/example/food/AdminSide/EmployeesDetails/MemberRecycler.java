package com.example.food.AdminSide.EmployeesDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.food.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MemberRecycler extends RecyclerView.Adapter<MemberRecycler.MemberView> {

    Context context;
    List<Employees> employees;
    Activity activity;

    public MemberRecycler(Context mContext,List<Employees> employees1,Activity activity1){
        this.context = mContext;
        this.employees = employees1;
        this.activity = activity1;
    }

    public static class MemberView extends RecyclerView.ViewHolder {

        TextView Name,Phone,Address,Dob;
        ImageButton Delete,Call;

        public MemberView(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.MemName1);
            Phone = itemView.findViewById(R.id.MemNumber);
            Dob = itemView.findViewById(R.id.Memage);
            Address = itemView.findViewById(R.id.MemAddress1);
            Delete = itemView.findViewById(R.id.Delete);
            Call = itemView.findViewById(R.id.Call);

        }
    }

    @NonNull
    @Override
    public MemberView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.member_adpater_view,parent,false);
        return new MemberView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberView holder, final int position) {

        holder.Name.setText(employees.get(position).getEmployeeName());
        holder.Phone.setText(employees.get(position).getNumber().substring(3));
        holder.Address.setText(employees.get(position).getAddress());
        holder.Dob.setText(employees.get(position).getAge());

        holder.Delete.setOnClickListener(v -> {
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position,employees.size());
            employees.remove(position);
        });

        holder.Call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + employees.get(position).getNumber()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

}
