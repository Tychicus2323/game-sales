package com.game.enums;

public enum ImportStatusEnums {
	PENDING, PROCESSING, COMPLETED, FAILED;
	
    @Override
    public String toString() {
        return name();
    }
}
