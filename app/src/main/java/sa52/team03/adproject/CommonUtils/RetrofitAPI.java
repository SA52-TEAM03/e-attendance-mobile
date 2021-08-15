package sa52.team03.adproject.CommonUtils;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sa52.team03.adproject.models.User;

public interface RetrofitAPI {
    @HTTP(method = "POST", path = "search", hasBody = true)
    @Headers({"Content-Type:application/json"})
    Call<ResponseBody> faceSearch(@Query("access_token") String token, @Body RequestBody body);

    @POST("login")
    Call<ResponseBody> loginUser (
            @Body User user
    );
}
