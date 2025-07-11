package transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionService {

    @Autowired
    TransactionService2 transactionService2;

    @Transactional
    void aa1() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        bb1();
    }

    void aa2() {
        System.out.println("outer with no transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        bb1();
    }

    @Transactional
    void aa3() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        bb2();
    }

    @Transactional
    void aa4() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        transactionService2.bb2();
    }

    @Transactional
    void aa5() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        bb3();
    }

    @Transactional
    void aa6() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        transactionService2.bb1();
    }

    @Transactional
    void aa7() {
        System.out.println("outer with transaction : " + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("transaction name : " + TransactionSynchronizationManager.getCurrentTransactionName());
        transactionService2.bb3();
    }

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
