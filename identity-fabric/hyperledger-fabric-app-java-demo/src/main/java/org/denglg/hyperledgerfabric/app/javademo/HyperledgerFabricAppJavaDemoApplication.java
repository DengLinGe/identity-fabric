package org.denglg.hyperledgerfabric.app.javademo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class HyperledgerFabricAppJavaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HyperledgerFabricAppJavaDemoApplication.class, args);

    }

}
