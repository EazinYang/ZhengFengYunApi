package page;

import com.github.javafaker.Faker;
import conf.Api;
import conf.BasicClass;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.util.*;
import static org.testng.Assert.*;

public class PolicyPageTest {
    PolicyPage policyPage=new PolicyPage();
    HomePage homePage=new HomePage();
    Faker fake=new Faker(Locale.CHINA);

    @Test
    @Description("正常测试用例：【申报通知】筛选关键字")
    @Story("政策文库")
    public void policyKeywordSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","测试数据");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.title");
        title.forEach(string -> {
            assertTrue(string.contains("测试数据"));
        });
    }

    @Test
    @Description("异常测试用例：【申报通知】关键字为空，筛选出所有数据")
    @Story("政策文库")
    public void policyKeywordEmptySearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")!=0);
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> time=jsonPath.get("data.hits.dispatchDate");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(Integer.parseInt(time.get(i).replace("-",""))>=Integer.parseInt(time.get(i+1).replace("-",""))){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("异常测试用例：【申报通知】不存在的政策关键词，筛选结果为空")
    @Story("政策文库")
    public void policyKeywordFalseSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","null!");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选申报状态=申报中的政策")
    @Story("政策文库")
    public void policyApplyStartSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("applyStatus","1");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<Integer> title=jsonPath.get("data.hits.minRemainDay");
        title.forEach(string -> {
            assertTrue(string>0);
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选申报状态=已截止的政策")
    @Story("政策文库")
    public void policyApplyFinshSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("applyStatus","2");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<Integer> title=jsonPath.get("data.hits.minRemainDay");
        title.forEach(string -> {
            assertTrue(string<0);
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选区域=国家级的政策")
    @Story("政策文库")
    public void policyLocationContrySearchSuccess(){
        List<String> list=policyPage.getCountryDepartment();
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("areas",new String[]{"000000","",""});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.department");
        title.forEach(string -> {
            assertTrue(list.contains(string));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选区域=广东省的政策")
    @Story("政策文库")
    public void policyLocationProvinceSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("prov","440000");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.province");
        title.forEach(string -> {
            assertTrue(string.equals("440000"));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选区域=佛山市的政策")
    @Story("政策文库")
    public void policyLocationCitySearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("areas",new String[]{"foshan","",""});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.city");
        title.forEach(string -> {
            assertTrue(string.equals(policyPage.getCityCode().get("foshan")));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选区域=佛山市下的区级的政策")
    @Story("政策文库")
    public void policyLocationDistrictSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        String code=policyPage.getAreaByParentCode("foshan").get("禅城区");
        map.put("areas",new String[]{"foshan",code,""});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> city=jsonPath.get("data.hits.city");
        city.forEach(string -> {
            assertTrue(string.equals(policyPage.getCityCode().get("foshan")));
        });
        List<String> area=jsonPath.get("data.hits.area");
        area.forEach(string -> {
            assertTrue(string.equals(code));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选区域=街道的政策")
    @Story("政策文库")
    public void policyLocationStreetSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        String districtcode=policyPage.getAreaByParentCode("foshan").get("禅城区");
        String streetcode=policyPage.getAreaByParentCode(districtcode).get("石湾镇街道");
        map.put("areas",new String[]{"foshan",districtcode,streetcode});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> city=jsonPath.get("data.hits.city");
        city.forEach(string -> {
            assertTrue(string.equals(policyPage.getCityCode().get("foshan")));
        });
        List<String> area=jsonPath.get("data.hits.area");
        area.forEach(string -> {
            assertTrue(string.equals(districtcode));
        });
        List<String> street=jsonPath.get("data.hits.street");
        street.forEach(string -> {
            assertTrue(string.equals(streetcode));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选主管部门条件的政策")
    @Story("政策文库")
    public void policyDepartmentSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("department","商务部");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.department");
        title.forEach(string -> {
            assertEquals(string,"商务部");
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选政策范围=含本级以下的政策")
    @Story("政策文库")
    public void policyBoundaryIncludeSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("boundary","1");
        map.put("prov","440000");
        Response response=policyPage.policySearch(map);
        Collection<String> districtcode = policyPage.getAreaByParentCode("foshan").values();
        ArrayList<String> streetcode =new ArrayList<>();
        districtcode.forEach(string -> {
            policyPage.getAreaByParentCode(string).values().forEach(string1 -> {
                streetcode.add(string1);
            });
        });
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<HashMap<String,Object>> policy=jsonPath.get("data.hits");
        policy.forEach(entry -> {
            assertEquals(entry.get("province"),"440000");
            if(entry.get("level").equals("3")){
                assertTrue(entry.get("city").equals("") || policyPage.getCityCode().containsValue(entry.get("city")));
            }
            if(entry.get("level").equals("4")){
                assertTrue(entry.get("city").equals("") || policyPage.getCityCode().containsValue(entry.get("city")));
                assertTrue(entry.get("area").equals("") || districtcode.contains(entry.get("area")));
            }
            if(entry.get("level").equals("5")){
                assertTrue(entry.get("city").equals("") || policyPage.getCityCode().containsValue(entry.get("city")));
                assertTrue(entry.get("area").equals("") || districtcode.contains(entry.get("area")));
                assertTrue(entry.get("street").equals("") || streetcode.contains(entry.get("street")));
            }
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选扶持方式")
    @Story("政策文库")
    public void policySupportSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        HashMap<Object,Object> uuid=policyPage.getSupportUuid();
        String sup1=(String)uuid.get("资金扶持");
        String sup2=(String)uuid.get("称号认定");
        map.put("uuidList",new Object[]{sup1,sup2});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<List<HashMap<String,Object>>> project=jsonPath.get("data.hits.projects");
        project.forEach(projects->{
            ArrayList<String> supportMethods=new ArrayList<>();
            List<String> supportMethodList=new ArrayList<>();
            ArrayList<String> supportMethodUUID=new ArrayList<>();
            for(HashMap<String,Object> map1:projects){
                for(String str1:((String)map1.get("supportMethods")).split(",")){
                    supportMethods.add(str1);
                }
                for(String str:(List<String>)map1.get("supportMethodList")){
                    supportMethodList.add(str);
                }
                for(String str1:((String)map1.get("supportMethodUUID")).split(",")){
                    supportMethodUUID.add(str1);
                }
            }
            assertTrue(supportMethods.contains("资金扶持") && supportMethods.contains("称号认定"));
            assertTrue(supportMethodUUID.contains(sup1) && supportMethodUUID.contains(sup2));
            assertTrue(supportMethodList.contains("资金扶持") && supportMethodList.contains("称号认定"));
        });
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选：发文时间升序")
    @Story("政策文库")
    public void policyDispatchDateAscSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("orderBy","asc");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> time=jsonPath.get("data.hits.dispatchDate");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(Integer.parseInt(time.get(i).replace("-",""))<=Integer.parseInt(time.get(i+1).replace("-",""))){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选：截止日期降序")
    @Story("政策文库")
    public void policyRemainDayDescSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("sortType","1");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<Integer> time=jsonPath.get("data.hits.minRemainDay");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(time.get(i)>=time.get(i+1)){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("正常测试用例：【申报通知】筛选：截止日期升序")
    @Story("政策文库")
    public void policyRemainDayAscSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("sortType","1");
        map.put("orderBy","asc");
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<Integer> time=jsonPath.get("data.hits.minRemainDay");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(time.get(i)<=time.get(i+1)){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("正常测试用例：【申报通知】组合搜索")
    @Story("政策文库")
    public void policyAllSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        String districtcode=policyPage.getAreaByParentCode("foshan").get("南海区");
        String streetcode=policyPage.getAreaByParentCode(districtcode).get("桂城街道");
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","测试数据");
        map.put("department","桂城街道经济促进局");
        map.put("areas",new String[]{"foshan",districtcode,streetcode});
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.title");
        title.forEach(string -> {
            assertTrue(string.contains("测试数据"));
        });
    }


    @Test
    @Description("正常测试用例：【政策文件】筛选关键字")
    @Story("政策文库")
    public void policyFileKeywordSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","测试数据");
        map.put("fileType",1);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.title");
        title.forEach(string -> {
            assertTrue(string.contains("测试数据"));
        });
    }

    @Test
    @Description("异常测试用例：【政策文件】关键字为空，筛选出所有数据")
    @Story("政策文库")
    public void policyFileKeywordEmptySearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","");
        map.put("fileType",1);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")!=0);
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> time=jsonPath.get("data.hits.dispatchDate");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(Integer.parseInt(time.get(i).replace("-",""))>=Integer.parseInt(time.get(i+1).replace("-",""))){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("异常测试用例：【政策文件】不存在的政策关键词，筛选结果为空")
    @Story("政策文库")
    public void policyFileKeywordFalseSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","null!");
        map.put("fileType",1);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：【公示公告】筛选关键字")
    @Story("政策文库")
    public void noticeKeywordSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","测试数据");
        map.put("fileType",2);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> title=jsonPath.get("data.hits.title");
        title.forEach(string -> {
            assertTrue(string.contains("测试数据"));
        });
    }

    @Test
    @Description("异常测试用例：【政策文件】关键字为空，筛选出所有数据")
    @Story("政策文库")
    public void noticeKeywordEmptySearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","");
        map.put("fileType",2);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")!=0);
        JsonPath jsonPath=new JsonPath(response.asString());
        List<String> time=jsonPath.get("data.hits.dispatchDate");
        boolean b=false;
        for(int i=0;i<time.size()-1;i++){
            if(Integer.parseInt(time.get(i).replace("-",""))>=Integer.parseInt(time.get(i+1).replace("-",""))){
                b=true;
            }else {
                b=false;
            }
        }
        assertTrue(b);
    }

    @Test
    @Description("异常测试用例：【政策文件】不存在的政策关键词，筛选结果为空")
    @Story("政策文库")
    public void noticeKeywordFalseSearchSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("keyWord","null!");
        map.put("fileType",2);
        Response response=policyPage.policySearch(map);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：根据政策id查询政策详情")
    @Story("政策文库")
    public void enterPolicyDetailSuccess(){
        HashMap<String,Object> policymap=policyPage.getPolicyList().get(0);
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyById.json");
        body.put("id",policymap.get("id"));
        Response response=policyPage.getPolicyDetail(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data.id"),policymap.get("id"));
        assertEquals(response.path("data.declare_date"),policymap.get("declareDate"));
        assertEquals(response.path("data.fw_department"),policymap.get("department"));
        assertEquals(response.path("data.city"),policymap.get("city"));
        assertEquals(response.path("data.title"),policymap.get("title"));
        assertTrue(response.path("data.projectNum")==(Integer)((List<HashMap<String,Object>>)policymap.get("projects")).size());
    }

    @Test
    @Description("异常测试用例：政策不存在为空，接口报错")
    @Story("政策文库")
    public void policyIdFailSearchFail(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyById.json");
        body.put("id",0);
        Response response=policyPage.getPolicyDetail(body);
        assertEquals((int)response.path("code"),500);
    }

    @Test
    @Description("正常测试用例：根据id查询项目数据")
    @Story("政策文库")
    public void enterPolicyProjectDetailSuccess(){
        HashMap<String,Object> policymap=policyPage.getPolicyList().get(0);
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyInterpretation.json");
        body.put("id",policymap.get("id"));
        Response response=policyPage.getProjectDetail(body);
        ArrayList<HashMap<String,Object>> projectsList1=(ArrayList<HashMap<String,Object>>)policymap.get("projects");
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> projectsList2=response.path("data");
       for(int i=0;i<projectsList1.size();i++){
           if((Integer) projectsList1.get(i).get("remainDay")<=0){
               assertEquals(projectsList2.get(i).get("declareDate"),"已截止");
           }
           if ((Integer) projectsList1.get(i).get("remainDay")>0){
               assertTrue((Integer) projectsList2.get(i).get("declareDate")>0);
           }
           assertEquals(projectsList2.get(i).get("id"),(projectsList1.get(i).get("id")));
           assertEquals(projectsList2.get(i).get("subtitle"),projectsList1.get(i).get("subtitle"));
           assertEquals(projectsList2.get(i).get("subtitleDescribe"),projectsList1.get(i).get("supportStandard"));
           assertFalse(((List)projectsList1.get(i).get("supportMethodList")).retainAll((List)projectsList2.get(i).get("supportMode")));
       }
    }

    @Test
    @Description("正常测试用例：项目id为空，查询所有项目")
    @Story("政策文库")
    public void projectIdEmptySearchSuccess(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyInterpretation.json");
        body.put("id","");
        Response response=policyPage.getProjectDetail(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(((List)response.path("data")).size()>10);

    }

    @Test
    @Description("正常测试用例：项目id不存在，查询为空")
    @Story("政策文库")
    public void projectIdFailSearchFail(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyInterpretation.json");
        body.put("id","99999999");
        Response response=policyPage.getProjectDetail(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(((List)response.path("data")).size()==0);

    }

    @Test
    @Description("正常测试用例：根据政策id获取政策文件")
    @Story("政策文库")
    public void getPolicyFileSuccess(){
        List<Integer> id= policyPage.getAllzhengceId();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetDeclarationNotice.json");
        body.put("id",id.get(0));
        Response response=policyPage.getPolicyFile(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(((String)response.path("data")).length()>0);
    }

    @Test
    @Description("正常测试用例：根据政策id获取相关文件")
    @Story("政策文库")
    public void getRelevantFileSuccess(){
        List<Object[]> id= JDBCUtils.queryMulti("select id from oa_zhengce where status='1' and id in(select DISTINCT(fid) from oa_zhengce_attachment);");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetRelevantFile.json");
        body.put("id",id.get(0)[0]);
        Response response=policyPage.getRevevanFile(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(((List)response.path("data")).size()>=2);
        List<HashMap<String,Object>> list=response.path("data");
        assertEquals(list.get(0).get("type"),1);
    }

    @Test
    @Description("正常测试用例：订阅成功")
    @Story("政策文库")
    public void subsribeSuccess(){
        List<Integer> yidingyue= policyPage.getYidingyue();
        List<Integer> allzhengce= policyPage.getAllzhengceId();
        allzhengce.removeAll(yidingyue);
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailSetSubscribe.json");
        body.put("zid",allzhengce.get(0));
        body.put("mId",homePage.getUserId());
        Response response=policyPage.subscribeSuccess(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("正常测试用例：取消订阅成功")
    @Story("政策文库")
    public void cancelSubsribeSuccess(){
        List<Integer> yidingyue= policyPage.getYidingyue();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailSetSubscribe.json");
        body.put("zid",yidingyue.get(0));
        body.put("mId",homePage.getUserId());
        body.put("type",1);
        Response response=policyPage.subscribeSuccess(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("正常测试用例：获取联系信息")
    @Story("政策文库")
    public void getContactSuccess(){
        List<Object[]> contact= JDBCUtils.queryMulti("select id,zhengceid,type,contact_unit,contact_name,contact_phone,contact_address,email,remark from oa_zhengce_contact");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetReportNotice.json");
        body.put("id",contact.get(0)[1]);
        Response response=policyPage.getContact(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<HashMap<String,Object>> rp=jsonPath.get("data.contact");
        List<Object[]> result= JDBCUtils.queryMulti("select id,zhengceid,type,contact_unit,contact_name,contact_phone,contact_address,email,remark from oa_zhengce_contact where zhengceid='"+contact.get(0)[1]+"';");
        for(int i=0;i<rp.size();i++) {
            assertEquals(rp.get(i).get("id"), result.get(i)[0]);
            assertEquals(rp.get(i).get("zhengceid"), result.get(i)[1]);
            assertEquals(rp.get(i).get("type"), result.get(i)[2]);
            assertEquals(rp.get(i).get("contactUnit"), result.get(i)[3]);
            assertEquals(rp.get(i).get("contactName"), result.get(i)[4]);
            assertEquals(rp.get(i).get("contactPhone"), result.get(i)[5]);
            assertEquals(rp.get(i).get("contactAddress"), result.get(i)[6]);
            assertEquals(rp.get(i).get("email"), result.get(i)[7]);
            assertEquals(rp.get(i).get("remark"), result.get(i)[8]);
        }
    }

    @Test
    @Description("正常测试用例：获取申报通知关联的公示名单")
    @Story("政策文库")
    public void getAnnounceInfoSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetAnnounceInfo.json");
        body.put("policyId",ann.get(0)[1]);
        Response response=policyPage.getAnnounceInfo(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data.tips"),"获批名单");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<HashMap<String,Object>> announceList=jsonPath.get("data.announceList");
        List<Object[]> annOne= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list where policy_id='"+ann.get(0)[1]+"';");
        for(int i=0;i<announceList.size();i++){
            assertEquals(announceList.get(i).get("announceId"),annOne.get(i)[0]);
            assertEquals(announceList.get(i).get("policyId"),annOne.get(i)[1]);
            assertEquals(announceList.get(i).get("projectId"),annOne.get(i)[2]);
            Object[] projectname=JDBCUtils.queryOne("SELECT subtitle from oa_formal_file where id='"+annOne.get(i)[2]+"';");
            assertEquals(announceList.get(i).get("projectName"),projectname[0]);
            assertEquals(announceList.get(i).get("year"),annOne.get(i)[3]);
            assertEquals(announceList.get(i).get("fond"),annOne.get(i)[4]);
            Object[] companyname=JDBCUtils.queryOne("SELECT company_name from t_enterprise_match_info where id='"+annOne.get(i)[5]+"';");
            assertEquals(announceList.get(i).get("companyName"),companyname[0]);
        }
        List<HashMap<String,Object>> noticeList=response.path("data.announce");
        for (HashMap<String,Object> map:noticeList){
            Object[] notice=JDBCUtils.queryOne("SELECT fw_department,dispatch_date,title from oa_zhengce where id='"+map.get("id")+"';");
            assertEquals(map.get("fw_department"),notice[0]);
            String date1=""+map.get("dispatch_date");
            String date2=""+notice[1];
            assertTrue(Integer.parseInt(date1.replace("-",""))==Integer.parseInt(date2.replace("-","")));
            assertEquals(map.get("title"),notice[2]);
        }
    }

    @Test
    @Description("正常测试用例：根据公司名称筛选公示名单")
    @Story("政策文库")
    public void companynameSearchSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        Object[] companyname=JDBCUtils.queryOne("SELECT company_name from t_enterprise_match_info where id='"+ann.get(0)[5]+"';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.json");
        body.put("policyId",ann.get(0)[1]);
        body.put("companyName",companyname[0]);
        Response response=policyPage.announceSearch(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data.announceList");
        list.forEach(map -> {
            String cname=""+map.get("companyName");
            assertTrue(cname.contains((String)companyname[0]));
        });
    }

    @Test
    @Description("异常测试用例：【公示名单】公司名称不存在，筛选为空")
    @Story("政策文库")
    public void companynameFalseSearchSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.json");
        body.put("policyId",ann.get(0)[1]);
        body.put("companyName","null");
        Response response=policyPage.announceSearch(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue((int)response.path("data.total")==0);
    }

    @Test
    @Description("正常测试用例：根据地区筛选公示名单")
    @Story("政策文库")
    public void districtCodeSearchSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.json");
        body.put("policyId",ann.get(0)[1]);
        body.put("districtCode",policyPage.getAnnounceDistictCode(ann.get(0)[1]));
        Response response=policyPage.announceSearch(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data.announceList");
        list.forEach(map -> {
            String code=""+map.get("provinceCode");
            assertTrue(code.contains(policyPage.getAnnounceDistictCode(ann.get(0)[1])));
        });
    }

    @Test
    @Description("正常测试用例：根据行业筛选公示名单")
    @Story("政策文库")
    public void industryCodeSearchSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.json");
        body.put("policyId",ann.get(0)[1]);
        List<String> industry=policyPage.getAnnounceIndustry(ann.get(0)[1]);
        body.put("industryCode",industry.get(0));
        Response response=policyPage.announceSearch(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data.announceList");
        list.forEach(map -> {
            String code=""+map.get("industry");
            assertTrue(code.contains(industry.get(1)));
        });
    }

    @Test
    @Description("正常测试用例：组合筛选公示名单")
    @Story("政策文库")
    public void allKeySearchSuccess(){
        List<Object[]> ann= JDBCUtils.queryMulti("select announce_id,policy_id,project_id,year,fond,enterprise_id from t_announce_list limit 0,100;");
        Object[] companyname=JDBCUtils.queryOne("SELECT company_name from t_enterprise_match_info where id='"+ann.get(0)[5]+"';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.json");
        body.put("policyId",ann.get(0)[1]);
        body.put("companyName",companyname[0]);
        body.put("districtCode",policyPage.getAnnounceDistictCode(ann.get(0)[1]));
        List<String> industry=policyPage.getAnnounceIndustry(ann.get(0)[1]);
        body.put("industryCode",industry.get(0));
        Response response=policyPage.announceSearch(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data.announceList");
        list.forEach(map -> {
            String industry1=""+map.get("industry");
            assertTrue(industry1.contains(industry.get(1)));
            String cname=""+map.get("companyName");
            assertTrue(cname.contains((String)companyname[0]));
            String code=""+map.get("provinceCode");
            assertTrue(code.contains(policyPage.getAnnounceDistictCode(ann.get(0)[1])));
        });
    }

    @Test
    @Description("正常测试用例：判断政策已申报")
    @Story("政策文库")
    public void isdeclareTure(){
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailHasAddDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("fieldId",declareid.get(0)[0]);
        Response response=policyPage.isDeclare(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        HashMap<String,Boolean> map=response.path("data");
        assertTrue(map.get("isDeclared"));
    }

    @Test
    @Description("正常测试用例：判断政策未申报")
    @Story("政策文库")
    public void isdeclareFalse(){
        List<Integer> policyid=policyPage.getAllzhengceId();
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        for(Object[] o:declareid){
            policyid.remove(o[0]);
        }
        System.out.println(policyid.size());
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailHasAddDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("fieldId",policyid.get(0));
        Response response=policyPage.isDeclare(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        HashMap<String,Boolean> map=response.path("data");
        assertFalse(map.get("isDeclared"));
    }

    @Test
    @Description("正常测试用例：申报政策成功")
    @Story("政策文库")
    public void declareSuccess(){
        List<Integer> policyid=policyPage.getAllzhengceId();
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        for(Object[] o:declareid){
            policyid.remove(o[0]);
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("zhengceId",policyid.get(0));
        body.put("contantName",fake.name().fullName());
        body.put("contantPhone",fake.phoneNumber().cellPhone());
        body.put("entityName","广东政沣云计算有限公司");
        Response response=policyPage.declare(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：联系人名称为空，申报失败")
    @Story("政策文库")
    public void contactnameEmptyDeclareFail(){
        List<Integer> policyid=policyPage.getAllzhengceId();
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        for(Object[] o:declareid){
            policyid.remove(o[0]);
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("zhengceId",policyid.get(0));
        body.put("contantName","");
        body.put("contantPhone",fake.phoneNumber().cellPhone());
        body.put("entityName","广东政沣云计算有限公司");
        Response response=policyPage.declare(body);
        assertEquals((int)response.path("code"),500);
        assertEquals(response.path("message"),"[contantName不能未空]");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：联系电话为空，申报失败")
    @Story("政策文库")
    public void phoneEmptyDeclareFail(){
        List<Integer> policyid=policyPage.getAllzhengceId();
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        for(Object[] o:declareid){
            policyid.remove(o[0]);
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("zhengceId",policyid.get(0));
        body.put("contantName",fake.name().fullName());
        body.put("contantPhone","");
        body.put("entityName","广东政沣云计算有限公司");
        Response response=policyPage.declare(body);
        assertEquals((int)response.path("code"),500);
        assertEquals(response.path("message"),"[contantPhone不能为空]");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：公司名称为空，申报失败")
    @Story("政策文库")
    public void companyEmptyDeclareFail(){
        List<Integer> policyid=policyPage.getAllzhengceId();
        List<Object[]> declareid=JDBCUtils.queryMulti("select zhengce_id from oa_member_declare where member_id='"+homePage.getUserId()+"';");
        for(Object[] o:declareid){
            policyid.remove(o[0]);
        }
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailDeclare.json");
        body.put("memberId",homePage.getUserId());
        body.put("zhengceId",policyid.get(0));
        body.put("contantName",fake.name().fullName());
        body.put("contantPhone",fake.phoneNumber().cellPhone());
        body.put("entityName","");
        Response response=policyPage.declare(body);
        assertEquals((int)response.path("code"),500);
        assertEquals(response.path("message"),"[entityName不能未空]");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("正常测试用例：纳入申报计划成功")
    @Story("政策文库")
    public void addDeclarePlanSuccess(){
        List<Integer> projectid=policyPage.getAllprohectId();
        List<Integer> declareplanid=policyPage.getDeclarePlanId();
        projectid.removeAll(declareplanid);
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpDeclaremanagerAddDeclarePlan.json");
        body.put("memberId",homePage.getUserId());
        body.put("formalId",projectid.get(0));
        body.put("type",0);
        Response response=policyPage.addDeclarePlan(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data"),"纳入成功");
    }

    @Test
    @Description("正常测试用例：取消纳入申报计划成功")
    @Story("政策文库")
    public void cancelDeclarePlanSuccess(){
        List<Integer> declareplanid=policyPage.getDeclarePlanId();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpDeclaremanagerAddDeclarePlan.json");
        body.put("memberId",homePage.getUserId());
        body.put("formalId",declareplanid.get(0));
        body.put("type",1);
        Response response=policyPage.addDeclarePlan(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data"),"取消纳入成功");
    }

    @Test
    @Description("异常测试用例：纳入已申报的计划失败")
    @Story("政策文库")
    public void repettionAddDeclarePlanSuccess(){
        List<Integer> declareplanid=policyPage.getDeclarePlanId();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpDeclaremanagerAddDeclarePlan.json");
        body.put("memberId",homePage.getUserId());
        body.put("formalId",declareplanid.get(5));
        body.put("type",0);
        Response response=policyPage.addDeclarePlan(body);
        assertEquals((int)response.path("code"),500);
        assertEquals(response.path("message"),"已存在");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("正常测试用例：获取公示公告关联的政策文件")
    @Story("政策文库")
    public void getNoticeRelatePolicyFileSuccess(){
        List<Object[]> id=JDBCUtils.queryMulti("select announce_id,policy_id from t_announce_policy where policy_id in(select id from oa_zhengce where status='1');");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpPolicyDetailGetPolicyFile.json");
        body.put("announceId",id.get(0)[0]);
        Response response=policyPage.getNoticeRelatePolicyFile(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        list.forEach(map -> {
            assertEquals(map.get("id"),id.get(0)[1]);
        });
    }

    @Test
    @Description("正常测试用例：获取搜索历史关键字")
    @Story("政策文库")
    public void getSearchHistorySuccess(){
        List<Object[]> sql=JDBCUtils.queryMulti("select keyword from ims_ewei_shop_member_search_history where search_type='1' and user_id='"+homePage.getUserId()+"' order by create_time desc;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpLibrarysearchGetMemberSearchHistory.json");
        body.put("memberId",homePage.getUserId());
        Response response=policyPage.getSearchHistory(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        List<HashMap<String,Object>> list=response.path("data");
        for (int i=0;i<list.size();i++){
            assertEquals(list.get(i).get("keyword"),sql.get(i)[0]);
        }
    }

    @Test
    @Description("正常测试用例：清空搜索历史关键字")
    @Story("政策文库")
    public void clearSearchHistorySuccess(){
        List<Object[]> sql=JDBCUtils.queryMulti("select keyword from ims_ewei_shop_member_search_history where search_type='1' and user_id='"+homePage.getUserId()+"' order by create_time desc;");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiPcpLibrarysearchDeleteMemberSearchHistory.json");
        body.put("ids",sql.get(0)[0]+","+sql.get(1)[0]);
        Response response=policyPage.clearSearchHistory(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data"),"删除成功");
    }

}