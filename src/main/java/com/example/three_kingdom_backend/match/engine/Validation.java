package com.example.three_kingdom_backend.match.engine;

import com.example.three_kingdom_backend.match.enums.EnumKingdom;

public final class Validation {
    private Validation() {
    }

    public static void ensureAmountPositive(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("amount must be > 0");
    }

    public static void ensureEnoughGold(MatchAggregate agg, EnumKingdom who, int need) {
        var info = agg.kingdoms().get(who);
        int gold = info == null || info.getGold() == null ? 0 : info.getGold();
        if (gold < need)
            throw new IllegalStateException("Not enough gold");
    }

    public static void ensureTurn(MatchAggregate agg, EnumKingdom actor) {
        EnumKingdom currentTurn = agg.header().getCurrentTurn();
        if (currentTurn == null) {
            throw new IllegalStateException("No current turn set");
        }
        if (currentTurn != actor) {
            throw new IllegalStateException("Not your turn");
        }
    }
}
