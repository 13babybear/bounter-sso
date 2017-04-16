package com.bounter.sso.dao;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/16.
 */
@Repository
public class SSODao {
    private Map<String,String> credentials = new HashMap<>();
    public SSODao() {
        credentials.put("root","root");
    }

    public String findCredential(String userName) {
        return  credentials.get(userName);
    }
}
