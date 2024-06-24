package page;

import com.github.javafaker.Faker;
import conf.Api;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Locale;
import static org.testng.Assert.*;

public class RegisterPageTest {
    Faker faker=new Faker(Locale.CHINA);
    RegisterPage registerPage=new RegisterPage();

    @Test
    @Description("正常测试用例：注册成功")
    @Story("注册")
    public void registerSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name",faker.name().fullName());
        map.put("phoneNumber",faker.phoneNumber().cellPhone());
        map.put("password","123456a");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    //接口层注册成功
    @Test
    @Description("异常测试用例：姓名为空，注册成功")
    @Story("注册")
    public void nameEmptyRegisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name","");
        map.put("phoneNumber",faker.phoneNumber().cellPhone());
        map.put("password","123456a");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：手机号为空，注册失败")
    @Story("注册")
    public void phoneEmptyRegisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name",faker.name().fullName());
        map.put("phoneNumber","");
        map.put("password","123456a");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),500);
        assertFalse(((String)response.path("message")).contains("操作成功"));
        assertTrue(response.path("data")==null);
    }

    //当前接口可以注册成功，前端做了限制
    @Test
    @Description("异常测试用例：密码号为空，注册失败")
    @Story("注册")
    public void passwordEmptyRegisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name",faker.name().fullName());
        map.put("phoneNumber",faker.phoneNumber().cellPhone());
        map.put("password","");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：验证码号为空，注册失败")
    @Story("注册")
    public void codeEmptyRegisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name",faker.name().fullName());
        map.put("phoneNumber",faker.phoneNumber().cellPhone());
        map.put("password","123456a");
        map.put("code","");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertEquals(response.path("message"),"验证码不能为空");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：验证码号错误，注册失败")
    @Story("注册")
    public void codeFalseRegisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name",faker.name().fullName());
        map.put("phoneNumber",faker.phoneNumber().cellPhone());
        map.put("password","123456a");
        map.put("code","12345");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertEquals(response.path("message"),"验证码错误或已过期");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：所有字段为空，注册失败")
    @Story("注册")
    public void allEmptyregisterFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpRegister.json");
        map.put("name","");
        map.put("phoneNumber","");
        map.put("password","");
        map.put("code","");
        Response response= registerPage.registerSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertEquals(response.path("message"),"参数不能为空");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("正常测试用例：获取验证码成功")
    @Story("注册")
    public void getVercodeSuccess(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpVerification.json");
        map.put("phone",faker.phoneNumber().cellPhone());
        Response response= registerPage.getVercodeSuccess(map);
        assertEquals((int) response.path("code"),200);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")==null);
    }

    @Test
    @Description("异常测试用例：手机号为空，获取验证码失败")
    @Story("注册")
    public void phoneEmptyGetVercodeFail(){
        HashMap<String,Object> map=new HashMap<>();
        map.put("_file","/apijson/page/regitsterPage/apiPcpVerification.json");
        map.put("phone","");
        Response response= registerPage.getVercodeSuccess(map);
        assertEquals((int) response.path("code"),500);
        assertFalse(((String)response.path("message")).contains("操作成功"));
        assertTrue(response.path("data")==null);
    }
}