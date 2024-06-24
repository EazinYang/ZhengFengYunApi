package page;

import conf.BasicClass;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import util.JDBCUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PolicyPage extends BasicClass {

    private ArrayList<String> countryDepartment=new ArrayList();
    private HashMap<String,String> cityCode=new HashMap<>();
    private HashMap<Object,Object> supportUuid=new HashMap<>();
    private List<HashMap<String,Object>> policyList=new ArrayList<>();
    HomePage homePage=new HomePage();
    List<Integer> yidingyue=new ArrayList<>();
    List<Integer> allzhengceId=new ArrayList<>();
    List<Integer> projectid=new ArrayList<>();
    List<Integer> declareplanid=new ArrayList<>();

    //政策查询
    public Response policySearch(HashMap<String,Object> map){
        Response response= getResponseFromYaml("/api/page/policyPage/apiQueryPolicyByPC.yaml",map);
        if(policyList.isEmpty()){
            policyList=response.path("data.hits");
        }
        return response;
    }

    //获取国家级主管部门
    public List<String> getCountryDepartment() {
        if (countryDepartment.size()==0){
            HashMap<String,Object> map=new HashMap<>();
            map.put("_file","/apijson/page/policyPage/apiGetDepartmentByArea.json");
            map.put("areas","000000,,");
            countryDepartment=getResponseFromYaml("/api/page/policyPage/apiGetDepartmentByArea.yaml",map).path("data");
            return countryDepartment;
        }else {
            return countryDepartment;
        }
    }

    //获取市级code
    public HashMap<String, String> getCityCode() {
        if (cityCode.isEmpty()){
            Response response=getResponseFromYaml("/api/page/policyPage/apiPcpArealetterGetNewLetterArea.yaml");
            JsonPath jsonPath=new JsonPath(response.asString());
            List<String> pinyin=jsonPath.get("data.hotAdress.pinyin");
            List<String> code=jsonPath.get("data.hotAdress.code");
            for(int i=0;i<pinyin.size();i++){
                cityCode.put(pinyin.get(i),code.get(i));
            }
            return cityCode;
        }else {
            return cityCode;
        }
    }

    //获取子级code
    public HashMap<String, String> getAreaByParentCode(String str) {
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/policyPage/apiGetAreaByParentCode.json");
        body.put("parentCode",str);
        ArrayList<HashMap<String,String>> list=getResponseFromYaml("/api/page/policyPage/apiGetAreaByParentCode.yaml",body).path("data");
        HashMap<String,String> districtCode=new HashMap<>();
        list.forEach(map1 -> {
            districtCode.put(map1.get("name"),map1.get("code"));
        });
        return districtCode;
    }

    //获取扶持方式
    public HashMap<Object, Object> getSupportUuid() {
        if (supportUuid.isEmpty()){
            List<List<HashMap<String,String>>> list=getResponseFromYaml("/api/page/policyPage/apiPcpPolicylibraryGetTags.yaml").path("data.children");
            list.get(0).forEach(map->{
                supportUuid.put(map.get("name"),map.get("uuid"));
            });
            return supportUuid;
        }else {
            return supportUuid;
        }
    }

    //获取政策详情
    public Response getPolicyDetail(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetPolicyById.yaml",map);
    }

    //获取列表页的政策信息
    public List<HashMap<String, Object>> getPolicyList() {
        if(policyList.isEmpty()){
            HashMap<String,Object> map=new HashMap<>();
            map.put("_file", "/apijson/page/policyPage/apiQueryPolicyByPC.json");
            policyList=policySearch(map).path("data.hits");
            return policyList;
        }else {
            return policyList;
        }
    }

    //获取政策下项目信息
    public Response getProjectDetail(HashMap<String,Object> map) {
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetPolicyInterpretation.yaml",map);
    }

    //获取政策下的文件正文
    public Response getPolicyFile(HashMap<String,Object> map) {
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetDeclarationNotice.yaml",map);
    }

    //获取政策下的相关文件
    public Response getRevevanFile(HashMap<String,Object> map) {
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetRelevantFile.yaml",map);
    }

    //获取政策订阅状态
    public String getSubscribeStatus(Integer integer){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/policyPage/apiQueryPolicyByPC.json");
        map.put("mid",homePage.getUserId());
        map.put("zid",integer);
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailSubscribe.yaml",map).path("data");
    }

    //订阅
    public Response subscribeSuccess(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailSetSubscribe.yaml",map);
    }

    //获取联系方式
    public Response getContact(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetReportNotice.yaml",map);
    }

    //获取申报通知关联的公示公告和公示名单
    public Response getAnnounceInfo(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpApprovedGetAnnounceInfo.yaml",map);
    }

    //筛选公示名单
    public Response announceSearch(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpApprovedGetApprovedListByUnitRegionIndustry.yaml",map);
    }

    //申报政策
    public Response declare(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailDeclare.yaml",map);
    }

    //判断是否已申报
    public Response isDeclare(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailHasAddDeclare.yaml",map);
    }

    //纳入申报计划
    public Response addDeclarePlan(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpDeclaremanagerAddDeclarePlan.yaml",map);
    }

    //获取公示公告关联的政策文件
    public Response getNoticeRelatePolicyFile(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpPolicyDetailGetPolicyFile.yaml",map);
    }

    //获取搜索历史关键字
    public Response getSearchHistory(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpLibrarysearchGetMemberSearchHistory.yaml",map);
    }

    //清空搜索历史关键字
    public Response clearSearchHistory(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/policyPage/apiPcpLibrarysearchDeleteMemberSearchHistory.yaml",map);
    }

    //获取公示名单的地区code
    public String getAnnounceDistictCode(Object policyid){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/policyPage/apiPcpApprovedGetDistrictByIndustryAndApprovalId.json");
        map.put("policyId",policyid);
        List<String> list=getResponseFromYaml("/api/page/policyPage/apiPcpApprovedGetDistrictByIndustryAndApprovalId.yaml",map).path("data.districtCode");
        return list.get(0);
    }

    //获取公示名单的行业
    public ArrayList<String> getAnnounceIndustry(Object policyid){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/policyPage/apiPcpApprovedGetIndustryByDistrictAndApprovalId.json");
        map.put("policyId",policyid);
        List<HashMap<String,String>> list=getResponseFromYaml("/api/page/policyPage/apiPcpApprovedGetIndustryByDistrictAndApprovalId.yaml",map).path("data");
        ArrayList<String> list1=new ArrayList<>();
        list1.add(list.get(0).get("industry_code"));
        list1.add(list.get(0).get("industry"));
        return list1;
    }

    //获取已订阅的政策id
    public List<Integer> getYidingyue() {
        if(yidingyue.size()==0){
            List<Object[]> objects=JDBCUtils.queryMulti("select fid from ims_ewei_shop_member_favorite  b INNER JOIN oa_zhengce on b.fid=oa_zhengce.id where deleted='0' and member_id='"+homePage.getUserId()+"';");
            for (Object[] objects1:objects){
                yidingyue.add((Integer) objects1[0]);
            }
            return yidingyue;
        }else {
            return yidingyue;
        }
    }

    //获取所有政策id
    public List<Integer> getAllzhengceId() {
        if(allzhengceId.size()==0){
            List<Object[]> objects=JDBCUtils.queryMulti("select id from oa_zhengce where status='1';");
            for (Object[] objects1:objects){
                allzhengceId.add((Integer) objects1[0]);
            }
            return allzhengceId;
        }else {
            return allzhengceId;
        }
    }

    //获取所有项目id
    public List<Integer> getAllprohectId() {
        if(projectid.size()==0){
            List<Object[]> objects=JDBCUtils.queryMulti("select id from oa_formal_file where status='1';");
            for (Object[] objects1:objects){
                projectid.add((Integer) objects1[0]);
            }
            return projectid;
        }else {
            return projectid;
        }
    }

    //获取已纳入计划的项目id
    public List<Integer> getDeclarePlanId() {
        if(declareplanid.size()==0){
            List<Object[]> objects=JDBCUtils.queryMulti("select formal_id from oa_declare_plan where member_id='"+homePage.getUserId()+"';");
            for (Object[] objects1:objects){
                declareplanid.add((Integer) objects1[0]);
            }
            return declareplanid;
        }else {
            return declareplanid;
        }
    }
}
