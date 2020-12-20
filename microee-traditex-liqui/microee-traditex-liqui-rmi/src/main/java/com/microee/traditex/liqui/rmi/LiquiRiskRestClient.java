package com.microee.traditex.liqui.rmi;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.plugin.response.exception.RestException;
import com.microee.traditex.liqui.oem.models.LiquiRiskAlarmSettings;
import com.microee.traditex.liqui.oem.models.LiquiRiskStrategySettings;
import com.microee.traditex.liqui.oem.rmi.LiquiRiskRestfulRMi;

@Component
public class LiquiRiskRestClient implements LiquiRiskRestfulRMi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiRiskRestClient.class);

    @Value("${micro.services.microee-traditex-liqui-app.listOfServers}")
    private String listOfServers;

    // strategy list
    @Override
    @RequestMapping(value = "/strategy/list", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<List<LiquiRiskStrategySettings>> strategyList(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "liquiditySymbol", required = false) String liquiditySymbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/strategy/list", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl).queryParam("id", id).queryParam("liquiditySymbol", liquiditySymbol);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        String responseBody = response.getBody();
        R<List<LiquiRiskStrategySettings>> result = HttpAssets.parseJson(responseBody,
                new TypeReference<R<List<LiquiRiskStrategySettings>>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

    // 保存 strategy
    @Override
    @RequestMapping(value = "/strategy/save", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Long> create(@RequestBody LiquiRiskStrategySettings body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/strategy/save", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LiquiRiskStrategySettings> httpEntity = new HttpEntity<>(body, headers);
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                httpEntity, String.class);
        String responseBody = response.getBody();
        R<Long> result = HttpAssets.parseJson(responseBody, new TypeReference<R<Long>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

    @Override
    @RequestMapping(value = "/alarm/settings/get", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<LiquiRiskAlarmSettings> getAlarmSettings(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "strategyId", required = false) Long strategyId,
            @RequestParam(value = "liquiditySymbol", required = false) String liquiditySymbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/alarm/settings/get", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl)
                .queryParam("id", id).queryParam("strategyId", liquiditySymbol).queryParam("liquiditySymbol", liquiditySymbol);
        RestTemplate restTemplate = new RestTemplate();
        LOGGER.info("getAlarmSettings1: url={}, result={}", builder.toUriString());
        LOGGER.info("getAlarmSettings2-1: url={}, restTemplate={}", builder.toUriString(), restTemplate == null);
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        LOGGER.info("getAlarmSettings2-2: url={}, result={}", builder.toUriString());
        String responseBody = response.getBody();
        R<LiquiRiskAlarmSettings> result = HttpAssets.parseJson(responseBody,
                new TypeReference<R<LiquiRiskAlarmSettings>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient-getAlarmSettings: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient-getAlarmSettings: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

    @Override
    @RequestMapping(value = "/strategy/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> updateStrategy(LiquiRiskStrategySettings body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/strategy/update", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LiquiRiskStrategySettings> httpEntity = new HttpEntity<>(body, headers);
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                httpEntity, String.class);
        String responseBody = response.getBody();
        R<Boolean> result = HttpAssets.parseJson(responseBody, new TypeReference<R<Boolean>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

    @Override
    @RequestMapping(value = "/alarm/update", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> modifyAlarmSettings(LiquiRiskAlarmSettings body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/alarm/update", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LiquiRiskAlarmSettings> httpEntity = new HttpEntity<>(body, headers);
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                httpEntity, String.class);
        String responseBody = response.getBody();
        R<Boolean> result = HttpAssets.parseJson(responseBody, new TypeReference<R<Boolean>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

    @Override
    @RequestMapping(value = "/alarm/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> addAlarmSettings(LiquiRiskAlarmSettings body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        String fooResourceUrl = String.format("%s/liqui/risk/alarm/add", listOfServers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fooResourceUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LiquiRiskAlarmSettings> httpEntity = new HttpEntity<>(body, headers);
        HttpEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                httpEntity, String.class);
        String responseBody = response.getBody();
        R<Boolean> result = HttpAssets.parseJson(responseBody, new TypeReference<R<Boolean>>() {});
        if (result.getCode() != 200 && result.getCode() != 201) {
            LOGGER.error("restclient: url={}, errorMessage={}", builder.toUriString(),
                    HttpAssets.toJsonString(result));
            throw new RestException(result.getCode(), result.getMessage());
        }
        LOGGER.info("restclient: url={}, result={}", builder.toUriString(),
                HttpAssets.toJsonString(result));
        return result;
    }

}
