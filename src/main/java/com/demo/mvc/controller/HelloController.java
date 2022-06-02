package com.demo.mvc.controller;

import com.demo.mvc.annotation.Autowired;
import com.demo.mvc.annotation.Controller;
import com.demo.mvc.annotation.RequestMapping;
import com.demo.mvc.annotation.RequestParam;
import com.demo.mvc.service.HelloService;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/mvc")
public class HelloController {

    @Autowired("HelloServiceImpl")
    private HelloService helloService;

    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam("name") String name,
                      @RequestParam("age") String age) {
        try {
            PrintWriter pw = response.getWriter();
            String result = helloService.query(name, age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/get")
    public void get(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter pw = response.getWriter();
            String result = helloService.get();
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
