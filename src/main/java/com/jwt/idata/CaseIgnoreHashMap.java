package com.jwt.idata;

import java.io.Serializable;
import java.util.*;

public class CaseIgnoreHashMap implements Map, Serializable {

  private static final Long serialVersionUID = 4268511433216020229L;
  private Map hmap;
  private LinkedHashSet keySet;

  CaseIgnoreHashMap() {
    this.hmap = new HashMap<>();
    this.keySet = new LinkedHashSet<>();
  }


  @Override
  public int size() {
    return this.hmap.size();
  }

  @Override
  public boolean isEmpty() {
    return this.hmap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.hmap.containsKey(this.upper(key));
  }

  @Override
  public boolean containsValue(Object value) {
    return this.hmap.containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return this.hmap.get(this.upper(key));
  }

  @Override
  public Object put(Object key, Object value) {
    Object obj = this.hmap.put(this.upper(key), value);
    this.keySet.add(key);
    return obj;
  }

  @Override
  public Object remove(Object key) {
    Object obj = this.hmap.remove(this.upper(key));
    if (key == null) {
      this.keySet.remove(key);
    } else if (key instanceof String) {
      Iterator it = this.keySet.iterator();

      while (it.hasNext()) {
        Object each = it.next();
        if (each instanceof String && (key.toString()).equalsIgnoreCase(each.toString())) {
          this.keySet.remove(each);
          break;
        }
      }
    }
    return obj;
  }

  @Override
  public void putAll(Map t) {
    Iterator it = t.entrySet().iterator();

    while (it.hasNext()) {
      Entry ent = (Entry) it.next();
      this.hmap.put(this.upper(ent.getKey()), ent.getValue());
      this.keySet.add(ent.getKey());
    }
  }


  private Object upper(Object key) {
    if (key instanceof String) {
      key = ((String) key).toUpperCase();
    }
    return key;
  }

  @Override
  public void clear() {
    this.hmap.clear();
    this.keySet = new LinkedHashSet<>();
  }

  @Override
  public Set keySet() {
    return this.keySet;
  }

  @Override
  public Collection values() {
    return this.hmap.values();
  }

  @Override
  public Set<Entry> entrySet() {
    return this.hmap.entrySet();
  }
}
