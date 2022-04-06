package com.swissborg.apybot.objects;

public class CryptoCurrency {

    private String name;
    private Float marketCap;
    private Float price;
    private Float percentChange24h;
    private boolean inListingList;

    public CryptoCurrency(String name, Float marketCap, Float price, Float percentChange24h, boolean inListingList) {
        this.name = name;
        this.marketCap = marketCap;
        this.price = price;
        this.percentChange24h = percentChange24h;
        this.inListingList = inListingList;
    }

    public String getName() {
        return name;
    }

    public Float getMarketCap() {
        return marketCap;
    }

    public Float getPrice() {
        return price;
    }

    public Float getPercentChange24h() {
        return percentChange24h;
    }

    public boolean isInListingList() {
        return inListingList;
    }

}
