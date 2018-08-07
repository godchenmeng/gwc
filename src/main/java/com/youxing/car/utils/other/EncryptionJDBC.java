package com.youxing.car.utils.other;

import com.alibaba.druid.pool.DruidDataSource;

public class EncryptionJDBC extends DruidDataSource {
    public void setPassword(String password){super.setPassword(Encryption.decrypt(password));}
}
