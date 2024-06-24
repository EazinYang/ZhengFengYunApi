package page;

import conf.BasicClass;
import conf.Restful;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import util.JDBCUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClareStewardPage extends BasicClass {
    HomePage homePage=new HomePage();
    public ArrayList<Integer> declarePlanId=new ArrayList<>();
    public ArrayList<HashMap<String,Object>> classification=new ArrayList<>();

    //获取历史申报计划
    public Response getLastDeclarePlan(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerGetLastDeclarePlan.yaml",map);
    }

    //获取历史申报计划
    public Response getDeclarePlan(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerGetDeclarePlan.yaml",map);
    }

    //查询佐证材料
    public Response getSupportingMater(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerGetSupportingMaterials.yaml",map);
    }

    //上传佐证材料
    public Response uploadMaterials(File file){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerUpLoadMaterials.yaml",file);
    }

    //保存佐证材料
    public Response addSupportingMaterials(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerAddSupportingMaterials.yaml",map);
    }

    //编辑佐证材料
    public Response updateSupportingMaterials(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerUpdateMaterials.yaml",map);
    }

    //删除佐证材料
    public Response deleteSupportingMaterials(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerDeleteSupportingMaterials.yaml",map);
    }

    //获取佐证材料分类
    public ArrayList<HashMap<String, Object>> getClassification(){
        if (classification.isEmpty()){
            classification=getResponseFromYaml("/api/page/clareStewardPage/apiPcpDeclaremanagerGetClassification.yaml").path("data");
            return classification;
        }
        return classification;
    }

    //获取申报计划
    public ArrayList<Integer> getDeclarePlanId(){
        if(declarePlanId.isEmpty()){
            List<Object[]> sql= JDBCUtils.queryMulti("select formal_id from oa_declare_plan where member_id='"+homePage.getUserId()+"';");
            for(Object[] objects:sql){
                declarePlanId.add((Integer) objects[0]);
            }
            return declarePlanId;
        }
        return declarePlanId;
    }

}
