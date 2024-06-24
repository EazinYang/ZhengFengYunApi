package page;

import conf.Api;
import io.restassured.response.Response;

import java.util.HashMap;

public class RegisterPage extends Api {
    //注册
    public Response registerSuccess(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/registerPage/apiPcpRegister.yaml",map);
    }

    //获取验证码
    public Response getVercodeSuccess(HashMap<String,Object> map){
        return getResponseFromYaml("/api/page/registerPage/apiPcpVerification.yaml",map);
    }
}
