package br.com.jb.repository;


import br.com.jb.exception.NoFundsEnoughException;
import br.com.jb.model.AccountWallet;
import br.com.jb.model.Money;
import br.com.jb.model.MoneyAudit;
import br.com.jb.model.Wallet;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.jb.model.BankService.ACCOUNT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class CommonsRepository {

    public static void checkFundsForTransaction(final Wallet source, final long amount) {
        if (source.getFunds()< amount){
            throw new NoFundsEnoughException("Saldo insuficiente para transação. ");
        }
    }

    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description) {
       var history = new MoneyAudit(transactionId, ACCOUNT, description, OffsetDateTime.now());
       return Stream.generate(() -> new Money(history)).limit(funds).toList();

    }

}