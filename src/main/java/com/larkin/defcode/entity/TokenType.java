package com.larkin.defcode.entity;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS("Access"), REFRESH("Refresh");

    TokenType(String value) {
        this.value = value;
    }

    private final String value;
}
