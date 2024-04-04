package telran.probes.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.exceptions.NotFoundException;
import telran.probes.accounting.model.Account;
import telran.probes.accounting.repo.AccountRepository;
import telran.probes.dto.AccountDto;
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProviderServiceImpl implements AccountProviderService {
final AccountRepository accountRepo;
	@Override
	public AccountDto getAccount(String email) {
		log.debug("AccountProviderService: received request for email {}", email);
		Account account = accountRepo.findById(email)
				.orElseThrow(() -> new NotFoundException("account not found"));
		return account.build();
	}

}
