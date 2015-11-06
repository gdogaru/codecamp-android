package com.gdogaru.codecamp.svc;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.model.Codecamp;
import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class CodecampClient {

    private RestTemplate restTemplate;
    private String rootUrl;

    public CodecampClient() {
        restTemplate = new RestTemplate();
        GsonHttpMessageConverter messageConverter = new GsonHttpMessageConverter();
        Gson gson = CodecampApplication.instance().getGson();
        messageConverter.setGson(gson);
        restTemplate.getMessageConverters().add(messageConverter);
//        rootUrl = "https://codecampevents.azure-mobile.dnet/tables";
        rootUrl = "http://gdogaru.ro";
    }

    public Codecamp getEventData() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(org.springframework.http.MediaType.parseMediaType("application/json")));
        httpHeaders.set(ServerUtilities.ZUMO_HEADER, ServerUtilities.ZUMO_APP_ID);
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(httpHeaders);
        return restTemplate.exchange(rootUrl.concat("/codecamp_app_agenda.json"), HttpMethod.GET, requestEntity, Codecamp.class).getBody();
    }

}

//
//http://tagonsoft.ro/feedback.php - http POST
