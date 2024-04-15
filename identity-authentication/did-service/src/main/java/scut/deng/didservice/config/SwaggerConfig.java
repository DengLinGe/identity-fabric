package scut.deng.didservice.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration //声明该类为配置类
public class SwaggerConfig  {
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI().info(new Info() //
                .title("DID分布式身份认证接口API") //
              //
                .version("1.0.0"));
    }




}
