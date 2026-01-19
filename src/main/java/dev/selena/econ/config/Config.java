package dev.selena.econ.config;

import com.google.gson.annotations.Expose;
import dev.selena.core.config.Comment;
import lombok.Getter;

public class Config {

    @Getter
    @Expose
    @Comment("The currency format")
    private String CurrencyFormat = "$%,.2f";

    @Getter
    @Expose
    @Comment("The starting balance for new players")
    private double StartingBalance = 100.0;

    @Getter
    @Expose
    @Comment("The number of players to show on the top balances leaderboard")
    private int LeaderboardSize = 10;

    @Getter
    @Expose
    @Comment("The name of the currency in singular form")
    private String CurrencyNameSingular = "Coin";

    @Getter
    @Expose
    @Comment("The name of the currency in plural form")
    private String CurrencyNamePlural = "Coins";

    public static Config get() {
        return Configs.CONFIG.getConfig();
    }
}
