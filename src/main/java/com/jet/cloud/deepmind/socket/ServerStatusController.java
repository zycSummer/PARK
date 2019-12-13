package com.jet.cloud.deepmind.socket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

/**
 * @author yhy
 * @create 2019-10-22 10:41
 */
@Log4j2
@Controller
public class ServerStatusController {

    @Autowired//消息发送模板
    private SimpMessagingTemplate simpMessagingTemplate;


/*
    //@SubscribeMapping("/port")
    @MessageMapping("/port")
    //@SendTo("/serverStatus/port")
    public void greeting(HelloMessage message) throws Exception {
        //map.put("msg",split[0]);
        for (int i = 0; i < 10; i++) {
            ///destination :topic/greetings
            simpMessagingTemplate.convertAndSend("/serverStatus/port", "8080");
        }
    }
*/

    //@MessageMapping("/port")
    // @Scheduled(fixedRate = 2000L)
    // public void g() {
    //     System.out.println("发了一条消息" + System.currentTimeMillis());
    //     simpMessagingTemplate.convertAndSend("/serverStatus/port", Math.random());
    // }

}
