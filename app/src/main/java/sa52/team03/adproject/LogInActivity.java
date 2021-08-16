package sa52.team03.adproject;

import android.content.Context;
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
import sa52.team03.adproject.CommonUtils.RetrofitAPI;
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

        Call<ResponseBody> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .loginUser(new User(username, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String token = response.headers().get("JwtToken");
                if(token!=null){
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("JwtToken", token);
                    editor.putString("username", username);
                    editor.commit();
                    startActivity(new Intent(LogInActivity.this,StudentMainActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(),"incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}