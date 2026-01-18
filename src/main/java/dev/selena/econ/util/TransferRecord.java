package dev.selena.econ.util;

/**
 * Used to record the result of a transfer between two accounts.
 */
public record TransferRecord(boolean successful, double takenAmount, double receivedAmount) {

}
