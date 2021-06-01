package com.im;

import com.im.udp.server.UdpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.im")
@EnableAsync
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        new SpringApplication(Bootstrap.class).run().getBean(UdpServer.class).start();
    }

}
