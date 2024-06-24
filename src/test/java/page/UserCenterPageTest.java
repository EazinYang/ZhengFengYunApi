package page;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import util.JDBCUtils;

import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

public class UserCenterPageTest {
    UserCenterPage userCenterPage=new UserCenterPage();
    HomePage homePage=new HomePage();

    @Test
    @Description("正常测试用例：获取已读政策")
    @Story("用户中心")
    public void findsReadZC(){
        List<Object[]> sql= JDBCUtils.queryMulti("SELECT id from oa_zhengce where status=1 and zhijian_status=5 and id not in (SELECT zid FROM applet_read_policy where member_id='"+homePage.getUserId()+"');");
        HashMap<String,Object> body1=new HashMap<>();
        body1.put("_file","/apijson/page/userCenterPage/apiPcpPolicyDetailAddZCHistory.json");
        body1.put("zid",sql.get(0)[0]);
        body1.put("memberId",homePage.getUserId());
        Response response1= userCenterPage.addZCHistory(body1);
        assertEquals(response1.path("message"),"操作成功");
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/userCenterPage/apiPcpPersonalCenterFindIsReadZC.json");
        map.put("memberId",homePage.getUserId());
        Response response2= userCenterPage.findsReadZC(map);
        assertEquals((int) response2.path("code"),200);
        assertEquals(response2.path("message"),"操作成功");
        List<Integer> list=response2.path("data.id");
        assertEquals(list.get(0),sql.get(0)[0]);
    }
}