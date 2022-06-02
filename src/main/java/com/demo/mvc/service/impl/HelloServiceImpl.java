package com.demo.mvc.service.impl;

import com.demo.mvc.annotation.Service;
import com.demo.mvc.service.HelloService;

@Service("HelloServiceImpl")
public class HelloServiceImpl implements HelloService {
    public String query(String name, String age) {
        return "name:" + name + "  age:" + age;
    }

    @Override
    public String get() {
        return "test get请求";
    }
}
