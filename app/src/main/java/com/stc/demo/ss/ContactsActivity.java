package com.stc.demo.ss;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class ContactsActivity extends AppCompatActivity {
	private static final int LOADER_ID = 323;
	private static final String TAG = "ContactsActivity";
	private static final int REQUEST_READ_CONTACTS = 4326;
	ListView lv;
	CursorAdapter adapter;
	private LoaderManager.LoaderCallbacks<Cursor> contactsLoader= new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projectionFields = new String[] { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME,
					ContactsContract.Contacts.PHOTO_URI };
			// Construct the loader
			CursorLoader cursorLoader = new CursorLoader(ContactsActivity.this,
					ContactsContract.Contacts.CONTENT_URI, // URI
					projectionFields, // projection fields
					null, // the selection criteria
					null, // the selection args
					null // the sort order
			);
			// Return the loader for use
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			adapter.swapCursor(data);

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		lv=(ListView) findViewById(R.id.lv);
		setupCursorAdapter();
		lv.setAdapter(adapter);
		start();
	}

	private void start(){
		if(ActivityCompat.checkSelfPermission(this,READ_CONTACTS)!=PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
			return;
		}
		getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), contactsLoader);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(grantResults[0]==PERMISSION_GRANTED) {
			Log.d(TAG, "onRequestPermissionsResult: granted");
			start();
		}else {
			Toast.makeText(this, "NOT GRANTED", Toast.LENGTH_SHORT).show();
		}

	}

	private void setupCursorAdapter() {
		String[] uiBindFrom = { ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.PHOTO_URI };
		// View IDs which will have the respective column data inserted
		int[] uiBindTo = { R.id.tv, R.id.iv};
		// Create the simple cursor adapter to use for our list
		// specifying the template to inflate (item_contact),
		adapter = new SimpleCursorAdapter(
				this, R.layout.item_contact,
				null, uiBindFrom, uiBindTo,
				0);
	}
}
