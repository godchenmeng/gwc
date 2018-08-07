package com.youxing.car.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
/**
 * @author mars
 * @date 2017年5月2日 上午9:11:38
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfiguration {
	@Bean
	public Docket demoApi() {
		return new Docket(DocumentationType.SWAGGER_2)
		// .groupName("demo")
		// .genericModelSubstitutes(DeferredResult.class)
		// .useDefaultResponseMessages(false)
		// .forCodeGeneration(false)
				.pathMapping("/")
				// .select()
				// .build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("app接口文档", "公务车app接口，请勿随意测试删除类接口", "1.0.0", " ", "贵州优行车联", "Apache", "http://www.apache.org/licenses/");
		return apiInfo;
	}
}
