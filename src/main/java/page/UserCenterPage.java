package page;

import conf.BasicClass;
import io.restassured.response.Response;

import java.util.HashMap;

public class UserCenterPage extends BasicClass {
    //获取已读政策
    public Response findsReadZC(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/userCenterPage/apiPcpPersonalCenterFindIsReadZC.yaml",map);
    }

    //记录为已读政策
    public Response addZCHistory(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/userCenterPage/apiPcpPolicyDetailAddZCHistory.yaml",map);
    }
}
