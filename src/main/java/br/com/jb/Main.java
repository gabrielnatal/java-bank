package br.com.jb;

import br.com.jb.exception.AccountNotFoundException;
import br.com.jb.exception.NoFundsEnoughException;
import br.com.jb.exception.WalletNotFoundException;
import br.com.jb.model.AccountWallet;
import br.com.jb.model.MoneyAudit;
import br.com.jb.repository.AccountRepository;
import br.com.jb.repository.InvestmentRepository;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();


    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Olá seja bem vindo ao JBank!");
        while (true) {
            System.out.println("Selecione a operação desejada:");
            System.out.println("1 - Criar Conta");
            System.out.println("2 - Criar um investimento");
            System.out.println("3 - Fazer um investimento");
            System.out.println("4 - Fazer um depósito");
            System.out.println("5 - Fazer um saque");
            System.out.println("6 - Fazer uma transferência");
            System.out.println("7 - Investir");
            System.out.println("8 - Sacar investimento");
            System.out.println("9 - Listar contas");
            System.out.println("10 - Listar investimentos");
            System.out.println("11 - Listar carteira de investimentos");
            System.out.println("12 - Atualizar investimentos");
            System.out.println("13 - Histórico de conta");
            System.out.println("14 - Sair");

            var option = scanner.nextInt();
            switch (option) {
                case 1 -> createAccount();
                case 2 -> createInvestment();
                case 3 -> createWalletInvestment();
                case 4 -> deposit();
                case 5 -> withdraw();
                case 6 -> transferToAccount();
                case 7 -> incInvestment();
                case 8 -> rescueInvestment();
                case 9 -> listAccounts();
                case 10 -> investmentRepository.list().forEach(System.out::println);
                case 11 -> investmentRepository.listWallets().forEach(System.out::println);
                case 12 -> {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos atualizados com sucesso!");
                }
                case 13 -> checkHistory();
                case 14 -> {
                    System.out.println("Obrigado por usar o JBank!");
                    return;

                }
                default ->
                    System.out.println("Opção inválida, tente novamente.");
            }

        }
    }

    private static void createAccount() {
        System.out.println("Informe as chaves pix(separadas por ';' ");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o saldo inicial da conta: ");
        var amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta criada com sucesso! ");
    }

    private static void createInvestment() {
        System.out.println("Informe a taxa de investimento: ");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de deposito: ");
        var initialFunds = scanner.nextLong();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento criado com sucesso!");
    }

    private static void deposit() {
        System.out.println("Informe a chave pix da conta: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser depositado: ");
        var amount = scanner.nextLong();
        try {
            accountRepository.deposit(pix, amount);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }


    }

    private static void withdraw() {
        System.out.println("Informe a chave pix da conta para saque: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a sera sacado: ");
        var amount = scanner.nextLong();
        try {
            accountRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());

        }
    }

    private static void transferToAccount() {
        System.out.println("Informe a chave pix de origem: ");
        var source = scanner.next();
        System.out.println("Informe a chave pix de destino: ");
        var target = scanner.next();
        System.out.println("Informe o valor a ser depositado: ");
        var amount = scanner.nextLong();
        try {
            accountRepository.transferMoney(source, target,amount);
        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }


    }


    private static void createWalletInvestment(){

        System.out.println("Informe a chave pix da conta: ");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o id do investimento: ");
        var investmentId = scanner.nextInt();
        var investmentWallet = investmentRepository.initInvestment(account,investmentId);
        System.out.println("Carteira de investimento criada com sucesso! ");
    }

    private static void incInvestment(){
        System.out.println("Informe a chave pix da conta para investimento: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a ser investido: ");
        var amount = scanner.nextLong();
        try {
            investmentRepository.deposit(pix, amount);
        } catch (WalletNotFoundException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void rescueInvestment() {
        System.out.println("Informe a chave pix da conta para resgate do investimento: ");
        var pix = scanner.next();
        System.out.println("Informe o valor a sera sacado: ");
        var amount = scanner.nextLong();
        try {
            investmentRepository.withdraw(pix, amount);
        } catch (NoFundsEnoughException | AccountNotFoundException ex) {
            System.out.println(ex.getMessage());

        }
    }

    private static void checkHistory() {
        System.out.println("Informe a chave pix da conta para verificar extrato: ");
        var pix = scanner.next();
        try {
            var wallet = accountRepository.findByPix(pix);
            var transactions = wallet.getFinancialTransactions();

            if (transactions.isEmpty()) {
                System.out.println("Nenhuma transação encontrada.");
                return;
            }

            System.out.println("Histórico da conta:");

            var distinctTransactions = transactions.stream()
                    .collect(Collectors.toMap(
                            MoneyAudit::transactionId,
                            t -> t,
                            (t1, t2) -> t1
                    ))
                    .values();

            distinctTransactions.forEach(t -> {
                System.out.println("• [" + t.transactionId() + "] " +
                        t.description() + " | Serviço: " + t.targetService() +
                        " | Data: " + t.createdAt());
            });

        } catch (AccountNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void listAccounts() {
        accountRepository.list().forEach(account -> {
            System.out.println("Conta com chaves Pix: " + account.getPix());
            System.out.println("Saldo atual: " + account.getFunds());
            System.out.println("-------------------------------");
        });
    }


}