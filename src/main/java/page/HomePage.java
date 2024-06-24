package page;

import conf.BasicClass;

public class HomePage extends BasicClass {
    private Integer userId;

    public Integer getUserId() {
        if (userId==null){
            userId=getResponseFromYaml("/api/page/homePage/apiPcpPersonalCenterV2GetAuthResult.yaml").path("data.userId");
            return userId;
        }else {
            return userId;
        }
    }
}
