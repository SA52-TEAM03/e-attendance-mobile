package sa52.team03.adproject.CommonUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //Insert your IP address
    private static final String SERVER_URL = "http://192.168.10.147:8080/";
    private static final String FACE_ID_URL = "https://aip.baidubce.com/rest/2.0/face/v3/";
    private static RetrofitClient faceIdClient;
    private static RetrofitClient serverClient;
    private Retrofit retrofit;

    private RetrofitClient (String base_url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getFaceIdInstance() {
        if (faceIdClient == null) {
            faceIdClient = new RetrofitClient(FACE_ID_URL);
        }
        return faceIdClient;
    }

    public static synchronized RetrofitClient getServerInstance() {
        if (serverClient == null) {
            serverClient = new RetrofitClient(SERVER_URL);
        }
        return serverClient;
    }

    public RetrofitAPI getAPI () {
        return retrofit.create(RetrofitAPI.class);
    }
}
