package br.com.jb.model;


import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.jb.model.BankService.INVESTMENT;

@ToString
@Getter
public class InvestmentWallet extends Wallet {

    private final Investment investment;
    private final AccountWallet account;


    public InvestmentWallet(Investment investment, AccountWallet account, final long amount) {
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(),
                "Valor de criação da carteira de investimento");
    }

    public void updateAmount(final long percent){
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getService(),"rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history))
                .limit(amount).toList();
        this.money.addAll(money);
    }
    @Override
    public String toString() {
        return "Carteira de Investimento {" +
                "Pix='" + account.getPix() + '\'' +
                ", Valor Investido=" + getFunds() +
                ", Taxa=" + investment.tax() + "%" +
                '}';
    }

}

