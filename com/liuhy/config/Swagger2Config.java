package com.liuhy.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
* api配置
* @author liuhy
* @version 1.0
*/
@Configuration
@EnableSwagger2WebMvc
public class Swagger2Config {

    private String basePackage = "com.liuhy.controller";

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("open接口").select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .build().apiInfo(apiInfo());
    }

    @Bean
    public Docket createProtectRestApi() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(true).order(0);
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2).groupName("auth接口").select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .build().globalOperationParameters(pars)
                .apiInfo(safeApiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("大喇叭管控系统   OPEN RESTful API文档").description("开放接口参考和查看API详细信息")
                .termsOfServiceUrl("http://www.comtom.cn/")
                .contact(new Contact("liuhy", "", ""))
                .version("2.0").build();
    }


    private ApiInfo safeApiInfo() {
        return new ApiInfoBuilder().title("大喇叭管控系统   SAFE RESTful API文档").description("安全接口参考和查看API详细信息")
                .termsOfServiceUrl("http://www.comtom.cn/")
                .contact(new Contact("liuhy", "", ""))
                .version("2.0").build();
    }


}
