package wgu.jbas127.frontiercompanion.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import wgu.jbas127.frontiercompanion.data.models.ApiResponseDTO;

public interface ApiService {
    @GET("api/search")
    Call<ApiResponseDTO> searchItems(@Query("query") String query);
}
