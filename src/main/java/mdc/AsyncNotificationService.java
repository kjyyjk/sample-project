package mdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncNotificationService {
    static Logger log = LoggerFactory.getLogger(AsyncNotificationService.class);

    @Async
    public void sendAsync() {
        log.info("알림 발송");
    }
}
