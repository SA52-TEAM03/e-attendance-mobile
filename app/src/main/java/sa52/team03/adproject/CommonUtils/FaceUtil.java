package sa52.team03.adproject.CommonUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FaceUtil {

    public static String getFaceAPIToken() {
        String clientId = "xxGzIaemhGmLSzounyfi3VcY";
        String clientSecret = "Kmo9EETvRgBEzXK89x5yoSGIk7FRXnMy";
        return getFaceAPIToken(clientId, clientSecret);
    }

    public static String getFaceAPIToken(String ak, String sk) {

        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + ak
                + "&client_secret=" + sk;
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

}