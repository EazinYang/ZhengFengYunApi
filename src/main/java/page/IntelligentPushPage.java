package page;

import conf.BasicClass;
import io.restassured.response.Response;
import java.util.HashMap;

public class IntelligentPushPage extends BasicClass {
    public Integer userPushSetId=0;

    //筛选适配项目
    public Response searchProjectPush(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusFormal.yaml",map);
    }

    //立即匹配项目
    public Response matchProject(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apipcpnewPersonalCenterUpdateIntelligentPush.yaml",map);
    }

    //获取匹配次数
    public Response getMatchProjectNum(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpNewPersonalCenterGetIntelligentPushNum.yaml",map);
    }

    //获取推送设置二维码
    public Response getQRCode(){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpOfficialAccountsGetQRCode.yaml");
    }

    //获取推送设置信息
    public Response getBackSysUserPushSet(Integer userid){
        HashMap<String,Object> body=new HashMap<>();
        body.put("_file","/apijson/page/intelligentPushPage/apiPcpIntelligentPushGetBackSysUserPushSet.json");
        body.put("userId",userid);
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushGetBackSysUserPushSet.yaml",body);
    }

    //修改用户推送设置信息
    public Response updateBackSysUserPushSet(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushUpdateBackSysUserPushSet.yaml",map);
    }

    //获取推送记录id
    public Integer getBackSysUserPushSetId(Integer userid){
        if(userPushSetId==0){
            userPushSetId=getBackSysUserPushSet(userid).path("data.id");
            return userPushSetId;
        }else {
            return userPushSetId;
        }
    }

    //修改关注设置
    public Response focusPolicySet(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushFocusPolicySet.yaml",map);
    }

    //查询关注设置信息
    public Response getFocusPolicySetInfo(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushGetFocusPolicySetting.yaml",map);
    }

    //查询关注政策
    public Response getGsIFocusPolicy(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushGetGslFocusPolicy.yaml",map);
    }

    //查询我的订阅
    public Response mySubscribe(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/intelligentPushPage/apiPcpIntelligentPushMySubscribe.yaml",map);
    }
}

