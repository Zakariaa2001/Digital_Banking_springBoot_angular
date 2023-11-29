package com.EBANKBACKEND.EBANKBACKEND;

import com.EBANKBACKEND.EBANKBACKEND.dtos.CustomerDto;
import com.EBANKBACKEND.EBANKBACKEND.entities.*;
import com.EBANKBACKEND.EBANKBACKEND.enums.AccountStatus;
import com.EBANKBACKEND.EBANKBACKEND.enums.OperationType;
import com.EBANKBACKEND.EBANKBACKEND.exceptions.BalanceNotSufficientException;
import com.EBANKBACKEND.EBANKBACKEND.exceptions.BankAccountNotFoundException;
import com.EBANKBACKEND.EBANKBACKEND.exceptions.CustomerNotFoundException;
import com.EBANKBACKEND.EBANKBACKEND.repositories.AccountOperationRepository;
import com.EBANKBACKEND.EBANKBACKEND.repositories.BankAccountRepository;
import com.EBANKBACKEND.EBANKBACKEND.repositories.CustomerRepository;
import com.EBANKBACKEND.EBANKBACKEND.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


@SpringBootApplication
public class EBankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EBankBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
		return args -> {
			/*BankAccount bankAccount = bankAccountRepository.findById("03a5d8be-0a57-4acb-ac63-df5c13868f0e").orElse(null);
			if (bankAccount != null) {
				System.out.println("************************************");
				System.out.println(bankAccount.getId());
				System.out.println(bankAccount.getBalance());
				System.out.println(bankAccount.getStatus());
				System.out.println(bankAccount.getCreatedAt());
				System.out.println(bankAccount.getCustomer().getName());
				System.out.println(bankAccount.getClass().getSimpleName());
				if (bankAccount instanceof CurrentAccount) {
					System.out.println("Over Draft :"+((CurrentAccount)bankAccount).getOverDraft());
				} else if (bankAccount instanceof SavingAccount) {
					System.out.println("Rate:"+((SavingAccount)bankAccount).getInterestRate());
				}
				bankAccount.getAccountOperations().forEach(op->{
					System.out.println("===========================");
					System.out.println(op.getType()+"\t"+op.getOperationDate()+"\t"+op.getAmount());

				});
			}*/
			Stream.of("zakaria","fayssal","yassine").forEach(name-> {
				CustomerDto customerDto=new CustomerDto();
				customerDto.setName(name);
				customerDto.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customerDto);
			});
			bankAccountService.listCustomer().forEach(customer -> {
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*90000,5.5,customer.getId());
					List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
					for (BankAccount bankAccount:bankAccounts) {
						for (int i = 0; i < 10; i++) {
							bankAccountService.credit(bankAccount.getId(),1000+Math.random()*12000,"credit");
							bankAccountService.debit(bankAccount.getId(),10000+Math.random()*9000,"debit");
						}
					}
				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				} catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
					e.printStackTrace();
                }
            });
		};
	}
	//@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository) {
		return args -> {
			Stream.of("Yassine","Fayssal","Zakaria").forEach(name->{
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});
			customerRepository.findAll().forEach(cust->{
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});
			bankAccountRepository.findAll().forEach(acc->{
				for(int i=0;i<10;i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random()*1200);
					accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});

		};

	}
}