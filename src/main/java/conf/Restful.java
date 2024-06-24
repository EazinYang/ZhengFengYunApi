package conf;

import java.util.HashMap;

public class Restful {
    public String url;
    public String method;
    public int param;
    public HashMap<String,String> header=new HashMap<>();
    public HashMap<String,Object> query=new HashMap<>();
    public String body;
}
