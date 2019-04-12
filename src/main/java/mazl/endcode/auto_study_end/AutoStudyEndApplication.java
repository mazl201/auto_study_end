package mazl.endcode.auto_study_end;

import mazl.endcode.TextParsing.TextRecieverServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("mazl.endcode")
public class AutoStudyEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoStudyEndApplication.class, args);

        //启动 netty 接收数据
        String s = new TextRecieverServer(args[0], Integer.parseInt(args[1])).initNettyServer();
    }

}
