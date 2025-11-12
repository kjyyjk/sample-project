package event.asynctransaction;

import org.springframework.data.repository.CrudRepository;

import event.transaction.CustomEntity;

public interface AsyncCustomEntityRepository extends CrudRepository<AsyncCustomEntity, Long> {
}
