package com.example.android.waitlist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;

    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To not focus on anything when the app is opened
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing Stetho Library used to view thw databases in the app
        Stetho.initializeWithDefaults(this);

        final RecyclerView waitlistRecyclerView;

        // Set local attributes to corresponding views
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        mNewGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = (EditText) this.findViewById(R.id.party_count_edit_text);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);

        //SWIPE LEFT OR RIGHT TO DELETE FROM DB

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long)viewHolder.itemView.getTag();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
            }
        }).attachToRecyclerView(waitlistRecyclerView);

        waitlistRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                waitlistRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                //Values are passing to the orders activity

                TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
                String guestName = nameTextView.getText().toString();
                TextView partySizeTextView = (TextView) view.findViewById(R.id.party_size_text_view);
                String partySize = partySizeTextView.getText().toString();

                Cursor cursor1= mDb.rawQuery("select _id from waitlist where guestName = ? and partySize = ?",new String[]{guestName , partySize});
                int _id = 0;
                if(cursor1.moveToFirst()){

                    _id = cursor1.getInt(cursor1.getColumnIndex("_id"));
                }
                else{
                    Toast.makeText(MainActivity.this, "Cursor1 move to first not working", Toast.LENGTH_SHORT).show();
                }

                Intent order_intent = new Intent(MainActivity.this,OrderActivity.class);
                order_intent.putExtra("ID of the Current user" ,_id);
                startActivity(order_intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                /*Toast.makeText(MainActivity.this, "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();*/
            }
        }));

    }

    public void openFeedbackActivity(View view){

        Intent Customer_feedback = new Intent(MainActivity.this,CustomerFeedback.class);
        startActivity(Customer_feedback);

    }

    //This method is called when user clicks on the ADD button
    public void addToWaitlist(View view) {
        if (mNewGuestNameEditText.getText().length() == 0 ||
                mNewPartySizeEditText.getText().length() == 0) {
            return;
        }
        //default party size to 1
        int partySize = 1;
        try {
            //mNewPartyCountEditText inputType="number", so this should work
            partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }

        // Add guest info to mDb
        addNewGuest(mNewGuestNameEditText.getText().toString(), partySize);

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

        //clear UI text fields
        mNewPartySizeEditText.clearFocus();
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();

        //Toast.makeText(this, "Swipe Right to Delete Entry", Toast.LENGTH_SHORT).show();
    }



   //Query the mDb and get all guests from the waitlist table

    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

     //name  Guest's name
     //partySize Number in party
     //id of new record added
     //Adds a new guest to the mDb including the party count and the current timestamp

    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);

        long l = mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);


        //GETTING OUT CURRENT ID FROM waitlist TABLE

        String strx ="SELECT _id from waitlist where guestName ='" + name + "' AND" +
                " " + "partySize=" + partySize
                +  ";";
        Cursor xcursor= mDb.rawQuery(strx,null);
        int ID_CURRENT_USER=0;

        if(xcursor != null) {
            if(xcursor.moveToFirst())
            {
                ID_CURRENT_USER = xcursor.getInt(xcursor.getColumnIndex("_id"));

            }
            else{
                Toast.makeText(this, "Move to first not working", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "cursor is null", Toast.LENGTH_SHORT).show();
        }
        xcursor.close();

        //ENDING THE GETTING OF CURRENT ID

        /*String str= "INSERT INTO ORDERS(ID) VALUES ('" + ID_CURRENT_USER + "')";
        mDb.execSQL(str);*/

        return l;
    }

    private boolean removeGuest(long id){

        mDb.execSQL("delete from waitlist where _id= ?",new String[]{String.valueOf(id)});
        return true;
    }
}



