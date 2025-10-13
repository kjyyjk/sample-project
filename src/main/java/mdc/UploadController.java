package mdc;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.multipart.MultipartFile;

public class UploadController {

    static Logger log = LoggerFactory.getLogger(UploadController.class);

    public static String uploadFile(MultipartFile file) {
        String ip = UUID.randomUUID().toString().substring(0, 5);
        MDC.put("ip", ip);
        log.info("파일 업로드 요청 수신");

        UploadService.upload(file);

        log.info("파일 업로드 응답 완료");
        MDC.remove("ip");
        return "ok";
    }
}
