package com.app.utils;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Transient;

public class Filtro implements Serializable {
    
    private String name;
    
    private String type;
    @Transient
    private List<Object> values;
    
    private String operator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
