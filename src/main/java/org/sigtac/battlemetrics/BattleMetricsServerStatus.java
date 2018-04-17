package org.sigtac.battlemetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.sigtac.battlemetrics.model.BattleMetricsServer;
import org.sigtac.battlemetrics.model.BattleMetricsServerQueryResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class BattleMetricsServerStatus {
    private static Logger logger = Logger.getLogger(BattleMetricsServerStatus.class);
    private final BattleMetricsService service;
    private static final String ENDPOINT = "https://api.battlemetrics.com/";
    private static final String GAME_NAME = "squad";

    public BattleMetricsServerStatus() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
        service = retrofit.create(BattleMetricsService.class);

    }

    public Optional<BattleMetricsServer> getConnectionInfo(String serverNameFilter, Integer serverPortFilter, String ipFilter) throws IOException {
        Response<BattleMetricsServerQueryResponse> queryResult = service.getServersForGame(GAME_NAME).execute();
        do {
            if ( queryResult.isSuccessful() && queryResult.body() != null ) {
                try {
                    return checkResults(queryResult, serverNameFilter, serverPortFilter, ipFilter);
                } catch (NoSuchElementException nse) {
                    if(queryResult.body().links.get("next") == null) {
                        return Optional.empty();
                    }
                    URL nextQueryURL = new URL(queryResult.body().links.get("next"));
                    Map<String, List<String>> queryParams = splitQuery(URLDecoder.decode(nextQueryURL.getQuery(), "UTF-8"));
                    queryResult = service.continueQueryForServers(queryParams.get("page[key]").get(0), "squad").execute();
                }
            }
        }
        while (true);
    }

    /**
     * All input params must match for the predicate to match
     * @param serverNameFilter
     * @param serverPortFilter
     * @return
     */
    public Predicate<BattleMetricsServer> filterFunc(String serverNameFilter, Integer serverPortFilter, String ipFilter) {
      return server -> {
          if(!serverNameFilter.isEmpty() &&
              !server.getAttributes().getName().contains(serverNameFilter)) {
                  return false;
          }
          if(serverPortFilter!=null &&
                  !server.getAttributes().getPort().equals(serverPortFilter)){
              return false;
          }
          if(!ipFilter.isEmpty() &&
                  !server.getAttributes().getIp().equals(ipFilter)){
              return false;
          }
          return true;
      };
    }

    private Optional<BattleMetricsServer> checkResults(Response<BattleMetricsServerQueryResponse> queryResult,
                                                       String serverNameFilter,
                                                       Integer serverPortFilter,
                                                       String ipFilter) {
        return queryResult.body().getData().stream()
                .peek(server -> logger.trace("Checking server: " + server.getAttributes().getName()))
                .filter(filterFunc(serverNameFilter, serverPortFilter, ipFilter))
                .findFirst();
    }

    /** Stole this
     * https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection?
     * Had to fix it tho.  Fucking useless
     * @param params
     * @return
     */
    public Map<String, List<String>> splitQuery(String params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyMap();
        }
        // OMG who writes this crap
        return Arrays.stream(params.split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey,
                        LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new SimpleImmutableEntry<>(key, value);
    }
}
