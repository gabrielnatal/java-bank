package br.com.jb.repository;

import br.com.jb.exception.AccountNotFoundException;
import br.com.jb.exception.PixInUseException;
import br.com.jb.model.AccountWallet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;



import static br.com.jb.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {
    private final  List<AccountWallet> accounts = new ArrayList<>();


    public AccountWallet create ( final List<String> pix, final long initialFunds) {
        if(!accounts.isEmpty()) {
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for (var p : pix) {
                if (pixInUse.contains(p)) {
                    throw new PixInUseException("Pix já está em uso: " + p);
                }
            }
        }
            var newAccount = new AccountWallet(initialFunds, pix);
            accounts.add(newAccount);
            return newAccount;
    }

    public void deposit(final String pix, final long fundsAmount) {
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "Depósito na conta: " + pix);
    }

    public long withdraw(final String pix, final long amount) {
        var source = findByPix(pix);
       checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;

    }

    public void transferMoney(final String sourcePix, final String targetPix, final long amount) {
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "pix enviado de " + sourcePix + " para " + targetPix;
        target.addMoney(source.reduceMoney(amount),source.getService(), message);

    }


    public AccountWallet findByPix(final String pix) {
        return accounts.stream().filter(a -> a.getPix().contains(pix))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Pix não encontrado: " + pix));
    }

    public List<AccountWallet>list(){
        return this.accounts;
    }
}
