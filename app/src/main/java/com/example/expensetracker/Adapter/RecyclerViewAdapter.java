package com.example.expensetracker.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.Model.ExpenseData;
import com.example.expensetracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public  TextView name,date,quan,price;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private FirebaseAuth auth;
    private TextView editName;
    private List<ExpenseData> expenseDataList ;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_recyclerview,parent,false);
        return new ViewHolder(v);
    }


    public RecyclerViewAdapter(List<ExpenseData> expenseData, Context c) {
        this.expenseDataList = expenseData;
        this.context = c;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        //ExpenseData expenseData = expenseDataList.get(position);
        final String id = expenseDataList.get(position).getId();
        name.setText(expenseDataList.get(position).getProduct());
        date.setText(expenseDataList.get(position).getDate());
        quan.setText(MessageFormat.format("{0} Piece/s", String.valueOf(expenseDataList.get(position).getQuantity())));
        price.setText(MessageFormat.format("{0} tk", String.valueOf(expenseDataList.get(position).getAmount() * expenseDataList.get(position).getQuantity())));
        //Log.e("name", "onBindViewHolder: "+expenseDataList );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ExpenseData expenseData = expenseDataList.get(position);
              expenseDetails(v,expenseData,id);

            }
        });
    }

    private void expenseDetails(View v, final ExpenseData expenseData, final String id) {
        final DatabaseReference database;
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Expense List").child(userid);
        builder = new AlertDialog.Builder(v.getContext());

        v = LayoutInflater.from(context).inflate(R.layout.update_delete, null);
        builder.setView(v);
        dialog = builder.create();

        final TextView nameDetails = v.findViewById(R.id.update_product);
        final TextView quanDetails = v.findViewById(R.id.update_quantity);
        final TextView amountDetails = v.findViewById(R.id.update_amount);
        final TextView descDetails = v.findViewById(R.id.update_desc);
        final Button update = v.findViewById(R.id.updateBtn);
        Button delete = v.findViewById(R.id.deleteBtn);

        nameDetails.setText(expenseData.getProduct().trim());
        quanDetails.setText(String.valueOf(expenseData.getQuantity()).trim());
        amountDetails.setText(String.valueOf(expenseData.getAmount()));
        descDetails.setText(expenseData.getNote().trim());

        dialog.show();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String date = DateFormat.getDateInstance().format(new Date());

                String updatedProduct = nameDetails.getText().toString().trim();
                String updatedAmount = amountDetails.getText().toString().trim();
                String updatedQuan = quanDetails.getText().toString().trim();
                String updatedDesc = descDetails.getText().toString().trim();
                String updatedDate = date;


                ExpenseData expenseData = new ExpenseData();
                expenseData.setProduct(updatedProduct);
                expenseData.setQuantity(Integer.parseInt(updatedQuan));
                expenseData.setAmount(Integer.parseInt(updatedAmount));
                expenseData.setNote(updatedDesc);
                expenseData.setId(id);
                expenseData.setDate(updatedDate);

                database.child(id).setValue(expenseData);
                Toast.makeText(context.getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(id).removeValue();
                expenseDataList.remove(expenseData);
                notifyDataSetChanged();
                dialog.dismiss();


            }
        });

    }



    @Override
    public int getItemCount() {
        return expenseDataList.size();
    }



    private  class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateRecycler);
            name = itemView.findViewById(R.id.titleRecycler);
            quan = itemView.findViewById(R.id.quantityRecycler);
            price = itemView.findViewById(R.id.amountRecycler);

            editName = itemView.findViewById(R.id.update_product);
        }

    }
}
