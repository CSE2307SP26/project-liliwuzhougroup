package main;
import java.util.ArrayList;
public class CreateAccount{
    public void createAccount(ArrayList<BankAccount> accounts,BankAccount account){
        accounts.add(account);
    }
    public void createBlankAccount(ArrayList<BankAccount>accounts){
        BankAccount account = new BankAccount();
        accounts.add(account);
    }
}