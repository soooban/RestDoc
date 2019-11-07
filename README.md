## 介绍

该项目用于在运行时使用javadoc生成swagger文档，并使用swagger-ui进行显示。


在线示例： http://www.willingxyz.cn:8084/swagger-ui/index.html 

![示例](./images/example_summary.png)

## docker

docker通过以下命令运行：

`docker run --rm -d -p 8084:8084 willingxyz/restdoc:0.1.8.2`

swagger3规范打开 http://localhost:8084/swagger-ui/index.html 查看。
swagger2规范打开 http://localhost:8084/swagger2-ui/index.html 查看。


## 使用

第一步，配置pom，配置RestDocConfig

在SpringBoot中，增加依赖：

```
<dependency>
     <groupId>cn.willingxyz.restdoc</groupId>
     <artifactId>RestDocSpringSwagger3</artifactId>
     <version>0.1.8.2</version>
 </dependency>
```

如果使用Swagger2，替换为以下依赖：
```
<dependency>
     <groupId>cn.willingxyz.restdoc</groupId>
     <artifactId>RestDocSpringSwagger2</artifactId>
     <version>0.1.8.2</version>
 </dependency>
```

对于JavaConfig，配置如下：

```java 
@Bean
RestDocConfig _swaggerConfig()
{
        return RestDocConfig.builder()
                .apiTitle("rest doc title")
                .apiDescription("rest doc desc")
                .apiVersion("api version")
                //.fieldPrefix("_")
                //.tagDescriptionAsName(true)
                .hideEmptyController(true)
                .packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))
                .servers(Arrays.asList(RestDocConfig.Server.builder().description("url desc").url("localhost:8080").build()))
                .build();
}
```

其中 packages 表示要扫描的基础包名，如 `packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))`

其中 fieldPrefix表示字段前缀。
因为在获取javadoc时，会从field、get方法、set方法上获取，因此如果field有前缀，需要通过fieldPrefix设置，否则将无法获取到javadoc。
如：

```java
public class Response {
    /**
    * name javadoc
    */
    private String _name;
    public String getName() {
           return _name;
    }
    public void setName(String name) {
        _name = name;
    }
}
```

Name属性对应的字段是_name，因此 fieldPrefix应该设置为 `.fieldPrefix("_")`

第二步，在需要生成javadoc的项目中，增加如下依赖：

```
<!-- Annotation processor -->
<dependency>
    <groupId>com.github.therapi</groupId>
    <artifactId>therapi-runtime-javadoc-scribe</artifactId>
    <version>0.9.0</version>
    <scope>provided</scope>
</dependency>
```

第三步，Intellij Idea需要进行编译相关设置
1. File > Settings > Preferences > Build, Execution, Deployment > Compiler > Annotation Processors > 勾选"Enable annotation processing".
2. File > Settings > Preferences > Build, Execution, Deployment > Compiler > Java Compiler > 面板中的"Additional command line parameters" 下方输入框填入 "-parameters".
![编译设置](./images/compile-setting.png)

启动应用后，打开 http://host/swagger-ui/index.html 浏览.
如果是swagger2，打开 http://host/swagger2-ui/index.html

具体可参考 RestDocSpringExamples。

## swagger ui 配置

swagger-ui支持一些配置来控制ui的显示。
只需要把`SwaggerUIConfiguration`配置为一个bean。
如：
```
   @Bean
    SwaggerUIConfiguration _swaggerUIConfiguration()
    {
        var uiConfig = new SwaggerUIConfiguration();
        uiConfig.setDefaultModelRendering("model");
        uiConfig.setDefaultModelExpandDepth(0);
        uiConfig.setDocExpansion("full");
        return uiConfig;
    }
```

具体的配置含义请参考 https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md

## 忽略api

如果想要某些controller和method不出现在swagger文档中，可以通过在controller或method上增加`@IgnoreApi`注解.
如果该注解在controller上，表示该controller里的所有api都不会出现在swagger文档中。
如：
```
@IgnoreApi
@RestController
@RequestMapping("/ignoreapi/all")
public class IgnoreApiAllController {}
```
如果该注解在method上，表示该method的api不会出现在swagger文档中。
```
@IgnoreApi
@GetMapping("/ignore")
public void ignore()
{
}
```

此外，可以使用javadoc标签代替注解。
如：
```java
/**
 * @ignoreApi
 */
@RestController
@RequestMapping("/ignoreapi/javadoc/all")
public class IgnoreApiJavadocAllController {
        /**
         * @ignoreApi
         */
        @GetMapping("/ignore")
        public void ignore()
        {
        }
}
```

## 扩展

如果想要读取或修改生成的swagger对象，可以实现以下接口：

- `IOpenFilter`：用于Swagger3
- `ISwaggerFilter`：用于Swagger2

具体可参考 RestDocSpringExamples.

## 原理

通过注解处理器在编译时生成javadoc的json文件。
在运行时读取生成的javadoc文件。


## todo list

- 增加Bean Validation的支持
- 增加Spring Validator支持

