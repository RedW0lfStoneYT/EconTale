package dev.selena.econ.command.sub;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.selena.core.util.PlaceholderUtil;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CurrencyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WithdrawCommand extends AbstractAsyncPlayerCommand {

    private final RequiredArg<Integer> amountArg;

    public WithdrawCommand() {
        super("withdraw", "Withdraws money from your balance into an item");
        this.amountArg = this.withRequiredArg("amount", "The amount of money to withdraw", ArgTypes.INTEGER);
        this.requirePermission("EconTale.command.withdraw");
    }

    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {

        int amount = commandContext.get(amountArg);
        List<ItemStack> items = CurrencyUtil.getOrAddCurrencyComponent(playerRef.getUuid()).withdrawToItemStack(amount);
        if (items == null || items.isEmpty()) {
            playerRef.sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getWithdrawFailed(),
                    Placeholders.AMOUNT, String.valueOf(amount)
            ));
            return CompletableFuture.completedFuture(null);
        }
        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
        CombinedItemContainer inventory = new CombinedItemContainer(player.getInventory().getHotbar(), player.getInventory().getStorage());
        ListTransaction<ItemStackTransaction> transaction =  inventory.addItemStacks(items);
        return CompletableFuture.runAsync(() -> {
            if (transaction.getList().isEmpty())
                return;
            for (ItemStackTransaction itemTransaction : transaction.getList()) {
                ItemStack itemStack = itemTransaction.getRemainder();
                if (itemStack != null && !itemStack.isEmpty()) {
                    ItemUtils.dropItem(ref, itemStack, store);
                }
            }
        }, world).thenRun(() -> {
            playerRef.sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getWithdrawMoneyItem(),
                    Placeholders.AMOUNT, CurrencyUtil.formatCurrency(amount)
            ));
        });
    }
}
