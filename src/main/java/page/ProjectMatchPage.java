package page;

import conf.BasicClass;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ProjectMatchPage extends BasicClass {
    HashMap<String, List<Object>> support=new HashMap<>();

    //获取历史匹配的企业名
    public Response getLastMatch(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchEasyGetLastMatch.yaml",map);
    }

    //获取扶持方式id
    public HashMap<String, List<Object>> getSupport(){
        if (support.size()==0){
            HashMap<String,Object> body=new HashMap<>();
            body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyV2Match.json");
            List<HashMap<String,String>> list=getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchEasyV2Match.yaml",body).path("data.categoryTotal");
            list.forEach(map -> {
                List<Object> list1=new ArrayList<>();
                list1.add(map.get("typeId"));
                list1.add(map.get("total"));
                support.put(map.get("name"),list1);
            });
            return support;
        }
        return support;
    }

    //判断用户当天是否已匹配
    public Response isMatch(){
        return getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchEasyV2GetMatchCount.yaml");
    }

    //判断用户当天是否已匹配
    public Response isMatch(String phone){//判断用户当天是否已匹配
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone",phone);
        String token=getResponseFromYaml("/api/page/loginPage/apiPcpVerLogin.yaml",map).path("data");
        return given().log().all().header("Content-Type","application/json; charset=UTF-8").header("Token",token).header("Pcpauth","\n" +
                "Bearer "+token).when().get("http://ctxt.po-o.cn/api/pcp/projectmatch/easy/v2/getMatchCount").then().log().all().statusCode(200).extract().response();
    }

    //筛选项目列表
    public Response projectSearch(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchEasyV2GetProjectPage.yaml",map);
    }

    //历史记录
    public Response getMatchRecord(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchEasyGetMatchRecord.yaml",map);
    }

    //获取匹配记录结果
    public Response getMatchRecordResult(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/projectMatchPage/apiPcpProjectmatchGetMatchRecordResult.yaml",map);
    }
}
