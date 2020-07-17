package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.spring.SpringControllerResolver;
import cn.willingxyz.restdoc.spring.SpringRestDocParseConfig;
import cn.willingxyz.restdoc.spring.filter.HttpBasicAuthFilter;
import cn.willingxyz.restdoc.swagger.common.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import cn.willingxyz.restdoc.swagger.common.TypeNameParser;
import cn.willingxyz.restdoc.swagger3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@Import(SpringSwagger3Controller.class)
public class SpringSwagger3Configuration {

    @Bean("swaggerParser")
    IRestDocParser _docParser(@Autowired RestDocConfig restDocConfig,
                              @Autowired RestDocConfigSwagger3Ext ext,
                              @Autowired SpringRestDocParseConfig restDocParseConfig) {
        restDocParseConfig.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));

        // todo 从spring容器中获取实例
        Swagger3GeneratorConfig generatorConfig = new Swagger3GeneratorConfig(restDocParseConfig);
        generatorConfig.setDescription(restDocConfig.getApiDescription());
        generatorConfig.setTitle(restDocConfig.getApiTitle());
        generatorConfig.setVersion(restDocConfig.getApiVersion());
        generatorConfig.setServers(convertServers(restDocConfig.getServers()));
        generatorConfig.setSwaggerTypeInspector(new PrimitiveSwaggerTypeInspector());
        generatorConfig.setTypeInspector(new JavaTypeInspector());
        generatorConfig.setTypeNameParser(new TypeNameParser(restDocConfig.isResolveJavaDocAsTypeName()));
        generatorConfig.setResolveJavaDocAsTypeName(restDocConfig.isResolveJavaDocAsTypeName());

        if (restDocConfig.isTagDescriptionAsName()) {
            List<IOpenAPIFilter> openAPIFilters = new ArrayList<>(ext.getOpenAPIFilters());
            openAPIFilters.add(new TagDescriptionAsNameOpenAPIFilter());
            ext.setOpenAPIFilters(openAPIFilters);
        }
        if (restDocConfig.isHideEmptyController()) {
            List<IOpenAPIFilter> openAPIFilters = new ArrayList<>(ext.getOpenAPIFilters());
            openAPIFilters.add(new HideEmptyControllerOpenAPIFilter());
            ext.setOpenAPIFilters(openAPIFilters);
        }
        if(ext!=null)
            generatorConfig.setOpenAPIFilters(ext.getOpenAPIFilters());

        restDocParseConfig.setRestDocGenerator(new Swagger3RestDocGenerator(generatorConfig));
        restDocParseConfig.setFieldPrefix(restDocConfig.getFieldPrefix());

        return new RestDocParser(restDocParseConfig);
    }

    @Bean
    @ConditionalOnMissingBean(RestDocConfig.class)
    RestDocConfig restDocConfig() {
        return RestDocConfig.builder()
            .apiDescription("API descritpion")
            .apiTitle("API title")
            .apiVersion("1.0-SNAPSHOT")
            .packages(Collections.singletonList(""))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(SpringRestDocParseConfig.class)
    SpringRestDocParseConfig restDocParseConfig() {
        return new SpringRestDocParseConfig();
    }

    @Bean
    @ConditionalOnMissingBean(RestDocConfigSwagger3Ext.class)
    RestDocConfigSwagger3Ext restDocConfigSwagger3Ext() {
        return RestDocConfigSwagger3Ext.builder()
            .build();
    }

    private List<SwaggerGeneratorConfig.ServerInfo> convertServers(List<RestDocConfig.Server> servers) {
        if (servers == null || servers.size() <= 0) {
            return Collections.singletonList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/").build());
        }
        List<SwaggerGeneratorConfig.ServerInfo> serverInfos = new ArrayList<>();
        for (RestDocConfig.Server server : servers) {
            String url = server.getUrl();
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            SwaggerGeneratorConfig.ServerInfo serverInfo =
                    SwaggerGeneratorConfig.ServerInfo.builder().description(server.getDescription()).url(url)
                            .build();

            serverInfos.add(serverInfo);
        }
        return serverInfos;
    }

    @Bean
    @ConditionalOnMissingBean(SwaggerUIConfiguration.class)
    SwaggerUIConfiguration swaggerUIConfiguration() {
        return new SwaggerUIConfiguration();
    }

    @Bean
    @ConditionalOnClass(FilterRegistrationBean.class)
    @ConditionalOnMissingBean(RestDocConfig.HttpBasicAuth.class)
    public FilterRegistrationBean<HttpBasicAuthFilter> authFilterFilterRegistrationBean(@Autowired(required = false) RestDocConfig restDocConfig) {
        RestDocConfig.HttpBasicAuth httpBasicAuth;
        if (restDocConfig == null || (httpBasicAuth = restDocConfig.getHttpBasicAuth()) == null)
            httpBasicAuth = new RestDocConfig.HttpBasicAuth(null, null);

        FilterRegistrationBean<HttpBasicAuthFilter> filterBean = new FilterRegistrationBean<>();
        HttpBasicAuthFilter authFilter = new HttpBasicAuthFilter(httpBasicAuth.getUsername(), httpBasicAuth.getPassword());
        filterBean.addUrlPatterns("/swagger2-ui/**","/swagger-ui/*","/swagger.json");
        filterBean.setFilter(authFilter);
        return filterBean;
    }
}
