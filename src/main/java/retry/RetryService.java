package retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    private Logger log = LoggerFactory.getLogger(RetryService.class);

    @Retryable
    public void retryDefault() {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalArgumentException("service failed");
    }

    @Retryable(retryFor = {IllegalStateException.class})
    public void retrySpecificException() {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalArgumentException("service failed");
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 100L))
    public void retryCustom() {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalArgumentException("service failed");
    }

    @Retryable
    public void retryWithRecover1() {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalStateException("service failed");
    }

    @Retryable
    public void retryWithRecover2(String crew) {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalStateException("service failed");
    }

    @Retryable
    public String retryWithRecoverWithReturnValue(String crew) {
        log.info("doService count = {}", RetrySynchronizationManager.getContext().getRetryCount());
        throw new IllegalStateException("service failed");
    }

    @Recover
    public void recoverIllegalStateException(IllegalStateException e) {
        log.info("{}는 내가 먹을께", e.getClass().getSimpleName());
    }

    @Recover
    public void recoverIllegalStateException(IllegalStateException e, String crew) {
        log.info("크루 이름은 {}", crew);
        log.info("{}는 내가 먹을께", e.getClass().getSimpleName());
    }

    @Recover
    public String recoverIllegalStateExceptionReturn(IllegalStateException e, String crew) {
        log.info("크루 이름은 {}", crew);
        log.info("{}는 내가 먹을께", e.getClass().getSimpleName());
        return crew;
    }
}
