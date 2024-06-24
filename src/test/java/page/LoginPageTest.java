package page;

import conf.Api;
import conf.Config;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.HashMap;

import static org.testng.Assert.*;

public class LoginPageTest {
    Config config=Config.getInstance();

    @Test
    @Description("正常测试用例：【密码登录】用户名、密码、验证码正确，登录成功")
    @Story("登录")
    public void passwordLoginSuccess(){
        String code=LoginPage.getLoginPage().getImageVercode();
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username",config.username);
        map.put("password",config.password);
        map.put("code",code.substring(6));
        map.put("key",code);
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals(response.path("message"),"操作成功");
        assertTrue(response.path("data")!=null);
    }


    @Test
    @Description("异常测试用例：【密码登录】用户名错误，登录失败")
    @Story("登录")
    public void usernameFalseLoginFail(){
        String code=LoginPage.getLoginPage().getImageVercode();
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username","1");
        map.put("password",config.password);
        map.put("code",code.substring(code.length()-4));
        map.put("key",code);
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"用户名或密码错误");
    }

    @DataProvider(name = "密码")
    public String[][] passwordData(){
        return new String[][]{
            {""},{"1"}
        };
    }

    @Test(dataProvider = "密码")
    @Description("异常测试用例：【密码登录】密码为空、错误，登录失败")
    @Story("登录")
    public void passwordEmptyLoginFail(String password){
        String code=LoginPage.getLoginPage().getImageVercode();
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username",config.username);
        map.put("password",password);
        map.put("code",code.substring(code.length()-4));
        map.put("key",code);
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"用户名或密码错误");
    }

    @DataProvider(name = "图片验证码")
    public String[][] vercodeData(){
        return new String[][]{
                {""},{"1"}
        };
    }

    @Test(dataProvider = "图片验证码")
    @Description("异常测试用例：【密码登录】验证码为空、错误，登录失败")
    @Story("登录")
    public void vercodeEmptyLoginFail(String vercode){
        String code=LoginPage.getLoginPage().getImageVercode();
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username",config.username);
        map.put("password",config.password);
        map.put("code",vercode);
        map.put("key",code);
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"输入的验证码不一致");
    }

    @DataProvider(name = "验证码Key")
    public String[][] keyData(){
        return new String[][]{
                {""},{"1"}
        };
    }

    @Test(dataProvider = "验证码Key")
    @Description("异常测试用例：【密码登录】验证码的key为空、错误，登录失败")
    @Story("登录")
    public void keyEmptyLoginFail(String key){
        String code=LoginPage.getLoginPage().getImageVercode();
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username",config.username);
        map.put("password",config.password);
        map.put("code",code.substring(code.length()-4));
        map.put("key",key);
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"验证码已过期");
    }

    @Test()
    @Description("异常测试用例：【密码登录】所有字段为空，登录失败")
    @Story("登录")
    public void allEmptyLoginFail(){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpLogin.json");
        map.put("username","");
        map.put("password","");
        map.put("code","");
        map.put("key","");
        Response response=LoginPage.getLoginPage().passwordLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"验证码已过期");
    }

    @Test()
    @Description("正常测试用例：【验证码登录】手机号、验证码正确，登录成功")
    @Story("登录")
    public void vercodeLoginSuccess(){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone",config.username);
        Response response=LoginPage.getLoginPage().vercodeLoginSuccess(map);
        assertEquals((int) response.path("code"),200);
        assertTrue(response.path("data")!=null);
        assertEquals(response.path("message"),"操作成功");
    }

    @Test()
    @Description("异常测试用例：【验证码登录】手机号为空，登录失败")
    @Story("登录")
    public void phoneEmptyLoginFail(){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone","");
        Response response=LoginPage.getLoginPage().vercodeLoginSuccess(map);
        assertEquals((int) response.path("code"),500);
        assertTrue(response.path("data")==null);
    }

    @Test()
    @Description("异常测试用例：【验证码登录】手机号为空，登录失败")
    @Story("登录")
    public void phoneFalseLoginFail1(){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone","1357924680");
        Response response=LoginPage.getLoginPage().vercodeLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"用户不存在或者已禁用");
    }

    @DataProvider(name = "短信验证码")
    public String[][] vercodeMessageData(){
        return new String[][]{
                {""},{"1"}
        };
    }


    @Test(dataProvider = "短信验证码")
    @Description("异常测试用例：【验证码登录】验证码为空，登录失败")
    @Story("登录")
    public void vercodeNullLoginFail(String code){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone",config.username);
        map.put("code",code);
        Response response=LoginPage.getLoginPage().vercodeLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"验证码错误或已过期");
    }

    @Test
    @Description("异常测试用例：【验证码登录】手机号、验证码为空，登录失败")
    @Story("登录")
    public void allEmptyVercodeLoginFail(){
        HashMap<String, Object> map=new HashMap<>();
        map.put("_file", "/apijson/page/loginPage/apiPcpVerLogin.json");
        map.put("phone","");
        map.put("code","");
        Response response=LoginPage.getLoginPage().vercodeLoginSuccess(map);
        assertEquals((int) response.path("code"),400);
        assertTrue(response.path("data")==null);
        assertEquals(response.path("message"),"验证码错误或已过期");
    }
}