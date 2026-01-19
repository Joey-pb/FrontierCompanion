package wgu.jbas127.frontiercompanion.network;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.internal.GsonBuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wgu.jbas127.frontiercompanion.FrontierCompanionApplication;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static synchronized  ApiService getApiService() {
        if (apiService == null) {
            if (retrofit == null) {
                Application app = FrontierCompanionApplication.getInstance();
                String apiKey = "";

                try {
                    // Read AndroidManifest metadata
                    ApplicationInfo appInfo = app.getPackageManager().getApplicationInfo(
                            app.getPackageName(),
                            PackageManager.GET_META_DATA);

                    Bundle bundle = appInfo.metaData;
                    apiKey = bundle.getString("wgu.jbas127.frontiercompanion.FCMV_API_KEY");
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("RetrofitClient", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                }

                if (apiKey == null || apiKey.isEmpty()) {
                    Log.e("RetrofitClient", "API Key not found in AndroidManifest.xml");
                }

                // Create Interceptors
                AuthInterceptor authInterceptor = new AuthInterceptor(apiKey);
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                // Build OkHttpClient
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(authInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build();

                // Build Retrofit
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}
