package com.stc.demo.ss;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

import static com.stc.demo.ss.StudentContract.BASE_DB_URI;
import static com.stc.demo.ss.StudentContract.COURSES_TABLE_NAME;
import static com.stc.demo.ss.StudentContract.DB_NAME;
import static com.stc.demo.ss.StudentContract.STUDENTS_TABLE_NAME;

/**
 * Created by artem on 3/1/17.
 */

public class StatsPresenter implements StatsContract.Presenter {

	ContentResolver resolver;
	StatsContract.View v;

	public StatsPresenter(StatsContract.View v, ContentResolver resolver) {
		this.v = v;
		this.resolver=resolver;
		v.setPresenter(this);
	}

	@Override
	public Observable<Cursor> getStudentsCursor() {
		return getCursorObs(STUDENTS_TABLE_NAME);
	}
	@Override
	public Observable<Cursor> getCoursesCursor() {
		return getCursorObs(COURSES_TABLE_NAME);
	}



	private Observable<Cursor> getCursorObs(String path){
		return io.reactivex.Observable.fromCallable(new Callable<Cursor>() {
			@Override
			public Cursor call() throws Exception {
				Uri dbURI = Uri.parse(BASE_DB_URI+DB_NAME);
				Uri studentsURI = Uri.parse(BASE_DB_URI+path);
				ContentProviderClient yourCR = resolver.acquireContentProviderClient(dbURI);
				Cursor yourCursor = yourCR.query(studentsURI, null, null, null, null);
				yourCR.release();
				return yourCursor;
			}
		});
	}/*
	private Observable<Cursor> getStudentCoursesObs(String path){
		return io.reactivex.Observable.fromCallable(new Callable<Cursor>() {
			@Override
			public Cursor call() throws Exception {
				Uri dbURI = Uri.parse(BASE_DB_URI+DB_NAME);
				Uri studentsURI = Uri.parse(BASE_DB_URI+path);
				ContentProviderClient yourCR = resolver.acquireContentProviderClient(dbURI);
				Cursor yourCursor = yourCR.query(studentsURI, null, null, null, null);
				yourCR.release();
				return yourCursor;
			}
		});
	}*/
}
