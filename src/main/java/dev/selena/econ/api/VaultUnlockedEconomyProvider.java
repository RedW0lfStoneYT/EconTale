package dev.selena.econ.api;

import dev.selena.econ.EconTale;
import dev.selena.econ.config.Config;
import dev.selena.econ.util.CurrencyUtil;
import it.unimi.dsi.fastutil.booleans.BooleanDoublePair;
import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;

public class VaultUnlockedEconomyProvider implements Economy {
    @Override
    public boolean isEnabled() {
        return EconTale.getInstance().isEnabled();
    }

    @Override
    public @NotNull String getName() {
        return "EconTale";
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return false;
    }

    /**
     * No rounding internally but visually the rounding depends on {@link Config#getCurrencyFormat()}
     * @param pluginName The plugin name
     * @return -1 to indicate no rounding
     */
    @Override
    public int fractionalDigits(@NotNull String pluginName) {
        return -1;
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return CurrencyUtil.formatCurrency(amount.doubleValue());
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) {
        return CurrencyUtil.formatCurrency(amount.doubleValue());
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) {
        return CurrencyUtil.formatCurrency(amount.doubleValue());
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) {
        return CurrencyUtil.formatCurrency(amount.doubleValue());
    }

    @Override
    public boolean hasCurrency(@NotNull String currency) {
        return false;
    }

    @Override
    public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
        return Config.get().getCurrencyNameSingular();
    }

    @Override
    public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) {
        return Config.get().getCurrencyNamePlural();
    }

    @Override
    public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) {
        return Config.get().getCurrencyNameSingular();
    }

    @Override
    public @NotNull Collection<String> currencies() {
        return List.of(Config.get().getCurrencyNameSingular());
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name) {
        return CurrencyUtil.getOrAddCurrencyComponent(accountID) != null;
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
        return createAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {
        return createAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName, boolean player) {
        return createAccount(accountID, name);
    }

    /**
     * Returns an empty map as this implementation does not track account names.
     * @return An empty map because this is not implemented
     */
    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        return new HashMap<>();
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID accountID) {
        return Optional.empty();
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID) {
        return CurrencyUtil.getOrAddCurrencyComponent(accountID) != null;
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
        return hasAccount(accountID);
    }

    @Override
    public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
        return hasAccount(accountID);
    }

    @Override
    public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
        return false;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
        return false;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency, @NotNull String world) {
        return false;
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
        return BigDecimal.valueOf(CurrencyUtil.getBalance(accountID));
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
        return getBalance(pluginName, accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
        return getBalance(pluginName, accountID);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return CurrencyUtil.getOrAddCurrencyComponent(accountID).canWithdraw(amount.doubleValue());
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return has(pluginName, accountID, amount);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return has(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        BooleanDoublePair withdrawAction = CurrencyUtil.getOrAddCurrencyComponent(accountID).tryWithdraw(amount.doubleValue());
        if (withdrawAction.leftBoolean()) {
            return new EconomyResponse(BigDecimal.valueOf(withdrawAction.rightDouble()), getBalance(pluginName, accountID), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(BigDecimal.ZERO, getBalance(pluginName, accountID), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return withdraw(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        BooleanDoublePair depositedAmount = CurrencyUtil.getOrAddCurrencyComponent(accountID).deposit(amount.doubleValue());
        if (depositedAmount.leftBoolean()) {
            return new EconomyResponse(BigDecimal.valueOf(depositedAmount.rightDouble()), getBalance(pluginName, accountID), EconomyResponse.ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(BigDecimal.ZERO, getBalance(pluginName, accountID), EconomyResponse.ResponseType.FAILURE, "Deposit failed");
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return deposit(pluginName, accountID, amount);
    }

    @Override
    public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name, @NotNull UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return CurrencyUtil.getOrAddCurrencyComponent(accountID).getUuid().equals(uuid);
    }

    /**
     * NOT IMPLEMENTED
     */
    @Override
    public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return isAccountOwner(pluginName, accountID, uuid);
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission... initialPermissions) {
        return false;
    }

    @Override
    public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission) {
        return isAccountOwner(pluginName, accountID, uuid);
    }

    @Override
    public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission, boolean value) {
        return false;
    }
}
