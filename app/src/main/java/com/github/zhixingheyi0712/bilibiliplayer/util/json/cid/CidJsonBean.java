/**
  * Copyright 2020 bejson.com 
  */
package com.github.zhixingheyi0712.bilibiliplayer.util.json.cid;
import java.util.List;

/**
 * Auto-generated: 2020-07-14 9:27:50
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class CidJsonBean {

    private int code;
    private String message;
    private int ttl;
    private List<Data> data;
    public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setTtl(int ttl) {
         this.ttl = ttl;
     }
     public int getTtl() {
         return ttl;
     }

    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

}