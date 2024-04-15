package scut.deng.didservice;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;

@SpringBootApplication
@Slf4j
public class DidServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(DidServiceApplication.class, args);



    }

    @Bean
    public HashMap<String, Integer> hashMap(){
        return new HashMap<>();
    }

    @Bean(value = "web")
    public Dict dict(){
        Dict dict = Dict.create();
        dict.set("issuerNum", 0);
        dict.set("webServicePoint", new HashMap<String, JSONObject>());
        return dict;
    }

    @Bean(value = "DIDdict")
    public Dict DIDdict(){
        Dict dict = Dict.create();
        return dict;
    }

    @Bean(value = "Enterprisedict")
    public Dict Enterprisedict(){
        Dict dict = Dict.create();
        return dict;
    }

    @Bean(value = "VCdict")
    public Dict VCdict(){
        Dict dict = Dict.create();
        return dict;
    }
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;

    }
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }

}
