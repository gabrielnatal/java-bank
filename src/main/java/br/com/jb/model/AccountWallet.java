package br.com.jb.model;

import lombok.Getter;

import java.util.List;

import static br.com.jb.model.BankService.ACCOUNT;

@Getter
public class AccountWallet extends Wallet {

    private final List<String> pix;

    protected AccountWallet(final List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
    }

    public AccountWallet(final long amount, List<String> pix) {
        super(ACCOUNT);
        this.pix = pix;
        addMoney(amount,"Valor de criação da conta");
    }

    public void addMoney(final long amount, final String description) {
       var money = generateMoney(amount, description);
       this.money.addAll(money);
    }
}
