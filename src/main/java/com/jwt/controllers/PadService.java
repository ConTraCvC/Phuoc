package com.jwt.controllers;

import org.springframework.stereotype.Service;

@Service
public class PadService {
  public String lpad(String str, int length, String pad) {
    if (str == null) {
      str = "";
    }
    if (pad == null) {
      pad = " ";
    }

    if (str.length() > length) {
      return str.substring(0, length);
    } else if (str.length() == length) {
      return str;
    } else {
      int blkLength = length - str.length();
      int count = blkLength / pad.length();
      int mod = blkLength % pad.length();

      StringBuilder sb;
      for (sb = new StringBuilder(); count > 0; --count) {
        sb.append(pad);
      }

      if (mod > 0) {
        sb.append(pad, 0, mod);
      }
      sb.append(str);
      return sb.toString();
    }
  }

  public String rpad(String str, int length, String pad) {
    if (str == null) {
      str = "";
    }
    if (pad == null) {
      pad = " ";
    }
    int srcLength = str.length();
    if (srcLength > length) {
      return str.substring(srcLength - length);
    } else if (srcLength == length) {
      return str;
    } else {
      int padLength = pad.length();
      int blkLength = length - srcLength;
      int count = blkLength / padLength;
      int mod = blkLength % padLength;

      StringBuilder sb = new StringBuilder();
      sb.append(str);

      while (count > 0 ) {
        sb.append(pad);
        --count;
      }

      if (mod > 0) {
        sb.append(pad, 0, mod);
      }
      return sb.toString();
    }
  }
}
