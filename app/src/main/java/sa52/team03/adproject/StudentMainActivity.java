package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StudentMainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupImageViews();
    }

    protected void setupImageViews(){
        int[] ids = {R.id.imgTakeAttendance, R.id.imgClassSchedule, R.id.imgCheckAttendance, R.id.imgSubmitLeave};

        for(int i=0; i<ids.length; i++){
            ImageView imgView = findViewById(ids[i]);
            if(imgView!=null){
                imgView.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id==R.id.imgTakeAttendance){

            Intent intent = new Intent(this, QRCodeActivity.class);
            startActivity(intent);

        }else if(id==R.id.imgClassSchedule){

            Intent intent = new Intent(this, ClassScheduleActivity.class);
            startActivity(intent);

        }else if (id==R.id.imgCheckAttendance){

            Intent intent = new Intent(this, CheckAttendanceActivity.class);
            startActivity(intent);

        }else if (id==R.id.imgSubmitLeave){
            Intent intent = new Intent(this, SubmitLeaveActivity.class);
            startActivity(intent);
        }

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
}