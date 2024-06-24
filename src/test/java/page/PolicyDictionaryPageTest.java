package page;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class PolicyDictionaryPageTest {
    PolicyDictionaryPage policyDictionaryPage=new PolicyDictionaryPage();

    @Test
    @Description("正常测试用例：根据关键字搜索")
    @Story("政策词典")
    public void keywordSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionOne","科技");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<List<HashMap<String,String>>> title=response.path("data.children");
        title.forEach(list -> {
            list.forEach(map -> {
                assertTrue(map.get("title").contains("科技"));
            });
        });
    }

    @Test
    @Description("异常测试用例：关键字为空，搜索全部政策词典")
    @Story("政策词典")
    public void keywordEmptySearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionOne","");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(!((List)response.path("data")).isEmpty());
    }

    @Test
    @Description("正常测试用例：按字母类别搜索政策词典")
    @Story("政策词典")
    public void letterSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionTwo","ABCDE");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<String> zimu=response.path("data.zimu");
        zimu.forEach(string -> {
            assertTrue("ABCDE".contains(string));
        });
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Test
    @Description("正常测试用例：按中文类型搜索政策词典")
    @Story("政策词典")
    public void chineseSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionThree","chinese");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<List<HashMap<String,String>>> title=response.path("data.children");
        title.forEach(list -> {
            list.forEach(map -> {
                assertTrue(isContainChinese(map.get("title")) || map.get("title").matches(".*\\d.*"));
            });
        });
    }

    @Test
    @Description("正常测试用例：按中文类型搜索政策词典")
    @Story("政策词典")
    public void englishSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionThree","english");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<List<HashMap<String,String>>> title=response.path("data.children");
        title.forEach(list -> {
            list.forEach(map -> {
                assertTrue(map.get("title").matches(".*\\w.*"));
            });
        });
    }

    @Test
    @Description("正常测试用例：组合搜索")
    @Story("政策词典")
    public void allKeySearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditions.json");
        body.put("conditionTwo","ABCDE");
        body.put("conditionThree","chinese");
        Response response=policyDictionaryPage.searchDitictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<List<HashMap<String,String>>> title=response.path("data.children");
        title.forEach(list -> {
            list.forEach(map -> {
                assertTrue("ABCDE".contains(map.get("zimu")));
                assertTrue(isContainChinese(map.get("title")) || map.get("title").matches(".*\\d.*"));
            });
        });
    }

    @Test
    @Description("正常测试用例：获取政策词典热门搜索关键词")
    @Story("政策词典")
    public void getHotKeywordDictionary(){
        List<Object[]> sql= JDBCUtils.queryMulti("select keyword from oa_dictionary_search where type='0';");
        Response response=policyDictionaryPage.getHotKeywordDictionary();
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> title=response.path("data");
        for(int i=0;i<title.size();i++){
            assertEquals(title.get(i).get("keyword"),sql.get(i)[0]);
        }
    }

    @Test
    @Description("正常测试用例：获取词典详情信息")
    @Story("政策词典")
    public void getDictionaryInfoSuccess(){
        List<Object[]> sql= JDBCUtils.queryMulti("select id,title,content from ims_ewei_shop_qa_question limit 0,10;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindConditionsById.json");
        body.put("id",sql.get(0)[0]);
        Response response=policyDictionaryPage.getDictionaryInfo(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        HashMap<String,Object> title=response.path("data");
        assertEquals(title.get("title"),sql.get(0)[1]);
        String content=""+sql.get(0)[2];
        assertTrue(content.contains(""+title.get("content")));
    }

    @Test
    @Description("正常测试用例：获取更多词典")
    @Story("政策词典")
    public void getMoreDictionarySuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindMore.json");
        body.put("id",1735);
        body.put("keyWord","安全生产");
        Response response=policyDictionaryPage.getMoreDictionary(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        assertTrue(list.size()>0);
    }

    @Test
    @Description("正常测试用例：获取关联的政策")
    @Story("政策词典")
    public void getMoreDictionaryPolicySuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyDictionaryPage/apiEweiShopQaQuestionFindMorePolicy.json");
        Response response=policyDictionaryPage.getMoreDictionaryPolicy(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        assertTrue(list.size()>0);
    }
}