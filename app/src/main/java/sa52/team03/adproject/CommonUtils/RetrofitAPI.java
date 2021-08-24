package sa52.team03.adproject.CommonUtils;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sa52.team03.adproject.models.AttendanceSuccessData;
import sa52.team03.adproject.models.ClassModule;
import sa52.team03.adproject.models.QRCodeData;
import sa52.team03.adproject.models.User;

public interface RetrofitAPI {

    @HTTP(method = "POST", path = "search", hasBody = true)
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> faceSearch(@Query("access_token") String token, @Body RequestBody body);

    @HTTP(method = "POST", path = "faceset/user/add", hasBody = true)
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> faceRegister(@Query("access_token") String token, @Body RequestBody body);

    @HTTP(method = "POST",path="token", hasBody = true)
    Call<ResponseBody> loginUser(@Body User user);

    @GET("api/student/class-module")
    Call<List<ClassModule>> getClassModule(@Header("JwtToken") String token);

    @HTTP(method = "POST",path="api/student/scanQRCode", hasBody = true)
    Call<ResponseBody> scanQRCode(@Header("JwtToken") String token, @Body QRCodeData qrCodeData);

    @HTTP(method = "POST",path="api/student/takeAttendance", hasBody = true)
    Call<ResponseBody> takeAttendance(@Header("JwtToken") String token, @Body QRCodeData qrCodeData);

    @GET("api/student/getAttendanceSuccess/{qrCodeData}")
    Call<AttendanceSuccessData> getAttendanceSuccessData(@Header("JwtToken") String token, @Path("qrCodeData") String qrCodeData);
}
