package com.example.android.waitlist.data;

import android.provider.BaseColumns;


//This class will help us implement changes , as any changes done here will reflect Everywhere
// Eg. if we want to change the name of the columns
//This is the model class

public class WaitlistContract {

    public static final class WaitlistEntry implements BaseColumns {

        public static final String TABLE_NAME = "waitlist";
        public static final String COLUMN_GUEST_NAME = "guestName";
        public static final String COLUMN_PARTY_SIZE = "partySize";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }

}
