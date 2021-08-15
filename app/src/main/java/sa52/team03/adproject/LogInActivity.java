package sa52.team03.adproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.User;

public class LogInActivity extends AppCompatActivity {
    EditText et_username, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = findViewById(R.id.et_userName);
        et_password = findViewById(R.id.et_password);

        findViewById(R.id.logInBtn).setOnClickListener(view -> loginUser());

    }

    private void loginUser() {

        String userName = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (userName.isEmpty()) {
            et_username.setError("Username is required");
            et_username.requestFocus();
            return;
        } else if (password.isEmpty()) {
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;
        }

        Call<ResponseBody> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .loginUser(new User(userName, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}