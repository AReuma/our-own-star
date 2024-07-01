package com.example.backend.artistBoard.entity.market;

public enum MarketBoardStatus {
    SELL("판매중"),
    RESERVATION("예약중"),
    SOLD_OUT("판매완료");

    private final String status;

    MarketBoardStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static MarketBoardStatus fromString(String text) {
        for (MarketBoardStatus status : MarketBoardStatus.values()) {
            if (status.name().equals(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
