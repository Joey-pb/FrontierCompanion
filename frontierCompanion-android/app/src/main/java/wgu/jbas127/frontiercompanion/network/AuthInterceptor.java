package wgu.jbas127.frontiercompanion.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final String apiKey;

    public AuthInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // Get the original request from the chain.
        Request originalRequest = chain.request();

        // Build a new request, adding the "X-API-KEY" header.
        Request newRequest = originalRequest.newBuilder()
                .header("X-API-KEY", apiKey)
                .build();

        // Proceed with the new request and return the response.
        return chain.proceed(newRequest);
    }
}
