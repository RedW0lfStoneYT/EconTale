package dev.selena.econ.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.selena.core.util.PlaceholderUtil;
import dev.selena.econ.component.CurrencyComponent;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.MoneyEventReason;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CurrencyUtil;
import it.unimi.dsi.fastutil.booleans.BooleanDoublePair;
import lombok.Getter;
import net.celestialpvp.utils.ColorUtils;
import org.jetbrains.annotations.NotNull;

public class RedeemMoneyFromItemInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<RedeemMoneyFromItemInteraction> CODEC = BuilderCodec.builder(RedeemMoneyFromItemInteraction.class, RedeemMoneyFromItemInteraction::new, SimpleInteraction.CODEC)
            .documentation("Redeems money from item")
            .append(new KeyedCodec<>("Value", Codec.DOUBLE), (inst, value) -> {
                inst.value = value;
            }, value -> value.value).documentation("The amount of money each item is worth").add()
            .append(new KeyedCodec<>("ComsumeAllSneaking", Codec.BOOLEAN), (inst, consumeAllSneaking) -> {
                inst.consumeAllSneaking = consumeAllSneaking;
            }, inst -> inst.consumeAllSneaking).documentation("Whether to consume all items when sneaking").add()
            .append(new KeyedCodec<>("ConsumeAllNonSneaking", Codec.BOOLEAN), (inst, consumeAllNonSneaking) -> {
                inst.consumeAllNonSneaking = consumeAllNonSneaking;
            }, inst -> inst.consumeAllNonSneaking).documentation("Whether to consume all items when not sneaking").add()
            .build();

    @Getter
    private double value = 0;
    @Getter
    private boolean consumeAllSneaking = false;
    private boolean consumeAllNonSneaking = false;

    @Override
    protected void firstRun(@NotNull InteractionType interactionType, @NotNull InteractionContext interactionContext, @NotNull CooldownHandler cooldownHandler) {
        CurrencyComponent wallet = interactionContext.getEntity().getStore().getComponent(interactionContext.getEntity(), CurrencyComponent.getComponentType());
        if (wallet == null) {
            return;
        }
        MovementStates state = interactionContext.getEntity().getStore().getComponent(interactionContext.getEntity(), MovementStatesComponent.getComponentType()).getMovementStates();


        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            return;
        }

        if ((state.crouching && consumeAllSneaking) || (!state.crouching && consumeAllNonSneaking)) {
            int amount = itemStack.getQuantity();
            consume(interactionContext, wallet, itemStack, amount);
            return;
        }
        consume(interactionContext, wallet, itemStack, 1);
    }

    private void consume(InteractionContext context, CurrencyComponent wallet, ItemStack itemStack, int amount) {

        double totalValue = amount * value;
        BooleanDoublePair transaction = wallet.deposit(totalValue, MoneyEventReason.MONEY_ITEM_REDEEM);
        if (!transaction.firstBoolean()) {
            return;
        }
        Player player = context.getEntity().getStore().getComponent(context.getEntity(), Player.getComponentType());
        if (player == null) {
            return;
        }
        player.sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getRedeemMoneyItem(), Placeholders.AMOUNT, CurrencyUtil.formatCurrency(totalValue)));
        context.getHeldItemContainer().removeItemStackFromSlot(context.getHeldItemSlot(), itemStack, amount);
    }

    @Override
    public String toString() {
        return "RedeemMoneyFromItemInteraction{" +
                "value=" + value +
                ", consumeAllSneaking=" + consumeAllSneaking +
                ", consumeAllNonSneaking=" + consumeAllNonSneaking +
                '}';
    }
}
