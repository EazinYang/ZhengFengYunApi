package conf;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class Api {

    public Api() {
        useRelaxedHTTPSValidation();
    }

    public RequestSpecification getDefaultRequestSpecification() {
//        return given().log().all().header("Content-Type","application/json; charset=UTF-8");
        return given().log().all();
    }

    public static String loadJson(String path, HashMap<String, Object> map) {
        try {
            DocumentContext documentContext = JsonPath.parse(Api.class.getResource(path));
            map.entrySet().forEach(entry -> {
                documentContext.set(JsonPath.compile(entry.getKey()), entry.getValue());
            });
            return documentContext.jsonString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Restful getApiFromYaml(String path) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            Restful restful = objectMapper.readValue(Restful.class.getResource(path), Restful.class);
            return restful;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Restful updateApiFromMap(Restful restful, HashMap<String, Object> map) {
        if (restful.method.toLowerCase().contains("get")) {
            String path = (String) map.get("_file");
            map.remove("_file");
            try {
                DocumentContext documentContext = JsonPath.parse(Api.class.getResource(path));
                map.entrySet().forEach(entry -> {
                    documentContext.set(JsonPath.compile(entry.getKey()), entry.getValue());
                });
                Map jsonObject= JSONObject.parse(documentContext.jsonString());
                for(Object maps:jsonObject.entrySet()){
                    restful.query.put((String) ((Map.Entry)maps).getKey(),(((Map.Entry)maps).getValue()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (restful.method.toLowerCase().contains("post") && restful.param==1) {
            String path = (String) map.get("_file");
            map.remove("_file");
            try {
                DocumentContext documentContext = JsonPath.parse(Api.class.getResource(path));
                map.entrySet().forEach(entry -> {
                    documentContext.set(JsonPath.compile(entry.getKey()), entry.getValue());
                });
                Map jsonObject= JSONObject.parse(documentContext.jsonString());
                for(Object maps:jsonObject.entrySet()){
                    restful.query.put((String) ((Map.Entry)maps).getKey(),(((Map.Entry)maps).getValue()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (restful.method.toLowerCase().contains("post") && restful.param==0) {
            if (map.containsKey("_body")) {
                restful.body = (String) map.get("_body");
            }
            if (map.containsKey("_file")) {
                String path = (String) map.get("_file");
                map.remove("_file");
                restful.body = loadJson(path, map);
            }
        }
        return restful;
    }

    private String[] updateUrl(String url) {
        //多环境支持，替换url，更新host的header
        HashMap<String, String> hosts = Config.getInstance().env.get(Config.getInstance().current);
        String host = "";
        String urlNew = "";
        for (Map.Entry<String, String> entry : hosts.entrySet()) {
            if (url.contains(entry.getKey())) {
                host = entry.getKey();
                urlNew = url.replace(entry.getKey(), entry.getValue());
            }
        }
        return new String[]{host, urlNew};
    }

    public Response getResponseFromRestful(Restful restful){
        RequestSpecification requestSpecification=getDefaultRequestSpecification();
        if (restful.query!=null){
            restful.query.entrySet().forEach(entry -> {
                requestSpecification.queryParam(entry.getKey(),entry.getValue());});
        }
        if (restful.body!=null){
            requestSpecification.body(restful.body);
        }
        if (restful.header!=null){
            restful.header.entrySet().forEach(en->{
                requestSpecification.header(en.getKey(),en.getValue());
            });
        }
        String[] url=updateUrl(restful.url);
        return requestSpecification.header("Host",url[0]).when().request(restful.method,url[1]).then().log().all().statusCode(200).extract().response();
    }

    public Response getResponseFromRestful(Restful restful,File file){
        RequestSpecification requestSpecification=getDefaultRequestSpecification();
        if (restful.header!=null){
            restful.header.entrySet().forEach(en->{
                requestSpecification.header(en.getKey(),en.getValue());
            });
        }
        requestSpecification.multiPart(file);
        String[] url=updateUrl(restful.url);
        return requestSpecification.header("Host",url[0]).when().request(restful.method,url[1]).then().log().all().statusCode(200).extract().response();
    }

    public Response getResponseFromYaml(String path,HashMap<String,Object> map){
        //根据yaml生成接口定义并发送
        Restful restful=getApiFromYaml(path);
        restful=updateApiFromMap(restful,map);
        RestAssured.expect();
        return getResponseFromRestful(restful);
    }

    public Response getResponseFromYaml(String path){
        //根据yaml生成接口定义并发送
        Restful restful=getApiFromYaml(path);
        RestAssured.expect();
        return getResponseFromRestful(restful);
    }

    public Response getResponseFromYaml(String path,File file){
        //根据yaml生成接口定义并发送
        Restful restful=getApiFromYaml(path);
        RestAssured.expect();
        return getResponseFromRestful(restful,file);
    }
}
