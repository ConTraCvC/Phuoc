package com.jwt.idata;

import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
public class DataSet implements IDataSet{
  private static final long serialVersionUID = -730048143080606765L;
  private Map fieldMap = new CaseIgnoreHashMap();
  private Map recordSetMap = new LinkedHashMap();
  private Object object = null;

  @Override
  public String putField(String key, String value)  {
    Object prevValue = this.putObjectFieldInternal(key, value);
    return (String) prevValue;
  }

  public Object putObjectFieldInternal(String key, Object value) {
    return this.fieldMap.put(key, value);
  }

  @Override
  public String getField(String key) {
    Object value = this.getObjectField(key);
    return (String) getTargetValue(value.toString());
  }

  public static Object getTargetValue(String value) {
    if (value == null) {
      return null;
    } else if (value.getClass().isArray()) {
      return Array.getLength(value) > 0 ? Array.get(value, 0) : null;
    } else {
      return value;
    }
  }

  public Object getObjectField(String key) {
    return this.fieldMap.get(key);
  }

  @Override
  public Iterator getFieldKeys(){
    return this.fieldMap.keySet().iterator();
  }

}
