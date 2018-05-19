package com.example.android.waitlist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.waitlist.data.WaitlistContract.*;

//This class extends the SQLiteOpenHelper
public class WaitlistDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "waitlist.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public WaitlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create table String for waitlist
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + "IF NOT EXISTS " + WaitlistEntry.TABLE_NAME +  " (" +
                WaitlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WaitlistEntry.COLUMN_GUEST_NAME + " TEXT NOT NULL, " +
                WaitlistEntry.COLUMN_PARTY_SIZE + " INTEGER NOT NULL, " +
                WaitlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";


        // Create table String for orders table
        final String SQL_CREATE_ORDERS_TABLE = "CREATE TABLE IF NOT EXISTS ORDERS (ID INTEGER , DISHNAME TEXT DEFAULT ' ' ," +
                "  QTY INTEGER DEFAULT 1 ,  FOREIGN KEY(ID) REFERENCES waitlist(_id)); ";

        //Create table string for ordershistory table
        final String SQL_CREATE_ORDERSHISTORY_TABLE = "CREATE TABLE IF NOT EXISTS ORDERSHISTORY(ID INTEGER , DISHNAME TEXT DEFAULT ' ' ," +
                "  QTY INTEGER DEFAULT 1 ,  FOREIGN KEY(ID) REFERENCES waitlist(_id)); ";

        //Create table string for the customer_feedback table
        final String SQL_CREATE_CUSTIMER_FEEDBACK_TABLE = "CREATE TABLE IF NOT EXISTS CUSTOMER_FEEDBACK(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "NAME TEXT NOT NULL,"
                +"PHONE_NO INTEGER ,"
                +"FOOD_RATING FLOAT CHECK(FOOD_RATING <= 5),"
                +"SERVICE_RATING FLOAT CHECK(FOOD_RATING <= 5),"
                +"AMBIENCE_RATING FLOAT CHECK(FOOD_RATING <= 5),"
                +"FEEDBACK TEXT );";

        //Creating a trigger to delete record from orders table also
        // when a customer is deleted from the waitlist table
        String triggerSql = "create trigger T1 "
                + "after delete on waitlist "
                + "Begin "
                + "delete from orders where id = old._id ; "
                + "End";

        //A delete trigger to keep a record of dishes ordered by customers
        String triggerSql2 = "create trigger T2 "
                + "before delete on orders "
                + "Begin "
                + "insert into ordershistory values (old.id , old.dishname , old.qty );  "
                + " End ";

        //Creating index for timestamp to query faster when we put timestamp after the where clause
        // INSERT, UPDATE and DELETE becomes slower because on each operation the indexes mst also be updated
        // we would use timestamp to show notifications for customers waiting for more than 15 minutes
        String sqlIndex = "create index I1 on waitlist(timestamp)";

        //Creating a view here
        String viewSql = "create view V1 As select waitlist._id,waitlist.timestamp,orders.dishname,orders.qty"
                + " from waitlist , orders";
        String viewSql2= "create view V2 As select * from waitlist,orders";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDERSHISTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CUSTIMER_FEEDBACK_TABLE);
        sqLiteDatabase.execSQL(triggerSql);
        sqLiteDatabase.execSQL(triggerSql2);
        sqLiteDatabase.execSQL(sqlIndex);
        sqLiteDatabase.execSQL(viewSql);
        sqLiteDatabase.execSQL(viewSql2);

    }


    //When we upgrade the database to a new version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one This means if you change the
        // DATABASE_VERSION the table will be dropped
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data of user is not deleted
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WaitlistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS orders");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ordershistory");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS CUSTOMER_FEEDBACK " );
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS V1 " );
        sqLiteDatabase.execSQL("DROP VIEW IF EXISTS V2 " );

        onCreate(sqLiteDatabase);


    }
}