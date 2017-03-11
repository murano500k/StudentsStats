package com.stc.demo.ss;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.stc.demo.ss.StudentContract.COURSE_COLUMN_NAME_courseId;
import static com.stc.demo.ss.StudentContract.COURSE_COLUMN_NAME_mark;

public class CircleStatsActivity extends AppCompatActivity implements StatsContract.View {
	private static final String TAG = "CircleStatsActivity";
	private StatsContract.Presenter presenter;
	private PieChart mChart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle_stats);
		mChart = (PieChart) findViewById(R.id.chart);
		new StatsPresenter(this, getContentResolver());
		show();
	}

	@Override
	public void setPresenter(StatsContract.Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public void showError(String e) {
		Log.e(TAG, "showError: " +e);
		Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
	}
	private void show(){
		presenter.getCoursesCursor()
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Cursor>() {
					@Override
					public void onSubscribe(Disposable d) {
						Log.d(TAG, "onSubscribe: ");
					}

					@Override
					public void onNext(Cursor value) {
						createMarksCircleDiagram(value);
					}

					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "onError: ",e );
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete: ");
					}
				});
	}

	public void createCoursesCircleDiagram( Cursor cCursor){
		List<PieEntry> entries=new ArrayList<>();
		int i = 0;

		while (cCursor.moveToNext()){
			String courseName = cCursor.getString(cCursor.getColumnIndex(COURSE_COLUMN_NAME_courseId));
			if(!isAdded(courseName, entries)) {
				entries.add(new PieEntry(1f, courseName));
				Log.d(TAG, "new " + courseName);
			}
			else {
				if(incrementValue(courseName,entries)) Log.d(TAG, courseName+" +1 success");
				else Log.e(TAG, courseName+" +1 ERROR");
			}
			i++;
		}

	}

	public void createMarksCircleDiagram( Cursor cCursor){
		HashMap<String, ArrayList<Integer>> courseMarksMap=new HashMap<>();
		List<PieEntry> entries=new ArrayList<>();


		while (cCursor.moveToNext()){
			String courseName = cCursor.getString(cCursor.getColumnIndex(COURSE_COLUMN_NAME_courseId));
			if(!courseMarksMap.containsKey(courseName)) {
				ArrayList<Integer> marks = new ArrayList<>();
				marks.add(0, 0);
				marks.add(1, 0);
				marks.add(2, 0);
				marks.add(3, 0);
				marks.add(4, 0);
				courseMarksMap.put(courseName, marks);
			}
			int mark = cCursor.getInt(cCursor.getColumnIndex(COURSE_COLUMN_NAME_mark));
			Log.d(TAG, "new mark "+mark);
			courseMarksMap.get(courseName).set(mark, courseMarksMap.get(courseName).get(mark)+1);
		}
		String name = "";
		if(courseMarksMap.keySet().iterator().hasNext()) name=courseMarksMap.keySet().iterator().next();
		int i = 0;
		for(Integer markCount : courseMarksMap.get(name)){
			i++;
			entries.add(new PieEntry(markCount, "mark "+i));
		}
		setupChart(entries, name);
	}
	private boolean incrementValue(String name, List<PieEntry>list){
		for(PieEntry entry: list){
			if(TextUtils.equals(entry.getLabel(), name)) {
				list.set(list.indexOf(entry), new PieEntry(entry.getValue()+1f,name));

				return true;
			}
		}
		return false;
	}
	private boolean isAdded(String name , List<PieEntry> list){
		for(PieEntry pieEntry: list){
			if(TextUtils.equals(pieEntry.getLabel(),name))return true;
		}
		return false;
	}

	private void setupChart(List<PieEntry> entries, String title){
		PieDataSet dataSet = new PieDataSet(entries, title);
		ArrayList<Integer> colors = new ArrayList<Integer>();

		for (int c : ColorTemplate.VORDIPLOM_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.JOYFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.COLORFUL_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.LIBERTY_COLORS)
			colors.add(c);

		for (int c : ColorTemplate.PASTEL_COLORS)
			colors.add(c);

		colors.add(ColorTemplate.getHoloBlue());

		dataSet.setColors(colors);

		PieData data = new PieData(dataSet);
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);
		mChart.setData(data);

		// undo all highlights
		mChart.highlightValues(null);

		mChart.invalidate();
	}
}
