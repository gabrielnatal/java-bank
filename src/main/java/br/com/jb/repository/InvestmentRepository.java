package br.com.jb.repository;

import br.com.jb.exception.AccountWithInvestmentException;
import br.com.jb.exception.InvestmentNotFoundException;
import br.com.jb.exception.PixInUseException;
import br.com.jb.exception.WalletNotFoundException;
import br.com.jb.model.AccountWallet;
import br.com.jb.model.Investment;
import br.com.jb.model.InvestmentWallet;

import java.util.ArrayList;
import java.util.List;

import static br.com.jb.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

      private long nextId = 0;
      private final List<Investment> investments = new ArrayList<>();
      private final List<InvestmentWallet> wallets = new ArrayList<>();

      public Investment create (final long tax, final long initialFunds) {
          this.nextId++;
          var investment = new Investment(this.nextId, tax, initialFunds);
          investments.add(investment);
          return investment;
      }

      public InvestmentWallet initInvestment(final AccountWallet account, final long id){
          if(!wallets.isEmpty()) {
              var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
              if (accountInUse.contains(account)) {
                  throw new AccountWithInvestmentException("A conta" + account + " já possui um investimento associado.");
              }
          }


          var investment = findById(id);
          checkFundsForTransaction(account, investment.initialFunds());
            var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
            wallets.add(wallet);
            return  wallet;
      }


      public InvestmentWallet deposit(final String pix, final long funds){
          var wallet = findWalletByAccount(pix);
          wallet.addMoney(wallet.getAccount().reduceMoney(funds),wallet.getService(),"Investimento: ");
          return wallet;
      }


     public InvestmentWallet withdraw(final String pix, final long funds ){
         var wallet = findWalletByAccount(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds),wallet.getService(),"saque de investimento: ");
        if(wallet.getFunds() == 0){
            wallets.remove(wallet);
        }
        return wallet;
     }



      public void updateAmount(){
          wallets.forEach(w -> w.updateAmount(w.getInvestment().tax()));
      }



     public Investment findById(final long id){
         return investments.stream()
                 .filter( a-> a.id() == id)
                 .findFirst()
                 .orElseThrow(
                         () -> new InvestmentNotFoundException("Investimento não encontrado")
                 );
     }

    public InvestmentWallet findWalletByAccount(final String pix){
        return wallets.stream()
                .filter(w -> w.getAccount().getPix().stream().anyMatch(p -> p.equals(pix)))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("Carteira não encontrada"));
    }


      public List<InvestmentWallet> listWallets() {
          return this.wallets;
      }


      public List<Investment> list() {
          return this.investments;
      }

}
