package telran.probes.accounting.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.accounting.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

}
