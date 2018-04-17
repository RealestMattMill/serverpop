package org.sigtac.battlemetrics;

import org.sigtac.battlemetrics.model.BattleMetricsServerQueryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BattleMetricsService {
    @GET("servers")
    Call<BattleMetricsServerQueryResponse> getServers();

    @GET("servers?page[size]=100")
    Call<BattleMetricsServerQueryResponse> getServersForGame(@Query("filter[game]") String game);

    @GET("servers?page[size]=100&page[rel]=next")
    Call<BattleMetricsServerQueryResponse> continueQueryForServers(@Query("page[key]") String key, @Query("filter[game]") String game);
}
