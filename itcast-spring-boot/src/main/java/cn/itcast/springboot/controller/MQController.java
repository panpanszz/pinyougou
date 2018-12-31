package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    /**
     * 发送一个 map 消息到 MQ 的队列
     * @return
     */
    @GetMapping("/sendSms")
    public String sendSmsMsg(){
        Map<String, String> map = new HashMap<>();
        map.put("mobile", "15916881024");
        map.put("signName", "黑马");
        map.put("templateCode", "SMS_125018593");
        map.put("templateParam", "{\"code\":\"123456\"}");
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);
        return "发送 sms 消息完成。";
    }
}
