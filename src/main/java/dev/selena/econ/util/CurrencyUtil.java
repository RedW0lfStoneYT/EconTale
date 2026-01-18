package dev.selena.econ.util;

import dev.selena.core.util.PlayerUtil;
import dev.selena.econ.component.CurrencyComponent;
import dev.selena.econ.config.Config;
import dev.selena.econ.consts.MoneyEventReason;
import it.unimi.dsi.fastutil.booleans.BooleanDoublePair;

import java.util.UUID;

public class CurrencyUtil {


    /**
     * Get or add a CurrencyComponent for the given UUID. (Works with offline players)
     * @param uuid The UUID of the player
     * @return The CurrencyComponent associated with the player
     */
    public static CurrencyComponent getOrAddCurrencyComponent(UUID uuid) {
        return PlayerUtil.getOrAddComponentOffline(uuid, CurrencyComponent.getComponentType(), new CurrencyComponent(uuid));
    }

    /**
     * Format a currency amount according to the configured currency format.
     * @param amount The amount to format
     * @return The formatted currency string
     */
    public static String formatCurrency(double amount) {
        return String.format(Config.get().getCurrencyFormat(), amount);
    }

    /**
     * Try to transfer money from one player to another.
     * @param fromUuid The UUID of the player sending money
     * @param toUuid The UUID of the player receiving money
     * @param amount The amount to transfer
     * @return A TransferRecord indicating the result of the transfer
     * @see TransferRecord
     * @see CurrencyUtil#tryTransferMoney(CurrencyComponent, CurrencyComponent, double)
     */
    public static TransferRecord tryTransferMoney(UUID fromUuid, UUID toUuid, double amount) {
        CurrencyComponent fromComponent = getOrAddCurrencyComponent(fromUuid);
        CurrencyComponent toComponent = getOrAddCurrencyComponent(toUuid);
        return tryTransferMoney(fromComponent, toComponent, amount);
    }

    /**
     * Try to transfer money from one wallet to another.
     * @param fromWallet the wallet to transfer money from
     * @param toWallet the wallet to transfer money to
     * @param amount the amount to transfer
     * @return A TransferRecord indicating the result of the transfer
     * @see TransferRecord
     */
    public static TransferRecord tryTransferMoney(CurrencyComponent fromWallet, CurrencyComponent toWallet, double amount) {
        BooleanDoublePair transfer = fromWallet.tryWithdraw(amount, MoneyEventReason.TRANSFER_SEND);
        if (transfer.firstBoolean()) {
            double received = toWallet.deposit(transfer.secondDouble(), MoneyEventReason.TRANSFER_RECEIVE);
            return new TransferRecord(true, transfer.secondDouble(), received);
        }
        return new TransferRecord(false, 0, 0);
    }

    /**
     * Get the balance of a player's wallet by their UUID.
     * @param uuid The UUID of the player
     * @return The balance of the player's wallet
     * @see CurrencyComponent#getBalance()
     */
    public static double getBalance(UUID uuid) {
        CurrencyComponent wallet = getOrAddCurrencyComponent(uuid);
        return wallet.getBalance();
    }

    /**
     * Get the balance of a given wallet.
     * @param wallet The wallet to get the balance of
     * @return The balance of the wallet
     */
    public static double getBalance(CurrencyComponent wallet) {
        return wallet.getBalance();
    }


}
