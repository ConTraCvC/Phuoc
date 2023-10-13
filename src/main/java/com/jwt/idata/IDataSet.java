package com.jwt.idata;

import java.io.FileNotFoundException;
import java.util.Iterator;

public interface IDataSet {
  String putField(String key, String value) throws FileNotFoundException;

  String getField(String key);

  Iterator getFieldKeys();
}
