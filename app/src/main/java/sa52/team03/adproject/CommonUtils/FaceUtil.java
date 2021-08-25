package sa52.team03.adproject.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class FaceUtil {

    static String ACCESS_TOKEN;

    final static String clientId = "xxGzIaemhGmLSzounyfi3VcY";
    final static String clientSecret = "Kmo9EETvRgBEzXK89x5yoSGIk7FRXnMy";

    public static String getFaceAPIToken() {

        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // open link to the TokenUrl
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result.toString());
            return jsonObject.getString("access_token");
        } catch (Exception e) {
            System.err.print("fail to get token！");
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static Call<ResponseBody> GetAuthenticationCall(String stringBase64) throws JSONException {

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("image", stringBase64);
        bodyJson.put("image_type", "BASE64");
        bodyJson.put("group_id_list", "student,lecturer,admin");
        bodyJson.put("quality_control", "NORMAL");
        bodyJson.put("liveness_control", "LOW");
        bodyJson.put("max_face_num", "1");
        String bodyString = bodyJson.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bodyString);

        if (ACCESS_TOKEN==null)
            ACCESS_TOKEN = FaceUtil.getFaceAPIToken();

        return RetrofitClient
                .getFaceIdInstance()
                .getAPI()
                .faceSearch(ACCESS_TOKEN, requestBody);
    }

    public static Call<ResponseBody> GetRegisterCall(String stringBase64) throws JSONException {

        JSONObject bodyJson = new JSONObject();
        bodyJson.put("image", stringBase64);
        bodyJson.put("image_type", "BASE64");
        bodyJson.put("group_id", "student");
        bodyJson.put("user_id", "9");
        bodyJson.put("quality_control", "HIGH");
        bodyJson.put("liveness_control", "NORMAL");
        bodyJson.put("max_face_num", "1");
        String bodyString = bodyJson.toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bodyString);

        if (ACCESS_TOKEN==null)
            ACCESS_TOKEN = FaceUtil.getFaceAPIToken();

        return RetrofitClient
                .getFaceIdInstance()
                .getAPI()
                .faceRegister(ACCESS_TOKEN, requestBody);
    }

}