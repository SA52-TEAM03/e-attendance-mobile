package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.ClassModule;

public class CheckAttendanceActivity extends AppCompatActivity {

    private CheckAttendanceListViewAdapter mAdapter;
    private ListView listView;
    private List<ClassModule> classModules = new ArrayList<>();
    private TextView titleTextview, alertTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getClassModule();


        alertTextView = findViewById(R.id.alert);
        mAdapter = new CheckAttendanceListViewAdapter(this, classModules, alertTextView);
        listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setAdapter(mAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.logout:
                SharedPreferences sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(this,LogInActivity.class);
                startActivity(intent);
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
                titleTextview = findViewById(R.id.title);
                if (response.body() == null) {
                    titleTextview.setText("There is no class enrolment!");
                } else {
                    titleTextview.setText("Class Attendance Rate Until Now.");
                    mAdapter.setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ClassModule>> call, Throwable t) {

            }
        });
    }

}