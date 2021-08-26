package sa52.team03.adproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sa52.team03.adproject.CommonUtils.FaceUtil;
import sa52.team03.adproject.CommonUtils.RetrofitClient;
import sa52.team03.adproject.models.QRCodeData;

public class FaceAuthenticationActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private String mCameraId;
    private TextView tv_message;
    private Size mPreviewSize;
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String qrCodeText;

    private final Callback<ResponseBody> mFaceSearchCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
            try {
                assert response.body() != null;
                JSONObject result_json = new JSONObject(response.body().string());
                SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                String LoginUserId = pref.getString("userId", null);

                String GroupId = result_json.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getString("group_id");
                int userId = result_json.getJSONObject("result").getJSONArray("user_list").getJSONObject(0).getInt("user_id");

                if (!result_json.getString("error_code").equals("0")) {
                    runOnUiThread(() -> {
                        try {
                            tv_message.setText(result_json.getString("error_msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    mBackgroundHandler.postDelayed(() -> searchFace(), 1000);

                } else if (!LoginUserId.equals(String.valueOf(userId))) {
                    Toast.makeText(getApplicationContext(), "Sorry, face id authentication failed", Toast.LENGTH_SHORT).show();
                } else {
                    //Face Authentication Success
                    Toast.makeText(getApplicationContext(), "welcome " + GroupId + " " + userId, Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(FaceAuthenticationActivity.this, AttendanceSuccessActivity.class));
                    takeAttendance(qrCodeText);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            t.printStackTrace();
        }
    };

    private TextureView mTextureView;
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            setupCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    private CameraDevice mCameraDevice;
    private final CameraDevice.StateCallback mCameraDeviceStateListener = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private CaptureRequest.Builder mCaptureRequestBuilder;

    private final CameraCaptureSession.StateCallback mCaptureSessionStateListener = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                CaptureRequest previewCaptureRequest = mCaptureRequestBuilder.build();
                session.setRepeatingRequest(previewCaptureRequest, null, mBackgroundHandler);
                mBackgroundHandler.postDelayed(() -> searchFace(), 1000);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Toast.makeText(getApplicationContext(),
                    "Unable to setup camera preview", Toast.LENGTH_SHORT).show();
        }
    };

    private void setupCamera(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = getOptimalSize(map.getOutputSizes(ImageFormat.JPEG), width, height);
                mCameraId = cameraId;
                break;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                manager.openCamera(mCameraId, mCameraDeviceStateListener, mBackgroundHandler);
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "app required access to camera", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        SurfaceTexture mSurfaceTexture = mTextureView.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface mSurface = new Surface(mSurfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(mSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(mSurface), mCaptureSessionStateListener, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void searchFace() {

        Bitmap bitmap = mTextureView.getBitmap();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int cropSize = Math.min(w, h);

        bitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropSize) / 2,
                (bitmap.getHeight() - cropSize) / 2, cropSize, cropSize);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String stringBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        try {
            FaceUtil.GetAuthenticationCall(stringBase64).enqueue(mFaceSearchCallback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("CameraThread");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Size getOptimalSize(@NonNull Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() * 3 == option.getHeight() * 4 && option.getHeight() * 4 >= height * 3) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() * 3 == option.getHeight() * 4 && option.getWidth() * 4 >= height * 3) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, (Size1, Size2) -> Long.signum(Size1.getWidth() * Size1.getHeight() - Size2.getWidth() * Size2.getHeight()));
        }
        return sizeMap[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_authentication);

        tv_message = findViewById(R.id.tv_message);
        mTextureView = findViewById(R.id.textureView);

        findViewById(R.id.btn_cancel).setOnClickListener(v -> startActivity(new Intent(FaceAuthenticationActivity.this, StudentMainActivity.class)));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        qrCodeText = getIntent().getStringExtra("qrCodeText");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
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
                Intent intent = new Intent(this, LogInActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void takeAttendance(String qrCodeText) {

        String[] qrCodeData = qrCodeText.split("_");

        SharedPreferences sharedPref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String userName = sharedPref.getString("username", "");

        //QRCodeData(String studentUserName, String signInSignOutId, int scheduleId, String option)
        QRCodeData qrCode = new QRCodeData(userName, qrCodeData[0], Integer.parseInt(qrCodeData[1]), qrCodeData[2]);

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String token = pref.getString("JwtToken", null);

        Call<ResponseBody> call = RetrofitClient
                .getServerInstance()
                .getAPI()
                .takeAttendance(token, qrCode);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful()) {

                    //What to show if not successfully?
                    Intent intent = new Intent(FaceAuthenticationActivity.this, AttendanceFailureActivity.class);
                    startActivity(intent);
                    return;
                }

                Intent intent = new Intent(FaceAuthenticationActivity.this, AttendanceSuccessActivity.class);
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