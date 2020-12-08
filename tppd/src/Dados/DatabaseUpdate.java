package Dados;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUpdate implements Serializable {
    private static long serialVersionUID=1L;
    private Map<String, Map<String,Long>> data;

    public DatabaseUpdate(){
        data = new HashMap<>();
    }

    public void setData(Map<String, Map<String, Long>> data) {
        this.data = data;
    }
    public Map<String,Map<String,Long>> getData(){
        return data;
    }
}
