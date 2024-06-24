package page;

import conf.Api;
import io.restassured.response.Response;
import java.util.HashMap;

public class LoginPage extends Api {
    private String token;

    private static LoginPage loginPage;

    private LoginPage(){}

    public static LoginPage getLoginPage() {
        if(loginPage==null){
            loginPage=new LoginPage();
            return loginPage;
        }
        return loginPage;
    }

    //获取图片验证码
    public String getImageVercode(){
        return getResponseFromYaml("/api/page/loginPage/apiPcpYzmGetVerifyCode.yaml").path("data.key");
    }

    //【密码登录】登录成功
    public Response passwordLoginSuccess(HashMap<String, Object> map){
        return getResponseFromYaml("/api/page/loginPage/apiPcpLogin.yaml",map);
    }

    //【验证码登录】登录成功
    public Response vercodeLoginSuccess(HashMap<String, Object> map){
        return getResponseFromYaml("/api/page/loginPage/apiPcpVerLogin.yaml",map);
    }

    public String getToken() {
        if(token==null){
            HashMap<String,Object> map=new HashMap<>();
            map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
            token=getResponseFromYaml("/api/page/loginPage/apiPcpVerLogin.yaml",map).path("data");
            return token;
        }else {
            return token;
        }
    }

}
