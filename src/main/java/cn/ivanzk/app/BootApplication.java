package cn.ivanzk.app;

import cn.ivanzk.config.CloudServerlessProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口。
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = "cn.ivanzk")
@EnableConfigurationProperties(CloudServerlessProperties.class)
public class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}
