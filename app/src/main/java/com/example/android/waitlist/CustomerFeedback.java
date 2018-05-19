package com.example.android.waitlist;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.android.waitlist.data.WaitlistDbHelper;

public class CustomerFeedback extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mNumberEditText;
    private EditText mFeedbackEditText;

    private RatingBar mFoodRatingBar;
    private RatingBar mServiceRatingBar;
    private RatingBar mAmbienceRatingBar;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feedback);



        mNameEditText=(EditText) findViewById(R.id.CustomerFeedback_name_EditText);
        mNumberEditText=(EditText)findViewById(R.id.CustomerFeedback_phone_EditText);
        mFeedbackEditText=(EditText)findViewById(R.id.CustomerFeedback_feedback_EditText);

        mFoodRatingBar=(RatingBar) findViewById(R.id.CustomerFeedback_Food_RatingBar);
        mServiceRatingBar=(RatingBar) findViewById(R.id.CustomerFeedback_Service_RatingBar);
        mAmbienceRatingBar=(RatingBar) findViewById(R.id.CustomerFeedback_Ambience_RatingBar);


    }

    public void onSumbit(View view){

        String name = mNameEditText.getText().toString();
        String number = mNumberEditText.getText().toString();
        String feedback = mFeedbackEditText.getText().toString();

        float food = mFoodRatingBar.getRating();
        float service = mServiceRatingBar.getRating();
        float ambience = mAmbienceRatingBar.getRating();

        /*final String SQL_CREATE_CUSTIMER_FEEDBACK_TABLE = "CREATE TABLE IF NOT EXISTS CUSTOMER_FEEDBACK(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "NAME TEXT NOT NULL,"
                +"PHONE_NO INTEGER ,"
                +"FOOD_RATING INTEGER CHECK(FOOD_RATING < 5),"
                +"SERVICE_RATING INTEGER CHECK(FOOD_RATING < 5),"
                +"AMBIENCE_RATING INTEGER CHECK(FOOD_RATING < 5),"
                +"FEEDBACK TEXT );";*/

        //Getting referene to the database
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb= dbHelper.getWritableDatabase();

        mDb.execSQL("insert into CUSTOMER_FEEDBACK(name,phone_no,food_rating,service_rating,ambience_rating,feedback) values('"
                +name+"',"
                +number+","
                +food+","
                +service+","
                +ambience+",'"
                +feedback+"');");

        Toast.makeText(this, "Your Response has been recorded", Toast.LENGTH_SHORT).show();

        finish();

        Intent intent = new Intent( CustomerFeedback.this,MainActivity.class);
        startActivity(intent);

    }


}
