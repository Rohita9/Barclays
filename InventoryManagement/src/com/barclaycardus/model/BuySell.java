package com.barclaycardus.model;

/**
 * Created by Rohita on 2/4/2018.
 */
public class BuySell {
    private Double buyAt;
    private Double sellAt;

    public BuySell() {}

    public BuySell(Double buyAt,
                   Double sellAt) {
        this.buyAt = buyAt;
        this.sellAt = sellAt;
    }

    public Double getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(Double buyAt) {
        this.buyAt = buyAt;
    }

    public Double getSellAt() {
        return sellAt;
    }

    public void setSellAt(Double sellAt) {
        this.sellAt = sellAt;
    }
}
