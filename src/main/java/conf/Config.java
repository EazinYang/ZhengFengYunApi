package conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.HashMap;

public class Config {
    private static Config config=null;
    public String current;
    public HashMap<String,HashMap<String,String>> env;
    public String username;
    public String password;

    public static Config getInstance(){
        if (config==null){
            config=load("/conf/config.yaml");
            return config;
        }else {
            return config;
        }
    }

    public static Config load(String path){
        ObjectMapper objectMapper=new ObjectMapper(new YAMLFactory());
        try{
            Config config =objectMapper.readValue(Config.class.getResource(path),Config.class);
            return config;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
