package mdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    AsyncNotificationService notificationService;

    Logger log = LoggerFactory.getLogger(NotificationController.class);

    public String uploadFile() {
        MDC.put("ip", "192.168.1.100");
        log.info("알림 발송 요청 수신");

        // 비동기 작업
        notificationService.sendAsync();

        log.info("알림 발송 응답 완료");
        return "완료";
    }
}
