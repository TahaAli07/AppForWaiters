package com.example.android.waitlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android.waitlist.data.WaitlistDbHelper;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private EditText mItemNameEditText;
    private EditText mQtyEditText;
    private ListView mListView;

    private Button mButton;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mItemNameEditText=(EditText) findViewById(R.id.OrderPage_itemName_edittext);
        mQtyEditText=(EditText)findViewById(R.id.OrderPage_Qty_Edittext);
        mListView=(ListView) findViewById(R.id.OrderPageListView);
        mButton=(Button) findViewById(R.id.OrderPage_Button);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Order Page");

        final Integer ID_CURRENT_USER = getIntent().getExtras().getInt("ID of the Current user");
/*
        Toast.makeText(this, ID_CURRENT_USER.toString(), Toast.LENGTH_SHORT).show();
*/

        //Getting referene to the database
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb= dbHelper.getWritableDatabase();


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                WriteEditTextToDB(ID_CURRENT_USER,mItemNameEditText,mQtyEditText);

                //clear UI text fields
                mItemNameEditText.clearFocus();
                mQtyEditText.clearFocus();
                mItemNameEditText.getText().clear();
                mQtyEditText.getText().clear();


            }
        });

        updateListView();
    }

    public void  updateListView(){

        final Integer ID_CURRENT_USER = getIntent().getExtras().getInt("ID of the Current user");
/*
        Toast.makeText(this, ID_CURRENT_USER.toString(), Toast.LENGTH_SHORT).show();
*/

        //Getting cursor(pointer) to records matching with id
        Cursor mCursor=getItemCursor(ID_CURRENT_USER.toString());

        if(mCursor != null) {

            Log.d("debug cursor count", String.valueOf(mCursor.getCount()));
            String[] values = new String[mCursor.getCount()];
            int counter =0;

//            LinkedList<String>  linkedList = new LinkedList<String>();

            while(mCursor.moveToNext())
            {
                String dishname = mCursor.getString(mCursor.getColumnIndex("DISHNAME"));
                Log.d("debug dishname",dishname);
                Integer qty = mCursor.getInt(mCursor.getColumnIndex("QTY"));
                Log.d("debug qty",String.valueOf(qty));

//                linkedList.add(dishname);

                values[counter]=dishname + " - " + String.valueOf(qty);

                /*//Update the circle xml here
                mQtyEditText.setText(String.valueOf(qty));*/

                counter++;
            }

            for(int i=0;i<mCursor.getCount();i++) {
                Log.d("debug VALUES", values[i]);
            }
            ArrayList<String> List = new ArrayList<>();
            List.addAll( Arrays.asList(values) );

            adapter = new ArrayAdapter<>(OrderActivity.this,R.layout.order_list_item,R.id.OrderPage_Item_name_TextView, List);

            //Setting adapter for the listview
            mListView.setAdapter(adapter);

        }
        else{
            Toast.makeText(this, "cursor is null", Toast.LENGTH_SHORT).show();
        }
        mCursor.close();
    }

    private Cursor getItemCursor(String ID) {
        // String for select command on the orders table
        String str = "Select * from Orders where ID  = "+ ID ;

        return mDb.rawQuery(str,null);

    }

    public void WriteEditTextToDB(Integer ID_CURRENT_USER,EditText DishName, EditText qty ){

        if (DishName.getText().length()!=0 && qty.getText().length()!=0 ) {

            /*String str = "UPDATE ORDERS SET " +  "DISHNAME" + qty.getText().toString() + "= '" + DishName.getText().toString()+
                    "'WHERE ID = "+ ID_CURRENT_USER.toString() +";";*/

            //inserting values in the table
            String str = "INSERT INTO ORDERS VALUES ( "
                    + ID_CURRENT_USER
                    +" , '" + DishName.getText().toString()
                    +" ' , " + qty.getText().toString()
                    + ");";
            mDb.execSQL(str);
            Toast.makeText(this, "DATA INSERTED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "EMPTY VALUES ARE NOT ALLOWED", Toast.LENGTH_SHORT).show();
        }

        updateListView();
    }



}