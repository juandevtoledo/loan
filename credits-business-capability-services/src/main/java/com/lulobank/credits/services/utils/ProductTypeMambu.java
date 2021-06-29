package com.lulobank.credits.services.utils;

public enum ProductTypeMambu {
    SAVING_ACCOUNT("8a8187256bd203cd016bd241ff5611cc"),
    LOAN_ACCOUNT("8a8187256bd203cd016bd241fed011c6"),
    ASSIGNED_BRANCH_KEY("8a8186e56bdc3c78016bdcbac4cb046a");

    private String key;

    ProductTypeMambu(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
