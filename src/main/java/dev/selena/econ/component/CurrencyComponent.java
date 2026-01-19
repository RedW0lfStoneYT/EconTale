package dev.selena.econ.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.selena.econ.config.Config;
import dev.selena.econ.consts.MoneyEventReason;
import dev.selena.econ.systems.events.EconTaleMoneyAddEvent;
import dev.selena.econ.systems.events.EconTaleMoneyRemoveEvent;
import it.unimi.dsi.fastutil.booleans.BooleanDoublePair;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CurrencyComponent implements Component<EntityStore> {
    @Getter
    @Setter
    private double balance;
    @Getter
    @Setter
    private static ComponentType<EntityStore, CurrencyComponent> componentType;
    @Setter
    @Getter
    private UUID uuid;

    public static final BuilderCodec<CurrencyComponent> CODEC = BuilderCodec.builder(CurrencyComponent.class, CurrencyComponent::new)
            .append(new KeyedCodec<>("Balance", BuilderCodec.DOUBLE), (source, value) -> {
                source.balance = value;
            }, source -> source.balance)
            .add()
            .append(new KeyedCodec<>("UUID", BuilderCodec.UUID_STRING), (source, value) -> {
                source.uuid = value;
            }, source -> source.uuid)
            .add()
            .build();

    /**
     * Constructor for CurrencyComponent with specified UUID and initial balance.
     * @param uuid The owners UUID.
     * @param initialBalance The initial balance to set.
     */
    public CurrencyComponent(UUID uuid, double initialBalance) {
        this.uuid = uuid;
        this.balance = initialBalance;
    }

    /**
     * Constructor for CurrencyComponent with specified UUID and default starting balance.
     * @param uuid The owners UUID.
     */
    public CurrencyComponent(UUID uuid) {
        this(uuid, Config.get().getStartingBalance());
    }

    /**
     * Internal method for creating the constructor with codec,
     * Use {@link CurrencyComponent#CurrencyComponent(UUID)} or {@link CurrencyComponent#CurrencyComponent(UUID, double)}.
     */
    @ApiStatus.Internal
    public CurrencyComponent() {
        this.balance = Config.get().getStartingBalance();
    }

    /**
     * Deposits the specified amount into the currency component.
     * @param amount The amount to deposit.
     * @return The actual amount deposited (Could be altered by events).
     * @see CurrencyComponent#deposit(double, MoneyEventReason)
     */
    public BooleanDoublePair deposit(double amount) {
        return deposit(amount, MoneyEventReason.OTHER);
    }

    /**
     * Deposits the specified amount into the currency component with a reason.
     * @param amount The amount to deposit.
     * @param reason The reason for the deposit.
     * @return The actual amount deposited (Could be altered by events).
     * @see MoneyEventReason
     * @see CurrencyComponent#deposit(double, MoneyEventReason, boolean)
     */
    public BooleanDoublePair deposit(double amount, MoneyEventReason reason) {
        return deposit(amount, reason, false);
    }

    /**
     * Deposits the specified amount into the currency component with a reason and option to ignore canceled events.
     *
     * @param amount         The amount to deposit.
     * @param reason         The reason for the deposit.
     * @param ignoreCanceled Whether to ignore canceled events.
     * @return The actual amount deposited (Could be altered by events).
     * @see MoneyEventReason
     */
    public BooleanDoublePair deposit(double amount, MoneyEventReason reason, boolean ignoreCanceled) {
        EconTaleMoneyAddEvent.Pre addEvent = HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyAddEvent.Pre.class)
                .dispatch(new EconTaleMoneyAddEvent.Pre(uuid, amount, reason));
        if (addEvent.isCancelled() && !ignoreCanceled) {
            return BooleanDoublePair.of(false, 0);
        }
        this.balance += addEvent.getAmount();

        HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyAddEvent.Post.class)
                .dispatch(new EconTaleMoneyAddEvent.Post(uuid, addEvent.getAmount(), reason));
        return BooleanDoublePair.of(true, addEvent.getAmount());
    }

    /**
     * Attempts to withdraw the specified amount from the currency component.
     * @param amount The amount to withdraw.
     * @return A pair containing a boolean indicating success and the actual amount withdrawn.
     * @see CurrencyComponent#tryWithdraw(double, MoneyEventReason)
     */
    public BooleanDoublePair tryWithdraw(double amount) {
        return tryWithdraw(amount, MoneyEventReason.OTHER);
    }

    /**
     * Attempts to withdraw the specified amount from the currency component with a reason.
     * @param amount The amount to withdraw.
     * @param reason The reason for the withdrawal.
     * @return A pair containing a boolean indicating success and the actual amount withdrawn.
     * @see MoneyEventReason
     */
    public BooleanDoublePair tryWithdraw(double amount, MoneyEventReason reason) {
        EconTaleMoneyRemoveEvent.Pre removeEvent = HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyRemoveEvent.Pre.class)
                .dispatch(new EconTaleMoneyRemoveEvent.Pre(uuid, amount, reason));
        if (!canWithdraw(removeEvent.getAmount()) || removeEvent.isCancelled()) {
            return BooleanDoublePair.of(false, 0);
        }
        this.balance -= removeEvent.getAmount();
        this.balance = Math.max(this.balance, 0);
        HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyRemoveEvent.Post.class)
                .dispatch(new EconTaleMoneyRemoveEvent.Post(uuid, removeEvent.getAmount(), reason));
        return BooleanDoublePair.of(true, removeEvent.getAmount());
    }

    /**
     * Forces a withdrawal of the specified amount from the currency component.
     * @param amount The amount to withdraw.
     * @see CurrencyComponent#forceWithdraw(double, MoneyEventReason)
     */
    public void forceWithdraw(double amount) {
        forceWithdraw(amount, MoneyEventReason.OTHER);
    }

    /**
     * Forces a withdrawal of the specified amount from the currency component with a reason.
     * @param amount The amount to withdraw.
     * @param reason The reason for the withdrawal.
     * @see MoneyEventReason
     * @see CurrencyComponent#forceWithdraw(double, MoneyEventReason, boolean)
     */
    public void forceWithdraw(double amount, MoneyEventReason reason) {
        forceWithdraw(amount, reason, false);
    }

    /**
     * Forces a withdrawal of the specified amount from the currency component with a reason and option to ignore canceled events.
     * @param amount The amount to withdraw.
     * @param reason The reason for the withdrawal.
     * @param ignoreCanceled Whether to ignore canceled events.
     * @return The actual amount withdrawn (Could be altered by events).
     * @see MoneyEventReason
     */
    public double forceWithdraw(double amount, MoneyEventReason reason, boolean ignoreCanceled) {
        EconTaleMoneyRemoveEvent.Pre removeEvent = HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyRemoveEvent.Pre.class)
                .dispatch(new EconTaleMoneyRemoveEvent.Pre(uuid, amount, reason));
        if (removeEvent.isCancelled() && !ignoreCanceled) {
            return 0;
        }
        this.balance -= removeEvent.getAmount();
        this.balance = Math.max(this.balance, 0);

        HytaleServer.get().getEventBus()
                .dispatchFor(EconTaleMoneyRemoveEvent.Post.class)
                .dispatch(new EconTaleMoneyRemoveEvent.Post(uuid, removeEvent.getAmount(), reason));
        return removeEvent.getAmount();
    }

    /**
     * Checks if the specified amount can be withdrawn from the currency component.
     * @param amount The amount to check.
     * @return True if the amount can be withdrawn, false otherwise.
     */
    public boolean canWithdraw(double amount) {
        return this.balance >= amount;
    }


    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new CurrencyComponent(this.uuid, this.balance);
    }
}
