package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.AttendanceSuccessData;
import sa52.team03.adproject.models.QRCodeData;

public class AttendanceSuccessActivity extends AppCompatActivity {

    TextView txtStudentId;
    TextView txtSubmissionTime;
    TextView txtModuleCode;
    TextView txtClassDateTime;
    Button btnBack;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_success);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtStudentId = findViewById(R.id.txtStudentId);
        txtSubmissionTime = findViewById(R.id.txtSubmissionTime);
        txtModuleCode = findViewById((R.id.txtModuleCode));
        txtClassDateTime = findViewById(R.id.txtClassDateTime);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendanceSuccessActivity.this, StudentMainActivity.class));
            }
        });

        setAttendanceSuccessData(getIntent().getStringExtra("qrCodeText"));

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

    public void setAttendanceSuccessData(String qrCodeText){

        String[] qrCodeData = qrCodeText.split("_");

        SharedPreferences sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String userName = sharedPref.getString("username", "");

        //QRCodeData(String studentUserName, String signInSignOutId, int scheduleId, String option)
        QRCodeData qrCode = new QRCodeData(userName, qrCodeData[0], Integer.parseInt(qrCodeData[1]), qrCodeData[2]);

        SharedPreferences pref = getSharedPreferences("user_credentials",MODE_PRIVATE);
        String token = pref.getString("JwtToken", null);

        Call<AttendanceSuccessData> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .getAttendanceSuccessData(token, qrCodeText);

        call.enqueue(new Callback<AttendanceSuccessData>() {
            @Override
            public void onResponse(Call<AttendanceSuccessData> call, Response<AttendanceSuccessData> response) {

                if (response.body() == null) {

                } else {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    txtStudentId.setText(response.body().getStudentId());
                    txtSubmissionTime.setText(response.body().getSubmissionTime());
                    txtModuleCode.setText(response.body().getModuleCode());
                    txtClassDateTime.setText(response.body().getClassDateTime());
                }

            }

            @Override
            public void onFailure(Call<AttendanceSuccessData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}