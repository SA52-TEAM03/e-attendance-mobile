package sa52.team03.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

        findViewById(R.id.logInBtn).setOnClickListener(view -> Login());
        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        if (pref.contains("username") && pref.contains("password")) {
            Login(pref.getString("username", ""),
                    pref.getString("password", ""));
        }
    }

    private void Login(String username, String password) {

        Call<ResponseBody> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .loginUser(new User(username, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String token = response.headers().get("JwtToken");
                if (token != null) {
                    SharedPreferences sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("JwtToken", token);
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();
                    startActivity(new Intent(LogInActivity.this, StudentMainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Login() {

        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (username.isEmpty()) {
            et_username.setError("Username is required");
            et_username.requestFocus();
            return;
        } else if (password.isEmpty()) {
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;
        }

        Login(username, password);
    }

}