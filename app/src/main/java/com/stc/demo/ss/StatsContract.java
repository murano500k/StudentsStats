package com.stc.demo.ss;

import android.database.Cursor;

/**
 * Created by artem on 3/1/17.
 */

public interface StatsContract {
	interface View {
		void setPresenter(Presenter presenter);

		void showError(String e);

	}
	interface Presenter {
		io.reactivex.Observable<Cursor> getCoursesCursor();
		io.reactivex.Observable<Cursor> getStudentsCursor();
	}
}
