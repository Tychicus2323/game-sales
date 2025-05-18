package com.game.enums;

public enum GameTypeEnums {
	ONLINE(1),
    OFFLINE(2);

    private int value;

    GameTypeEnums(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameTypeEnums getGameType(int value) {
        for (GameTypeEnums type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid GameType value: " + value);
    }
}
