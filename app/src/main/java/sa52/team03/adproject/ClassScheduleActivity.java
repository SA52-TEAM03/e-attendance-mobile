package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.LocalDate;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.ClassModule;


public class ClassScheduleActivity extends AppCompatActivity {

    private MaterialCalendarView mCalendarView;
    private TextView mModuleCode, mModuleName, mClassLecturer, mClassDate;
    private List<ClassModule> classModules = new ArrayList<>();
    private final String[] colorString = new String[]{"#B8860B", "#9370DB", "#FFA500", "#008000", "#FF0000" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_schedule);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCalendarView = findViewById(R.id.calendarView);
        mModuleCode = findViewById(R.id.moduleCode);
        mModuleName = findViewById(R.id.moduleName);
        mClassLecturer = findViewById(R.id.classLecturer);
        mClassDate = findViewById(R.id.classDate);

        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Boolean hasCalss = false;
                for(ClassModule cm : classModules){
                    for (String strDate : cm.getSchedules()){
                        if (date.getDate().isEqual(LocalDate.parse(strDate))){
                            mModuleCode.setText("Module Code : " + cm.getModuleCode());
                            mModuleName.setText("Module Name : " + cm.getModuleName());
                            mClassLecturer.setText("Lecturer : "  + cm.getLecturerName());
                            mClassDate.setText("Date : " + strDate);
                            hasCalss = true;
                        }
                    }
                }
                if(!hasCalss){
                    mModuleCode.setText("");
                    mModuleName.setText("");
                    mClassLecturer.setText("");
                    mClassDate.setText("");
                }
            }
        });

        mCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mModuleCode.setText("");
                mModuleName.setText("");
                mClassLecturer.setText("");
                mClassDate.setText("");
            }
        });

        getClassModule();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getClassModule() {

        SharedPreferences pref = getSharedPreferences("user_credentials",MODE_PRIVATE);
        String token = pref.getString("JwtToken", null);

        Call<List<ClassModule>> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .getClassModule(token);

        call.enqueue(new Callback<List<ClassModule>>() {
        @Override
        public void onResponse(Call<List<ClassModule>> call, Response<List<ClassModule>> response) {
            classModules = response.body();
            for (int i = 0; i < classModules.size(); i++) {
                Collection<CalendarDay> dates = new ArrayList<>();
                for (String strDate : classModules.get(i).getSchedules()) {
                        LocalDate date = LocalDate.parse(strDate);
                        CalendarDay day = CalendarDay.from(date);
                        dates.add(day);
                }
                if (i>colorString.length-1) {
                    EventDecorator eventDecorator = new EventDecorator(Color.parseColor(colorString[i - 5]), dates);
                }
                EventDecorator eventDecorator = new EventDecorator(Color.parseColor(colorString[i]),dates);
                mCalendarView.addDecorator(eventDecorator);
            }
        }

        @Override
        public void onFailure(Call<List<ClassModule>> call, Throwable t) {

        }
    });

    }

}