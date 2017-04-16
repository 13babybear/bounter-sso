package com.bounter.sso.service.impl;

import com.bounter.sso.dao.SSODao;
import com.bounter.sso.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by admin on 2017/4/16.
 */
@Service
public class SSOServiceImpl implements SSOService {
    @Autowired
    private SSODao ssoDao;

    @Override
    public boolean loginCheck(String userName, String password) {
        String credentialPassword = ssoDao.findCredential(userName);
        if (credentialPassword != null && credentialPassword.equals(password)) {
            return true;
        }
        return false;
    }
}
