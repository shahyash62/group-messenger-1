package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by shahy on 2/22/2018.
 */

public class insertData {

    private static final String TAG = OnPTestClickListener.class.getName();
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    //private final TextView mTextView;
    private final ContentResolver mContentResolver;
    private final Uri mUri;

    public insertData(ContentResolver _cr) {
        //mTextView = _tv;
        mContentResolver = _cr;
        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
    }

    /**
     * buildUri() demonstrates how to build a URI for a ContentProvider.
     *
     * @param scheme
     * @param authority
     * @return the URI
     */
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    /**
     * testInsert() uses ContentResolver.insert() to insert values into your ContentProvider.
     *
     * @return true if the insertions were successful. Otherwise, false.
     */
    public boolean insert(int count, String message) {
        ContentValues mNewValues = new ContentValues();
        mNewValues.put("key", Integer.toString(count));
        mNewValues.put("val", message);
        try {
            mContentResolver.insert(mUri, mNewValues);
            Log.i(TAG, "insert: inserted in db ez gg");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     * testQuery() uses ContentResolver.query() to retrieves values from your ContentProvider.
     * It simply queries one key at a time and verifies whether it matches any (key, value) pair
     * previously inserted by testInsert().
     *
     * Please pay extra attention to the Cursor object you return from your ContentProvider.
     * It should have two columns; the first column (KEY_FIELD) is for keys
     * and the second column (VALUE_FIELD) is values. In addition, it should include exactly
     * one row that contains a key and a value.
     *
     * @return
     */
}
