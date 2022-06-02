package com.demo.mvc.servlet;

import com.demo.mvc.annotation.*;
import com.demo.mvc.controller.HelloController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    //保存路径下所有的class
    List<String> classNames = new ArrayList<String>();

    //bean容器
    Map<String, Object> beans = new HashMap<String, Object>();

    //映射，路径与方法的映射
    Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //扫描controller、service
        scanPackage("com.demo");

        //实例化，通过反射实例
        instance();

        //属性注入
        doAutoWeired();

        //path-controller层的method映射
        UrlHandler();

    }

    //path-controller层的method映射
    public void UrlHandler() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                //获取类路径，/mvc
                RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
                String classPath = mapping.value();

                //获取方法路径
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    //找到带有RequestMapping注解的方法
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        //    /query
                        String methodPath = method.getAnnotation(RequestMapping.class).value();
                        handlerMap.put(classPath + methodPath, method);
                    }
                }

            }
        }
    }

    //对容器中的bean进行属性注入
    public void doAutoWeired() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                //获取所有属性
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    //找到autowired注解的属性
                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired name = field.getAnnotation(Autowired.class);
                        Object o = beans.get(name.value());
                        field.setAccessible(true);//打开权限
                        try {
                            field.set(instance, o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (clazz.isAnnotationPresent(Service.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired name = field.getAnnotation(Autowired.class);
                        Object o = beans.get(name.value());
                        field.setAccessible(true);//打开权限
                        try {
                            field.set(instance, o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    //对所有的class进行判断，是否有controller、service注解。有则实例化，放入容器中
    public void instance() {
        for (String className : classNames) {
            //com.demo.mvc.service.impl.HelloServiceImpl.class
            //com.demo.mvc.annotation.Autowired.class
            //com.demo.mvc.controller.HelloController.class
            //等
            String cn = className.replace(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //控制类
                    Object instance = clazz.newInstance();
                    //获取beanName，/mvc。@RequestMapping("/mvc")
                    RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
                    beans.put(mapping.value(), instance);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    //服务类
                    Object instance = clazz.newInstance();
                    //获取beanName
                    Service mapping = clazz.getAnnotation(Service.class);
                    beans.put(mapping.value(), instance);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void scanPackage(String basePackage) {
        //url=com.demo.mvc
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        //   /D:/work/workspace/springmvcdemo1/out/artifacts/springmvcdemo1_war_exploded/WEB-INF/classes/com/demo/
        String filStr = url.getFile();

        File file = new File(filStr);
        String[] fileStr = file.list();
        for (String path : fileStr) {
            File filePath = new File(filStr + path);
            if (filePath.isDirectory()) {
                scanPackage(basePackage + "." + path);
            } else {
                //com.demo.xxx.xxx.class
                classNames.add(basePackage + "." + filePath.getName());

            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();   //  /pro/mvc/query
        String context = req.getContextPath();  //  /pro
        String path = uri.replace(context, "");//  /mvc/query
        Method method = (Method) handlerMap.get(path);

        //根据 /mvc 找到实例
        HelloController instance = (HelloController) beans.get("/" + path.split("/")[1]);
        Object args[] = handleArgs(req, resp, method);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // http://ip+port/pro/mvc/query

        String uri = req.getRequestURI();   //  /pro/mvc/query
        String context = req.getContextPath();  //  /pro
        String path = uri.replace(context, "");//  /mvc/query
        Method method = (Method) handlerMap.get(path);

        //根据 /mvc 找到实例
        HelloController instance = (HelloController) beans.get("/" + path.split("/")[1]);


        Object args[] = handleArgs(req, resp, method);
        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Object[] handleArgs(HttpServletRequest request, HttpServletResponse response, Method method) {
        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        int args_i = 0;
        int index = 0;
        for (Class<?> paramClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[args_i++] = request;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[args_i++] = response;
            }
            // 从0-3判断有没有requestParam注解；
            // 0为request
            // 1为response
            //判断2,3有没有requestParam注解；
            Annotation[] paramAns = method.getParameterAnnotations()[index];
            if (paramAns.length > 0) {
                for (Annotation paramAn : paramAns) {
                    if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        RequestParam rp = (RequestParam) paramAn;

                        args[args_i++] = request.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }

}

