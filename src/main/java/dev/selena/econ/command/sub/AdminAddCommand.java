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
import dev.selena.econ.component.CurrencyComponent;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.MoneyEventReason;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CurrencyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class AdminAddCommand extends AbstractAsyncCommand {

    private final RequiredArg<Double> amountArg;
    private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg;

    public AdminAddCommand() {
        super("add", "Adds money to a players balance");
        this.requirePermission("econ.admin");
        playerArg = this.withRequiredArg("player", "The player you want to pay", ArgTypes.GAME_PROFILE_LOOKUP);
        amountArg = this.withRequiredArg("amount", "The amount of money to add", ArgTypes.DOUBLE);
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        ProfileServiceClient.PublicGameProfile playerProfile = playerArg.get(commandContext);
        double amount = commandContext.get(amountArg);
        if (playerProfile == null) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getTargetPlayerNotFound()));
            return CompletableFuture.runAsync(()->{});
        }

        if (PlayerUtil.isOnline(playerProfile.getUuid())) {
            World world = Universe.get().getWorld(Objects.requireNonNull(Objects.requireNonNull(Universe.get().getPlayer(playerProfile.getUuid()).getWorldUuid())));
            assert world != null;
            return CompletableFuture.runAsync(() -> {
                command(commandContext, playerProfile, amount);
            }, world);
        }
        return CompletableFuture.runAsync(() -> {
            command(commandContext, playerProfile, amount);
        });
    }


    private void command(CommandContext commandContext, ProfileServiceClient.PublicGameProfile playerProfile, double amount) {
        CurrencyComponent wallet = CurrencyUtil.getOrAddCurrencyComponent(playerProfile.getUuid());
        if (amount <= 0) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getPaymentCantBeBelowZero()));
            return;
        }
        double finalAmount = wallet.deposit(commandContext.get(amountArg), MoneyEventReason.ADMIN_ADD, true);

        String formattedAmount = CurrencyUtil.formatCurrency(finalAmount);
        String formattedBal =  CurrencyUtil.formatCurrency(wallet.getBalance());
        commandContext.sender().sendMessage(
                PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getAdminAddBalance(),
                        Placeholders.AMOUNT, formattedAmount,
                        Placeholders.TARGET_PLAYER, PlayerUtil.getDisplayNameRaw(playerProfile.getUuid()),
                        Placeholders.BALANCE, formattedBal
                ));
        PlayerRef playerRef = Universe.get().getPlayer(playerProfile.getUuid());
        if (playerRef == null) {
            PlayerUtil.saveOfflinePLayerRef(playerProfile.getUuid());
            return;
        }
        playerRef.sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                Lang.get().getPaymentSuccessReceiver(),
                Placeholders.AMOUNT, formattedAmount,
                Placeholders.PLAYER, commandContext.sender().getDisplayName(),
                Placeholders.BALANCE, formattedBal
        ));

    }


}
