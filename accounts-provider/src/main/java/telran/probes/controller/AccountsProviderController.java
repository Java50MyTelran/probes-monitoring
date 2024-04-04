package telran.probes.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.AccountDto;
import telran.probes.service.AccountProviderService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountsProviderController {
	final AccountProviderService providerService;
	@GetMapping("${app.accounts.provider.url}" + "/{email}")
	AccountDto getAccount(@PathVariable String email) {
		log.debug("AccountsProviderController: request fro account {}", email);
		return providerService.getAccount(email);
	}

}
