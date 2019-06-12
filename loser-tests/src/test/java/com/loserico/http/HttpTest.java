package com.loserico.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpTest {
	public static void main(String[] args){
        String reqUrl = "https://pd-erp-t2.noahgrouptest.com/service/"
        		+ "IHttpServletAdaptorimpl?tdd=tdd&tddtp="
        		+ "material&token=07a81a68-c60c-4ee4-83dc-25aa51a51497";
        JSONObject jsonObj = new JSONObject();
         //物料
		 jsonObj.put("pk_org", "101");
		 jsonObj.put("code", "1234567890181");
		 jsonObj.put("name", "vivo Xxx2001----test20");
		 jsonObj.put("pk_marbasclass", "京东SKU");
		 jsonObj.put("pk_measdoc", "个");
		 jsonObj.put("pk_mattaxes", "CN001");
		 jsonObj.put("intolerance", "0");
		 jsonObj.put("outtolerance", "0");
		 jsonObj.put("outcloselowerlimit", "0");
		 jsonObj.put("productfamily", "what");
		System.out.println(jsonObj.toString());
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost=new HttpPost(reqUrl);
        StringEntity entity;
		entity = new StringEntity(jsonObj.toString(),"GBK");
		entity.setContentEncoding("GBK");
		entity.setContentType("application/json");
   
        httpPost.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            //200为成功！
            System.out.println("连接状态为："+statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed:" + response.getStatusLine());
            }else{
                String resultStr = EntityUtils.toString(response.getEntity());
//                JSONObject resultJSON=JSONObject.fromObject(resultStr);
                System.out.println("resultJSON:"+resultStr);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
