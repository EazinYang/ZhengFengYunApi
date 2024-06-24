package page;

import conf.Api;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.testng.Assert.*;

public class ClareStewardPageTest {
    HomePage homePage=new HomePage();
    ClareStewardPage clareStewardPage=new ClareStewardPage();

    @Test
    @Description("正常测试用例：【申报计划】获取历史申报计划")
    @Story("政策文库")
    public void getLastDeclarePlan(){
        ArrayList<Integer> id=clareStewardPage.getDeclarePlanId();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerGetLastDeclarePlan.json");
        body.put("memberId",homePage.getUserId());
        Response response= clareStewardPage.getLastDeclarePlan(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<ArrayList<ArrayList<Integer>>> list=jsonPath.get("data.yearList.monthList.formalId");
        list.forEach(list1->{
            list1.forEach(list2->{
                list2.forEach(integer -> {
                    assertTrue(id.contains(integer));
                });
            });
        });
    }

    @Test
    @Description("正常测试用例：【申报计划】获取申报计划")
    @Story("政策文库")
    public void getDeclarePlan(){
        ArrayList<Integer> id=clareStewardPage.getDeclarePlanId();
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerGetLastDeclarePlan.json");
        body.put("memberId",homePage.getUserId());
        Response response= clareStewardPage.getDeclarePlan(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        JsonPath jsonPath=new JsonPath(response.asString());
        List<ArrayList<ArrayList<Integer>>> list=jsonPath.get("data.yearList.monthList.formalId");
        list.forEach(list1->{
            list1.forEach(list2->{
                list2.forEach(integer -> {
                    assertTrue(id.contains(integer));
                });
            });
        });
    }

    @Test
    @Description("正常测试用例：【佐证材料】查询全部佐证材料")
    @Story("政策文库")
    public void allGetSupportingMater(){
        List<Object[]> sql=JDBCUtils.queryMulti("select id from oa_aicompile_zuozheng_content where userid='"+homePage.getUserId()+"';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerGetSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        Response response= clareStewardPage.getSupportingMater(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> list=response.path("data.resultList");
        assertTrue(list.size()!=0);
        for(int i=0;i<list.size();i++){
            assertEquals(list.get(i).get("id"),sql.get(i)[0]);
        }
    }

    @Test
    @Description("正常测试用例：【佐证材料】根据父级条件查询佐证材料")
    @Story("政策文库")
    public void parentGetSupportingMater(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerGetSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        body.put("parentId",clareStewardPage.getClassification().get(0).get("id"));
        Response response= clareStewardPage.getSupportingMater(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> list=response.path("data.resultList");
        assertTrue(list.size()!=0);
        for(int i=0;i<list.size();i++){
            assertTrue((int)list.get(i).get("parentId")==(int)clareStewardPage.getClassification().get(0).get("id"));
        }
    }

    @Test
    @Description("正常测试用例：【佐证材料】根据子级条件查询佐证材料")
    @Story("政策文库")
    public void sonGetSupportingMater(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerGetSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        body.put("parentId",clareStewardPage.getClassification().get(0).get("id"));
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(0).get("childList");
        body.put("childId",child.get(0).get("id"));
        Response response= clareStewardPage.getSupportingMater(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        ArrayList<HashMap<String,Object>> list=response.path("data.resultList");
        assertTrue(list.size()!=0);
        for(int i=0;i<list.size();i++){
            assertTrue((int)list.get(i).get("parentId")==(int)clareStewardPage.getClassification().get(0).get("id"));
            assertTrue((int)list.get(i).get("childId")==(int)child.get(0).get("id"));
        }
    }

    @Test()
    @Description("正常测试用例：【佐证材料】设置有效期，保存佐证材料")
    @Story("政策文库")
    public void indateAddSupportingMaterials(){
        Response response1=clareStewardPage.uploadMaterials(new File("src/main/resources/property2.jpg"));
        HashMap<String,String> file=response1.path("data");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(1).get("childList");
        body.put("childId",child.get(1).get("id"));
        body.put("effectiveDate","2024-06-01~2024-06-30");
        body.put("remarkName","新文件");
        body.put("url",file.get("url"));
        body.put("fileName",file.get("fileName"));
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("正常测试用例：【佐证材料】设置无期限，保存佐证材料")
    @Story("政策文库")
    public void unlimitedAddSupportingMaterials(){
        Response response1=clareStewardPage.uploadMaterials(new File("src/main/resources/property2.jpg"));
        HashMap<String,String> file=response1.path("data");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(1).get("childList");
        body.put("childId",child.get(1).get("id"));
        body.put("effectiveDate","1");
        body.put("remarkName","新文件");
        body.put("url",file.get("url"));
        body.put("fileName",file.get("fileName"));
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("异常测试用例：【佐证材料】分类为空，保存佐证材料失败")
    @Story("政策文库")
    public void childidEmptyAddSupportingMaterials(){
        Response response1=clareStewardPage.uploadMaterials(new File("src/main/resources/property2.jpg"));
        HashMap<String,String> file=response1.path("data");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        body.put("childId","");
        body.put("remarkName","新文件");
        body.put("url",file.get("url"));
        body.put("fileName",file.get("fileName"));
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),500);
    }

    @Test()
    @Description("异常测试用例：【佐证材料】文件为空，保存佐证材料失败")
    @Story("政策文库")
    public void fileEmptyAddSupportingMaterials(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(0).get("childList");
        body.put("childId",child.get(0).get("id"));
        body.put("remarkName","新文件");
        body.put("url","");
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),500);
        assertEquals(response2.path("message"),"保存失败");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("异常测试用例：【佐证材料】有效期为空，保存佐证材料失败")
    @Story("政策文库")
    public void indateEmptyAddSupportingMaterials(){
        Response response1=clareStewardPage.uploadMaterials(new File("src/main/resources/property2.jpg"));
        HashMap<String,String> file=response1.path("data");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(0).get("childList");
        body.put("childId",child.get(0).get("id"));
        body.put("remarkName","新文件");
        body.put("effectiveDate","");
        body.put("url",file.get("url"));
        body.put("fileName",file.get("fileName"));
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),500);
        assertEquals(response2.path("message"),"有效日期不能为空");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("异常测试用例：【佐证材料】所有字段为空，保存佐证材料失败")
    @Story("政策文库")
    public void allEmptyAddSupportingMaterials(){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.json");
        body.put("memberId",homePage.getUserId());
        body.put("childId","");
        body.put("remarkName","");
        body.put("url","");
        body.put("fileName","");
        body.put("effectiveDate","");
        Response response2= clareStewardPage.addSupportingMaterials(body);
        assertEquals((int)response2.path("code"),500);
        assertEquals(response2.path("message"),"有效日期不能为空");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("正常测试用例：【佐证材料】编辑佐证材料")
    @Story("政策文库")
    public void updateSupportingMaterials(){
        List<Object[]> sql=JDBCUtils.queryMulti("select id from oa_aicompile_zuozheng_content where userid='"+homePage.getUserId()+"' and remark_name='新文件';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerUpdateMaterials.json");
        List<HashMap> child=(List<HashMap>) clareStewardPage.getClassification().get(0).get("childList");
        body.put("typeId",child.get(0).get("id"));
        body.put("remarkName","修改文件");
        body.put("effectiveDate","1");
        body.put("id",sql.get(0)[0]);
        Response response2= clareStewardPage.updateSupportingMaterials(body);
        assertEquals((int)response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        assertTrue(response2.path("data")==null);
    }

    @Test()
    @Description("异常测试用例：【佐证材料】分类为空，编辑佐证材料失败")
    @Story("政策文库")
    public void typeEmptyUpdateSupportingMaterials(){
        List<Object[]> sql=JDBCUtils.queryMulti("select id from oa_aicompile_zuozheng_content where userid='"+homePage.getUserId()+"';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerUpdateMaterials.json");
        body.put("typeId","");
        body.put("remarkName","修改文件");
        body.put("effectiveDate","1");
        body.put("id",sql.get(0)[0]);
        Response response2= clareStewardPage.updateSupportingMaterials(body);
        assertEquals((int)response2.path("code"),500);
        assertEquals(response2.path("message"),"类型不能为空");
        assertTrue(response2.path("data")==null);
    }

    @Test(priority = 5)
    @Description("正常测试用例：【佐证材料】删除佐证材料")
    @Story("政策文库")
    public void deleteSupportingMaterials(){
        List<Object[]> sql=JDBCUtils.queryMulti("select id from oa_aicompile_zuozheng_content where userid='"+homePage.getUserId()+"' and remark_name='修改文件';");
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/clareStewardPage/apiPcpDeclaremanagerDeleteSupportingMaterials.json");
        body.put("id",sql.get(0)[0]);
        Response response= clareStewardPage.deleteSupportingMaterials(body);
        assertEquals((int)response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertEquals(response.path("data"),"删除成功");
    }

}