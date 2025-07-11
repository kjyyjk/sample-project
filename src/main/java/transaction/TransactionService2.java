package transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionService2 {

    @Transactional
    void bb1() {
        System.out.println("inner with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void bb2() {
        System.out.println("inner with requires_new: " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
    }

    void bb3() {
        System.out.println("inner with no transaction: " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
    }
}
