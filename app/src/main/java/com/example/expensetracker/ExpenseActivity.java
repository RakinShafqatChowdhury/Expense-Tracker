package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.Adapter.RecyclerViewAdapter;
import com.example.expensetracker.Model.ExpenseData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private DatabaseReference database;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private TextView totalExpense;
    private List<ExpenseData> expenseDataArrayList;
    private TextView countItem;
    private Context c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        auth = FirebaseAuth.getInstance();
        expenseDataArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userid = user.getUid();
        database = firebaseDatabase.getReference().child("Expense List").child(userid);
        database.keepSynced(true);
        builder = new AlertDialog.Builder(this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        countItem = findViewById(R.id.itemCount);

        totalExpense = findViewById(R.id.total_expense);

        final DatabaseReference db= FirebaseDatabase.getInstance().getReference("Expense List").child(userid);

        db.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                if (dataSnapshot.exists()){
                    expenseDataArrayList = new ArrayList<>();
                    expenseDataArrayList.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()){

                        ExpenseData expenseData= d.getValue(ExpenseData.class);
                        expenseDataArrayList.add(expenseData);
                        //Log.e("array", "onDataChange: "+expenseData.getAmount() );
                        total = total+(expenseData.getAmount()*expenseData.getQuantity());
                    }

                    adapter=new RecyclerViewAdapter(expenseDataArrayList,ExpenseActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    countItem.setText(String.valueOf(expenseDataArrayList.size()));
                    totalExpense.setText(MessageFormat.format("{0} tk", String.valueOf(total)));
                }else{
                    totalExpense.setText("0 tk");
                    countItem.setText("0");
                    Toast.makeText(ExpenseActivity.this, "Empty list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(ExpenseActivity.this, "Something goes wrong", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar,menu);
        MenuItem menuItem = menu.findItem(R.id.searchView);

        final SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void search(String newText) {

        ArrayList<ExpenseData> filteredList = new ArrayList<>();
        for(ExpenseData expenseData : expenseDataArrayList){
            if(expenseData.getProduct().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(expenseData);
            }
        }

        adapter = new RecyclerViewAdapter(filteredList,c);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        countItem.setText(String.valueOf(filteredList.size()));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.signout:
                auth.signOut();
                startActivity(new Intent(ExpenseActivity.this,MainActivity.class));
                finish();
                break;

            case R.id.add_expense:
                AddExpense();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void AddExpense() {
        LayoutInflater inflater = LayoutInflater.from(ExpenseActivity.this);
        View v = inflater.inflate(R.layout.add_expense_popup,null);
        builder.setView(v);
        dialog = builder.create();
        dialog.show();

        final EditText product = v.findViewById(R.id.add_product);
        final EditText amount = v.findViewById(R.id.add_amount);
        final EditText quantity = v.findViewById(R.id.add_quantity);
        final EditText note = v.findViewById(R.id.add_desc);
        Button saveBtn = v.findViewById(R.id.save_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addedproduct = product.getText().toString().trim();
                String addedAmount = amount.getText().toString().trim();
                String addedNote = note.getText().toString().trim();
                String addedQuantity = quantity.getText().toString().trim();

                if(TextUtils.isEmpty(addedproduct)){
                    product.setError("Required");
                    return;
                }
                if(TextUtils.isEmpty(addedAmount)){
                    amount.setError("Required");
                    return;

                }


                if(TextUtils.isEmpty(addedQuantity)){
                    quantity.setError("Required");
                    return;
                }

                String id = database.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                ExpenseData expenseData = new ExpenseData();

                expenseData.setQuantity(Integer.parseInt(addedQuantity));
                expenseData.setAmount(Integer.parseInt(addedAmount));
                expenseData.setProduct(addedproduct);
                expenseData.setNote(addedNote);
                expenseData.setDate(date);
                expenseData.setId(id);



                database.child(id).setValue(expenseData);

                Toast.makeText(ExpenseActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
