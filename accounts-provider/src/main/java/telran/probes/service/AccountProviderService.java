package telran.probes.service;

import telran.probes.dto.AccountDto;

public interface AccountProviderService {
AccountDto getAccount(String email);
}
