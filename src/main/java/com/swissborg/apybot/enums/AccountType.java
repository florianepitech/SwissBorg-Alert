package com.swissborg.apybot.enums;

import java.util.ArrayList;

public enum AccountType {

    STANDARD("⚪️", "Standard", 1.0F, 0),
    COMMUNITY("🟣", "Community", 1.5F, 2000),
    GENERATION("🔵", "Generation", 1.75F, 20000),
    GENESIS("🟢", "Genesis", 2.0F, 50000);

    private String symbol, name;
    private float multiplier;
    private int tokenToLock;

    AccountType(String symbol, String name, float multiplier, int tokenToLock) {
        this.symbol = symbol;
        this.name = name;
        this.multiplier = multiplier;
        this.tokenToLock = tokenToLock;
    }

    public static ArrayList<AccountType> getAll() {
        ArrayList<AccountType> accountTypes = new ArrayList<>();
        accountTypes.add(STANDARD);
        accountTypes.add(COMMUNITY);
        accountTypes.add(GENERATION);
        accountTypes.add(GENESIS);
        return accountTypes;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public int getTokenToLock() {
        return tokenToLock;
    }
}
