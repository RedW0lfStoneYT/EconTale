package dev.selena.econ.command.sub;

import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.selena.core.util.PlaceholderUtil;
import dev.selena.core.util.PlayerUtil;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CurrencyUtil;
import dev.selena.econ.util.TransferRecord;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DepositCommand extends AbstractAsyncCommand {

    private final RequiredArg<Double> amountArg;
    private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg;

    public DepositCommand() {
        super("deposit", "Transfer money from one player to another");
        playerArg = this.withRequiredArg("player", "The player to deposit money to", ArgTypes.GAME_PROFILE_LOOKUP);
        amountArg = this.withRequiredArg("amount", "The amount of money to add", ArgTypes.DOUBLE);
        this.addAliases("pay");
        requirePermission("econ.player");
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        if (!commandContext.isPlayer()) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getPlayerOnlyCommandConsole()));
            return CompletableFuture.runAsync(()->{});
        }
        ProfileServiceClient.PublicGameProfile targetProfile = commandContext.get(playerArg);
        double amount = commandContext.get(amountArg);
        if (commandContext.sender().getUuid().equals(targetProfile.getUuid())) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getPlayerOnlyCommandConsole()));
            return CompletableFuture.runAsync(()->{});
        }
        String formattedAmount = CurrencyUtil.formatCurrency(amount);
        if (amount <= 0) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getPaymentCantBeBelowZero(), Placeholders.AMOUNT, formattedAmount));
            return CompletableFuture.runAsync(()->{});
        }

        if (PlayerUtil.isOnline(targetProfile.getUuid())) {
            World world = Universe.get().getWorld(Objects.requireNonNull(Objects.requireNonNull(Universe.get().getPlayer(targetProfile.getUuid()).getWorldUuid())));
            assert world != null;
            return CompletableFuture.runAsync(() -> {
                command(commandContext, targetProfile, amount);
            }, world);
        }
        if (PlayerUtil.isOnline(commandContext.sender().getUuid())) {
            World world = Universe.get().getWorld(Objects.requireNonNull(Objects.requireNonNull(Universe.get().getPlayer(commandContext.sender().getUuid()).getWorldUuid())));
            assert world != null;
            return CompletableFuture.runAsync(() -> {
                command(commandContext, targetProfile, amount);
            }, world);
        }
        return CompletableFuture.runAsync(() -> {
            command(commandContext, targetProfile, amount);
        });
    }

    private void command(CommandContext commandContext, ProfileServiceClient.PublicGameProfile targetProfile, double amount) {
        String formattedAmount = CurrencyUtil.formatCurrency(amount);

        TransferRecord transferRecord = CurrencyUtil.tryTransferMoney(commandContext.sender().getUuid(), targetProfile.getUuid(), amount);

        if (!transferRecord.successful()) {
            String formattedBal = CurrencyUtil.formatCurrency(CurrencyUtil.getBalance(commandContext.sender().getUuid()));
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                    Lang.get().getPaymentInsufficientFunds(),
                    Placeholders.AMOUNT, formattedAmount,
                    Placeholders.BALANCE, formattedBal));
            return;
        }
        formattedAmount = CurrencyUtil.formatCurrency(transferRecord.takenAmount());
        String formattedBal = CurrencyUtil.formatCurrency(CurrencyUtil.getBalance(commandContext.sender().getUuid()));
        commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                Lang.get().getPaymentSuccessSender(),
                Placeholders.AMOUNT, formattedAmount,
                Placeholders.TARGET_PLAYER, targetProfile.getUsername(),
                Placeholders.BALANCE, formattedBal
        ));
        PlayerRef playerRef = Universe.get().getPlayer(targetProfile.getUuid());
        if (playerRef == null) {
            PlayerUtil.saveOfflinePLayerRef(targetProfile.getUuid());
            return;
        }
        formattedAmount = CurrencyUtil.formatCurrency(transferRecord.receivedAmount());
        formattedBal = CurrencyUtil.formatCurrency(CurrencyUtil.getBalance(playerRef.getUuid()));
        playerRef.sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                Lang.get().getPaymentSuccessReceiver(),
                Placeholders.AMOUNT, formattedAmount,
                Placeholders.PLAYER, commandContext.sender().getDisplayName(),
                Placeholders.BALANCE, formattedBal
        ));
    }

}
