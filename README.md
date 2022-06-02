# simple-springMVC
手写springMVC，实现了简单的get、post请求
1. 安装JDK
2. 安装Tomcat
  a. 教程https://www.cnblogs.com/lpgit/p/10929507.html#:~:text=Tomcat%E7%9A%84%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AE%201%20%E5%8E%BB%E5%AE%98%E7%BD%91%E4%B8%8B%E8%BD%BD%202%20%E9%85%8D%E7%BD%AE%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F%203%20%E5%8D%95%E5%87%BB%E2%80%9C%E5%BC%80%E5%A7%8B%E2%80%9D%E2%80%94%E2%80%9C%E8%BF%90%E8%A1%8C%E2%80%9D%EF%BC%8C%E8%BE%93%E5%85%A5%22cmd%22%EF%BC%8C%E5%9C%A8%E6%8E%A7%E5%88%B6%E5%8F%B0%E8%BE%93%E5%85%A5,...%204%20%E7%82%B9%E5%87%BB%E5%BC%80%E5%A7%8B%2C%20%E6%90%9C%E7%B4%A2%20%E6%9C%8D%E5%8A%A1%20%E6%88%96%E8%80%85%20%E8%BF%9B%E5%85%A5%E6%8E%A7%E5%88%B6%E9%9D%A2%E6%9D%BF%E2%80%94%E7%B3%BB%E7%BB%9F%E5%92%8C%E5%AE%89%E5%85%A8%20
  b. 注意：不要启动服务，不然无法在idea中启动
  c. 通过idea生成一个maven项目
    ⅰ. 添加web模块：file-Project Structure-artifacts-"+"-web application exploded-from modules
    ⅱ. 添加tomcat服务器，edit configuration-“+”-tomcat server -local-deployment-“+”-选择第一个-修改application context为“/”
  d. get请求：http://localhost:8080/mvc/get
  e. post请求：http://localhost:8080/mvc/query?name=1&age=2
3. 代码细节
  a. init，初始化过程：
    ⅰ. 扫描包路径下的class文件
    ⅱ. 将有controller、service注解的class文件进行实例化
    ⅲ. 依赖注入
    ⅳ. 将url和controller层的method进行映射
  b. doGet，根据uri获取到method，通过当前controller.class获取method的参数
  c. doPost，根据uri获取到method，通过当前controller.class获取method的参数
