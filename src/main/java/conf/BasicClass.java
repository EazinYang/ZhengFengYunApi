package conf;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import page.LoginPage;

import static io.restassured.RestAssured.given;

public class BasicClass extends Api{
    @Override
    public RequestSpecification getDefaultRequestSpecification(){
        RequestSpecification requestSpecification=super.getDefaultRequestSpecification();
        requestSpecification.header("Token",LoginPage.getLoginPage().getToken()).header("Pcpauth","\n" +
                "Bearer "+LoginPage.getLoginPage().getToken());
        return requestSpecification;
    }

}
