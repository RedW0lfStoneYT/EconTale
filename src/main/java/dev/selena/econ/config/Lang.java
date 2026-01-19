package dev.selena.econ.config;

import com.google.gson.annotations.Expose;
import dev.selena.core.config.Comment;
import lombok.Getter;

public class Lang {

    @Expose
    @Getter
    @Comment("The message sent when a non player tries to check their own balance (no player specified)")
    private String PlayerOnlyCommandConsole = "&cOnly players can run this command.";

    @Expose
    @Getter
    @Comment("The message sent when target player cant be found")
    private String TargetPlayerNotFound = "&cNo player with name: &e{target_player}&a could not be found.";

    @Expose
    @Getter
    @Comment("The message sent when checking another players balance")
    private String BalanceOther = "&a{target_player}'s balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message sent when checking your own balance")
    private String BalanceSelf = "&aYour balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message sent when you try pay yourself money")
    private String CantPlaySelf = "&cYou cannot pay yourself money. &e/help eco&a for more info";

    @Getter
    @Expose
    @Comment("For when you try transfer 0 or less")
    private String PaymentCantBeBelowZero = "&cTransfer amount needs to be above zero &e{amount}&a. &e/help eco&a for more info";

    @Getter
    @Expose
    @Comment("The message sent to the sender when a payment is successful")
    private String PaymentSuccessSender = "&aYou have sent &e{amount} &ato &e{target_player}&a. Your new balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message sent to the receiver when a payment is successful")
    private String PaymentSuccessReceiver = "&aYou have received &e{amount} &afrom &e{player}&a. Your new balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message sent when the sender has insufficient funds for a payment")
    private String PaymentInsufficientFunds = "&cYou do not have enough funds to send &e{amount}&c. Your current balance is &e{balance}&a.";



    @Getter
    @Expose
    @Comment("The message when an admin sets a players balance")
    private String AdminSetBalance = "&aYou have set &e{target_player}'s &abalance to &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message when an admin adds to a players balance")
    private String AdminAddBalance = "&aYou have added &e{amount} &ato &e{target_player}'s &abalance. New balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The message when an admin removes moneu from a players balance")
    private String AdminRemoveBalance = "&aYou have removed &e{amount} &afrom &e{target_player}'s &abalance. New balance is &e{balance}&a.";

    @Getter
    @Expose
    @Comment("The baltop header message")
    private String BalTopHeader = "&e----=[ &aBalance Top &e]=----";

    @Getter
    @Expose
    @Comment("The baltop entry format")
    private String BalTopEntry = "&e#{position} &a{player} &e- &a{balance}";

    @Getter
    @Expose
    @Comment("The footer message for baltop")
    private String BalTopFooter = "&e-----------------------";

    @Getter
    @Expose
    @Comment("The message sent when a deposit fails (usually event cancelled)")
    private String DepositFailed = "&cYour deposit of &e{amount} &chas failed.";

    @Getter
    @Expose
    @Comment("The message sent when you redeem a money item")
    private String RedeemMoneyItem = "&e{amount} &ahas been added to your wallet.";

    @Getter
    @Expose
    @Comment("The message sent when you withdraw money to a money item")
    private String WithdrawMoneyItem = "&e{amount} &ahas been withdrawn from your wallet.";

    @Getter
    @Expose
    @Comment("The message sent when a withdrawal fails (usually event cancelled)")
    private String WithdrawFailed = "&cYour withdrawal of &e{amount} &chas failed.";


    public static Lang get() {
        return Configs.LANG.getConfig();
    }
}
