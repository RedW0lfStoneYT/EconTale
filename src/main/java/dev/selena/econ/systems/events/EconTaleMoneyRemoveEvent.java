package dev.selena.econ.systems.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import dev.selena.econ.consts.MoneyEventReason;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class EconTaleMoneyRemoveEvent implements IEvent<Void> {

    @Getter
    private double amount;
    @Getter
    private final UUID playerUUID;
    @Getter
    private final MoneyEventReason reason;

    /**
     * Constructor for HycononyMoneyRemoveEvent
     *
     * @param playerUUID Player UUID because the player might be offline. to get their Holder<\EntityStore> use PlayerUtil.getOfflinePlayerRef(playerUUID)
     * @param amount     Amount of money being removed
     * @param reason     Reason for money addition
     */
    public EconTaleMoneyRemoveEvent(UUID playerUUID, double amount, MoneyEventReason reason) {
        this.amount = amount;
        this.playerUUID = playerUUID;
        this.reason = reason;
    }

    public EconTaleMoneyRemoveEvent(UUID playerUUID, double amount) {
        this(playerUUID, amount, MoneyEventReason.OTHER);
    }

    protected void setAmountPre(double amount) {
        this.amount = amount;
    }


    public static class Pre extends EconTaleMoneyRemoveEvent implements ICancellable {
        @Getter
        @Setter
        private boolean cancelled;

        public Pre(UUID playerUUID, double amount, MoneyEventReason reason) {
            super(playerUUID, amount, reason);
            cancelled = false;
        }

        public void setAmount(double amount) {
            this.setAmountPre(amount);
        }
    }

    public static class Post extends EconTaleMoneyRemoveEvent {
        public Post(UUID playerUUID, double amount, MoneyEventReason reason) {
            super(playerUUID, amount, reason);
        }
    }
}
