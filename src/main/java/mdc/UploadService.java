package mdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class UploadService {

    static Logger log = LoggerFactory.getLogger(UploadService.class);

    public static void upload(MultipartFile file) {
        log.info("파일 업로드 시작");

        // 파일 처리 로직

        log.info("파일 업로드 완료");
    }
}
