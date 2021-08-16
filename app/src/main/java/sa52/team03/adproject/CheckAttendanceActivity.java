package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;

public class CheckAttendanceActivity extends AppCompatActivity {

    private CheckAttendanceListViewAdapter mAdapter;
    private ListView listView;
    private Map<String, List<Integer>> studentAttendance = new HashMap<>();
    private TextView titleTextview, alertTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getAttendance();

        alertTextView = findViewById(R.id.alert);
        mAdapter = new CheckAttendanceListViewAdapter(this,studentAttendance,alertTextView);
        listView = (ListView) findViewById(R.id.listView);
        if (listView!=null){
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAttendance() {

        String userName = "student1@email.com";

        Call<Map<String, List<Integer>>> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .getAttendance(userName);

        call.enqueue(new Callback<Map<String, List<Integer>>>() {
            @Override
            public void onResponse(Call<Map<String, List<Integer>>> call, Response<Map<String, List<Integer>>> response) {
                titleTextview = findViewById(R.id.title);
                if (response.body()==null){
                    titleTextview.setText("There is no class enrolment!");
                }
                else{
                    titleTextview.setText("Class Attendance Rate Until Now.");
                    mAdapter.setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<Map<String, List<Integer>>> call, Throwable t) {

            }
        });

    }
}