package page;

import conf.BasicClass;
import io.restassured.response.Response;

import java.util.HashMap;

public class PolicyDictionaryPage extends BasicClass {
    //搜索政策词典
    public Response searchDitictionary(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.yaml",map);
    }

    //获取政策词典热门搜索关键词
    public Response getHotKeywordDictionary(){
        return getResponseFromYaml("/api/page/policyDictionaryPage/apiEweiShopQaQuestionFindDictionarySearch.yaml");
    }

    //获取政策词典详情
    public Response getDictionaryInfo(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditionsById.yaml",map);
    }

    //获取更多词典
    public Response getMoreDictionary(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyDictionaryPage/apiEweiShopQaQuestionFindMore.yaml",map);
    }

    //获取更多词典
    public Response getMoreDictionaryPolicy(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyDictionaryPage/apiEweiShopQaQuestionFindMorePolicy.yaml",map);
    }
}
