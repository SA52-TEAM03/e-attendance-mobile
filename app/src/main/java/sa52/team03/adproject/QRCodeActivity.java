package sa52.team03.adproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.QRCodeData;
import sa52.team03.adproject.models.User;

public class QRCodeActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView mCodeScannerView;
    private Button btnCancel;
    private int REQUEST_CAMERA = 1000001;
    private TextView txtQRCodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnCancel = findViewById(R.id.btnQRScanCancel);

        txtQRCodeData = findViewById(R.id.textQRCode);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            startScanning();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRCodeActivity.this, StudentMainActivity.class));
            }
        });

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

    private void startScanning() {

        mCodeScannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, mCodeScannerView);
        mCodeScanner.startPreview();   // this line is very important, as you will not be able to scan your code without this, you will only get blank screen
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        validateQRCode(result.getText());
                    }
                });
            }
        });

        mCodeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                startScanning();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void validateQRCode(String qrCodeText){

        String[] qrCodeData = qrCodeText.split("_");

        SharedPreferences sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String userName = sharedPref.getString("username", "");

        //QRCodeData(String studentUserName, String signInSignOutId, int scheduleId, String option)
        QRCodeData qrCode = new QRCodeData(userName, qrCodeData[0], Integer.parseInt(qrCodeData[1]), qrCodeData[2]);


        SharedPreferences pref = getSharedPreferences("user_credentials",MODE_PRIVATE);
        String token = pref.getString("JwtToken", null);

        Call<ResponseBody> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .scanQRCode(token, qrCode);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(!response.isSuccessful()){
                    txtQRCodeData.setText("QR Code Expired or Wrong QR Code!");
                    return;
                }

                Intent intent = new Intent(QRCodeActivity.this, FaceAuthenticationActivity.class);
                intent.putExtra("qrCodeText", qrCodeText);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}