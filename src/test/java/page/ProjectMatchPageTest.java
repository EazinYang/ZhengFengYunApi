package page;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

public class ProjectMatchPageTest {
    HomePage homePage=new HomePage();
    ProjectMatchPage projectMatchPage=new ProjectMatchPage();

    @Test
    @Description("正常测试用例：获取用户的历史匹配企业")
    @Story("项目匹配")
    public void getLastMatchSuccess(){
        List<Object[]> sql= JDBCUtils.queryMulti("SELECT DISTINCT(entity_name) FROM back_sys_user_match_record where user_id='"+homePage.getUserId()+"' order by create_time desc;");
        List<Object> companyname=new ArrayList<>();
        sql.forEach(objects -> {
            companyname.add(objects[0]);
        });
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyGetLastMatch.json");
        body.put("userId",homePage.getUserId());
        Response response= projectMatchPage.getLastMatch(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        list.forEach(map -> {
            assertTrue(companyname.contains(map.get("entityName")));
        });
    }

    @Test(priority = 1)
    @Description("正常测试用例：判断用户当天已匹配")
    @Story("项目匹配")
    public void isMatchTrue(){
        Response response= projectMatchPage.isMatch();
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data")==1);
    }

    @Test
    @Description("正常测试用例：判断用户当天未匹配")
    @Story("项目匹配")
    public void isMatchFalse(){
        Response response= projectMatchPage.isMatch("15910000000");
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data")==0);
    }

    @Test
    @Description("正常测试用例：根据扶持方式筛选项目并校验发文日期排序")
    @Story("项目匹配")
    public void supportProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyV2GetProjectPage.json");
        body.put("typeId",projectMatchPage.getSupport().get("资金扶持").get(0));
        body.put("userId",homePage.getUserId());
        Response response= projectMatchPage.projectSearch(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<List<String>> support=response.path("data.projectList.support");
        support.forEach(l -> {
            l.contains("资金");
        });
        List<String> dispatchDate=response.path("data.projectList.dispatchDate");
        for(int i=0;i<dispatchDate.size()-1;i++){
            assertTrue(Integer.parseInt(dispatchDate.get(i).replace("-",""))>=Integer.parseInt(dispatchDate.get(i+1).replace("-","")));
        }
    }

    @Test
    @Description("正常测试用例：扶持方式为空，筛选所有扶持方式的项目")
    @Story("项目匹配")
    public void supportEmptyProjectSearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyV2GetProjectPage.json");
        body.put("userId",homePage.getUserId());
        Response response= projectMatchPage.projectSearch(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data.allProjectNum"),projectMatchPage.getSupport().get("全部").get(1));
    }

    @Test
    @Description("正常测试用例：按截止日期排序")
    @Story("项目匹配")
    public void orderByEndDateSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyV2GetProjectPage.json");
        body.put("userId",homePage.getUserId());
        body.put("orderType",2);
        Response response= projectMatchPage.projectSearch(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<Integer> dispatchDate=response.path("data.projectList.remainingTime");
        for(int i=0;i<dispatchDate.size()-1;i++){
            assertTrue(dispatchDate.get(i)>=dispatchDate.get(i+1));
        }
    }

    @Test
    @Description("正常测试用例：按推荐度排序")
    @Story("项目匹配")
    public void orderByrecommendWeightSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyV2GetProjectPage.json");
        body.put("userId",homePage.getUserId());
        body.put("orderType",3);
        Response response= projectMatchPage.projectSearch(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<Float> dispatchDate=response.path("data.projectList.recommendWeight");
        for(int i=0;i<dispatchDate.size()-1;i++){
            assertTrue(dispatchDate.get(i)>=dispatchDate.get(i+1));
        }
    }

    @Test
    @Description("正常测试用例：【匹配记录】获取历史记录")
    @Story("项目匹配")
    public void getMatchRecord(){
        List<Object[]> sql= JDBCUtils.queryMulti("SELECT DISTINCT(entity_name) FROM back_sys_user_match_record where user_id='"+homePage.getUserId()+"' order by create_time desc;");
        List<Object> companyname=new ArrayList<>();
        sql.forEach(objects -> {
            companyname.add(objects[0]);
        });
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchEasyGetMatchRecord.json");
        body.put("userId",homePage.getUserId());
        Response response= projectMatchPage.getMatchRecord(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<String> name=response.path("data.entityName");
        name.forEach(string -> {
            assertTrue(companyname.contains(string));
        });
    }

    @Test
    @Description("正常测试用例：【匹配记录】获取匹配记录结果")
    @Story("项目匹配")
    public void getMatchRecordResult(){
        List<Object[]> recordId= JDBCUtils.queryMulti("select record_id,project_id from back_sys_user_match_result where project_id in (SELECT project_id from oa_formal_file where status ='1');");
        List<Object> projectid=new ArrayList<>();
        for(int i=0;i<recordId.size();i++){
            if(i>1){
                if(recordId.get(i)[0]==recordId.get(0)[0]){
                    projectid.add(""+recordId.get(i)[1]);
                }
            }
            projectid.add(recordId.get(i)[1]);
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/projectMatchPage/apiPcpProjectmatchGetMatchRecordResult.json");
        body.put("recordId",recordId.get(0)[0]);
        Response response= projectMatchPage.getMatchRecordResult(body);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> project=response.path("data.projectList");
        project.forEach(map -> {
            assertTrue(projectid.contains(map.get("projectId")));
        });
    }
}