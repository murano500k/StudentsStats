package com.stc.demo.ss;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements StatsContract.View{

	private SimpleCursorAdapter adapter;
	public static final int STUDENTS_LOADER_ID = 78; // From docs: A unique identifier for this loader. Can be whatever you want.
	StatsContract.Presenter presenter;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupCursorAdapter();

	}
	private void setupCursorAdapter() {
		// Column data from cursor to bind views from
		String[] uiBindFrom = { "name",
				"lastname", "birthday"};
		// View IDs which will have the respective column data inserted
		int[] uiBindTo = { R.id.name, R.id.lastname, R.id.bd};
		// Create the simple cursor adapter to use for our list
		// specifying the template to inflate (item_contact),
		adapter = new SimpleCursorAdapter(
				this, R.layout.student_entry,
				null, uiBindFrom, uiBindTo,
				0);

		ListView listView = (ListView) findViewById(R.id.lv);
		listView.setAdapter(adapter);
		new StatsPresenter(this,getContentResolver());
	}

	@Override
	public void setPresenter(StatsContract.Presenter presenter) {
		this.presenter=presenter;
		Log.d(TAG, "setPresenter: ");
		showStudents(presenter.getStudentsCursor());
	}

	@Override
	public void showError(String e) {
		Log.e(TAG, "showError: " +e);
	}

	public void showStudents(io.reactivex.Observable<Cursor> cursorObservable) {
		if(cursorObservable==null) {
			showError("null");
			return;
		}
		cursorObservable.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Cursor>() {
					@Override
					public void onSubscribe(Disposable d) {
						Log.d(TAG, "onSubscribe: ");
					}

					@Override
					public void onNext(Cursor value) {
						Log.d(TAG, "onNext: "+value);

						if(value==null) {
							showError("null2");
							return;
						}
						adapter.swapCursor(value);

					}

					@Override
					public void onError(Throwable e) {
						showError(e.getMessage());
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete: ");
					}
				});
	}

}
