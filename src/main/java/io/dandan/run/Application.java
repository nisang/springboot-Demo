package io.dandan.run;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan("io.dandan")
@EnableAutoConfiguration
@SpringBootApplication
//@Import({MyBatisConfiguration.class}) 失败
//@ImportResource({"search-service-provider.xml"})

public class Application implements CommandLineRunner,EmbeddedServletContainerCustomizer {

	@Override
	public void run(String... args) throws Exception {
		while (true) {
			
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	/**
	 * 设定绑定端口
	 */
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.setPort(9099);
	}
}
