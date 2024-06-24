package page;

import conf.BasicClass;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

public class IntelligentPushPageTest {
    IntelligentPushPage intelligentPushPage=new IntelligentPushPage();
    HomePage homePage=new HomePage();
    PolicyPage policyPage=new PolicyPage();

    @Test
    @Description("正常测试用例：【适配项目】关键字为空，筛选所有项目，排序方式：推送时间")
    @Story("智能推送")
    public void keywordEmptyPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String createTime1=""+result.get(i).get("createTime");
            String createTime2=""+result.get(i+1).get("createTime");
            assertTrue(Integer.parseInt(createTime1.replace("-",""))>=Integer.parseInt(createTime2.replace("-","")));
        }
    }

    @Test
    @Description("正常测试用例：【适配项目】输入关键字，筛选含关键字的项目")
    @Story("智能推送")
    public void keywordPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("search","广东");
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        result.forEach(map -> {
            assertTrue((""+map.get("subtitle")).contains("广东"));
        });
    }

    @Test
    @Description("异常测试用例：【适配项目】输入不存在的关键字，筛选结果为空")
    @Story("智能推送")
    public void keywordFalsePushProjectSearchFail(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("search","null!");
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：【适配项目】排序方式为发文时间，根据发文时间降序排序")
    @Story("智能推送")
    public void dispatchDateSortPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("sortType",0);
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String dispatchDate1=""+result.get(i).get("dispatchDate");
            String dispatchDate2=""+result.get(i+1).get("dispatchDate");
            assertTrue(Integer.parseInt(dispatchDate1.replace("-",""))>=Integer.parseInt(dispatchDate2.replace("-","")));
        }
    }

    @Test
    @Description("正常测试用例：【适配项目】排序方式为截止日期，根据截止日期降序排序")
    @Story("智能推送")
    public void endDateSortPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("sortType",1);
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String declareDate1=""+result.get(i).get("declareDate");
            String declareDate2=""+result.get(i+1).get("declareDate");
            assertTrue(Integer.parseInt(declareDate1.replace("-",""))>=Integer.parseInt(declareDate2.replace("-","")));
        }
    }

    @Test
    @Description("正常测试用例：【适配项目】单个扶持方式，筛选含该扶持方式的项目")
    @Story("智能推送")
    public void oneSupportPushProjectSearchSuccess(){
        HashMap<Object, Object> supportid=policyPage.getSupportUuid();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("uuid",supportid.get("资金扶持"));
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        result.forEach(map -> {
            List<String> fuchifangshi=(List<String>) map.get("fuchifangshi");
            assertTrue(fuchifangshi.contains("资金"));
        });
    }

    @Test
    @Description("正常测试用例：【适配项目】多个扶持方式，筛选含该扶持方式的项目")
    @Story("智能推送")
    public void multiSupportPushProjectSearchSuccess(){
        HashMap<Object, Object> supportid=policyPage.getSupportUuid();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("uuid",supportid.get("资金扶持")+","+supportid.get("称号认定"));
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        result.forEach(map -> {
            List<String> fuchifangshi=(List<String>) map.get("fuchifangshi");
            assertTrue(fuchifangshi.contains("资金") && fuchifangshi.contains("称号"));
        });
    }

    @Test
    @Description("正常测试用例：【适配项目】筛选申报状态为申报中的项目")
    @Story("智能推送")
    public void clareStartPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("status",1);
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String declareDate=""+result.get(i).get("declareDate");
            assertTrue(Integer.parseInt(declareDate)>=0);
        }
    }

    @Test
    @Description("正常测试用例：【适配项目】筛选申报状态为申报中的项目")
    @Story("智能推送")
    public void clareEndPushProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("status",2);
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String declareDate=""+result.get(i).get("declareDate");
            assertTrue(Integer.parseInt(declareDate)<0);
        }
    }

    @Test(priority = 1)
    @Description("正常测试用例：【适配项目】组合搜索")
    @Story("智能推送")
    public void groupPushProjectSearchSuccess(){
        HashMap<Object, Object> supportid=policyPage.getSupportUuid();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.json");
        body.put("memberId",homePage.getUserId());
        body.put("search","广东");
        body.put("status",1);
        body.put("uuid",supportid.get("资金"));
        Response response=intelligentPushPage.searchProjectPush(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> result=response.path("data.result");
        for (int i=0;i<result.size()-1;i++){
            String declareDate=""+result.get(i).get("declareDate");
            String subject=""+result.get(i).get("subtitle");
            List<String> fuchifangshi=(List<String>) result.get(i).get("fuchifangshi");
            assertTrue(Integer.parseInt(declareDate)>=0);
            assertTrue(subject.contains("广东"));
            assertTrue(fuchifangshi.contains("资金"));
        }
    }


    @Test(priority = 1)
    @Description("正常测试用例：【适配项目】剩余次数>0，立即匹配项目")
    @Story("智能推送")
    public void matchProjectSuccess(){
        try{
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apipcpnewPersonalCenterUpdateIntelligentPush.json");
        body.put("userId",homePage.getUserId());
        Response response=intelligentPushPage.matchProject(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

//    @Test(priority =2)
//    @Description("正常测试用例：【适配项目】剩余次数=0，匹配失败")
//    @Story("智能推送")
//    public void matchProjectFail(){
//        try{
//            Thread.sleep(5000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        HashMap<String,Object> body=new HashMap<>();
//        body.put("_file","/apijson/page/intelligentPushPage/apipcpnewPersonalCenterUpdateIntelligentPush.json");
//        body.put("userId",homePage.getUserId());
//        Response response=intelligentPushPage.matchProject(body);
//        assertEquals((int) response.path("code"),500);
//        assertEquals(response.path("message"),"该用户今天刷新次数已用完");
//        assertTrue(response.path("data")==null);
//    }

    @Test
    @Description("正常测试用例：【适配项目】获取匹配次数")
    @Story("智能推送")
    public void getMatchProjectNumSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpNewPersonalCenterGetIntelligentPushNum.json");
        body.put("userId",homePage.getUserId());
        Response response=intelligentPushPage.getMatchProjectNum(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data")<=3 && (int)response.path("data")>=0);
    }

    @Test
    @Description("正常测试用例：【适配项目】获取公众号二维码")
    @Story("智能推送")
    public void getQRCode(){
        Response response=intelligentPushPage.getQRCode();
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        String code=response.path("data");
        assertTrue(code.contains("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="));
    }

    @Test
    @Description("正常测试用例：【适配项目】开启智能推送、仅有新项目推送、推送时间9:00")
    @Story("智能推送")
    public void updateBackSysUserPushSetAllOpen(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushUpdateBackSysUserPushSet.json");
        body.put("userId",homePage.getUserId());
        body.put("id",intelligentPushPage.getBackSysUserPushSetId(homePage.getUserId()));
        Response response1=intelligentPushPage.updateBackSysUserPushSet(body);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertEquals(response1.path("data"),"修改成功");
        Response response2=intelligentPushPage.getBackSysUserPushSet(homePage.getUserId());
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertEquals((int)response2.path("data.matchPushSet"),1);
        assertEquals((int)response2.path("data.matchRepeatPush"),1);
        assertEquals((int)response2.path("data.pushTime"),1);
    }

    @Test
    @Description("正常测试用例：【适配项目】推送时间15:00")
    @Story("智能推送")
    public void updateBackSysUserPushSetPutTime2(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushUpdateBackSysUserPushSet.json");
        body.put("userId",homePage.getUserId());
        body.put("id",intelligentPushPage.getBackSysUserPushSetId(homePage.getUserId()));
        body.put("pushTime",2);
        Response response1=intelligentPushPage.updateBackSysUserPushSet(body);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertEquals(response1.path("data"),"修改成功");
        Response response2=intelligentPushPage.getBackSysUserPushSet(homePage.getUserId());
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertEquals((int)response2.path("data.pushTime"),2);
    }

    @Test
    @Description("正常测试用例：【适配项目】关闭智能推送、仅有新项目推送")
    @Story("智能推送")
    public void updateBackSysUserPushSetAllClose(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushUpdateBackSysUserPushSet.json");
        body.put("userId",homePage.getUserId());
        body.put("id",intelligentPushPage.getBackSysUserPushSetId(homePage.getUserId()));
        body.put("matchPushSet",2);
        body.put("matchRepeatPush",2);
        Response response1=intelligentPushPage.updateBackSysUserPushSet(body);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertEquals(response1.path("data"),"修改成功");
        Response response2=intelligentPushPage.getBackSysUserPushSet(homePage.getUserId());
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertEquals((int)response2.path("data.matchPushSet"),2);
        assertEquals((int)response2.path("data.matchRepeatPush"),2);
    }

    @Test
    @Description("正常测试用例：【适配项目】获取搜索历史关键字")
    @Story("政策文库")
    public void getSearchHistorySuccess(){
        List<Object[]> sql= JDBCUtils.queryMulti("select keyword from ims_ewei_shop_member_search_history where search_type='2' and user_id='"+homePage.getUserId()+"' order by create_time desc;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpLibrarysearchGetMemberSearchHistory.json");
        body.put("memberId",homePage.getUserId());
        body.put("searchTypeId",2);
        Response response=policyPage.getSearchHistory(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        for (int i=0;i<list.size();i++){
            assertEquals(list.get(i).get("keyword"),sql.get(i)[0]);
        }
    }

    @Test
    @Description("正常测试用例：【关注政策】输入关键词，查询关注政策")
    @Story("智能推送")
    public void keywordGetGsIFocusPolicy(){
        HashMap<String,Object> body1=new HashMap<>();
        body1.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushFocusPolicySet.json");
        body1.put("id",homePage.getUserId());
        body1.put("keyword","技术,佛山");
        Response response1=intelligentPushPage.focusPolicySet(body1);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertTrue(response1.path("data")==null);
        HashMap<String,Object> body2=new HashMap<>();
        body2.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusPolicy.json");
        body2.put("memberId",homePage.getUserId());
        Response response2=intelligentPushPage.getGsIFocusPolicy(body2);
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> map=response2.path("data.result");
        map.forEach(map1 -> {
            assertTrue(String.valueOf(map1.get("title")).contains("技术") || String.valueOf(map1.get("title")).contains("佛山"));
        });
    }

    @Test
    @Description("正常测试用例：【关注政策】输入政策等级，查询关注政策")
    @Story("智能推送")
    public void zcdjGetGsIFocusPolicy(){
        HashMap<String,Object> body1=new HashMap<>();
        body1.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushFocusPolicySet.json");
        body1.put("id",homePage.getUserId());
        body1.put("zcJibie","1,3");
        Response response1=intelligentPushPage.focusPolicySet(body1);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertTrue(response1.path("data")==null);
        HashMap<String,Object> body2=new HashMap<>();
        body2.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusPolicy.json");
        body2.put("memberId",homePage.getUserId());
        Response response2=intelligentPushPage.getGsIFocusPolicy(body2);
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> map=response2.path("data.result");
        map.forEach(map1 -> {
            assertTrue((int)map1.get("heat")==0 || (int)map1.get("heat")==2);
        });
    }

    @Test
    @Description("正常测试用例：【关注政策】输入关键词、政策等级，查询关注政策")
    @Story("智能推送")
    public void allGetGsIFocusPolicy(){
        HashMap<String,Object> body1=new HashMap<>();
        body1.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushFocusPolicySet.json");
        body1.put("id",homePage.getUserId());
        body1.put("zcJibie","1,3");
        body1.put("keyword","技术,佛山");
        Response response1=intelligentPushPage.focusPolicySet(body1);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertTrue(response1.path("data")==null);
        HashMap<String,Object> body2=new HashMap<>();
        body2.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusPolicy.json");
        body2.put("memberId",homePage.getUserId());
        Response response2=intelligentPushPage.getGsIFocusPolicy(body2);
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> map=response2.path("data.result");
        map.forEach(map1 -> {
            assertTrue((int)map1.get("heat")==0 || (int)map1.get("heat")==2);
            assertTrue(String.valueOf(map1.get("title")).contains("技术") || String.valueOf(map1.get("title")).contains("佛山"));

        });
        HashMap<String,Object> body3=new HashMap<>();
        body3.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetFocusPolicySetting.json");
        body3.put("id",homePage.getUserId());
        Response response3=intelligentPushPage.getFocusPolicySetInfo(body3);
        assertEquals((int) response3.path("code"),200);
        assertEquals(response3.path("message"),"操作成功");
        assertEquals(response3.path("data.keyword"),"技术,佛山");
        assertEquals(response3.path("data.zcJibie"),"1,3");
    }

    @Test
    @Description("异常测试用例：【关注政策】均为空，查询关注政策为空")
    @Story("智能推送")
    public void emptyGetGsIFocusPolicy(){
        HashMap<String,Object> body1=new HashMap<>();
        body1.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushFocusPolicySet.json");
        body1.put("id",homePage.getUserId());
        Response response1=intelligentPushPage.focusPolicySet(body1);
        assertEquals((int) response1.path("code"),200);
        assertEquals(response1.path("message"),"操作成功");
        assertTrue(response1.path("data")==null);
        HashMap<String,Object> body2=new HashMap<>();
        body2.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusPolicy.json");
        body2.put("memberId",homePage.getUserId());
        Response response2=intelligentPushPage.getGsIFocusPolicy(body2);
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertTrue((int)response2.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：【我的订阅】我的订阅列表查询")
    @Story("智能推送")
    public void mySubscribe(){
        List<Integer> yidingyue= policyPage.getYidingyue();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushMySubscribe.json");
        body.put("id",homePage.getUserId());
        Response response=intelligentPushPage.mySubscribe(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> list=response.path("data");
        list.forEach(map -> {
            assertTrue(yidingyue.contains(map.get("id")));
        });
    }
}