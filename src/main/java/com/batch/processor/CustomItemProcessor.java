package com.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.batch.model.Transaction;

public class CustomItemProcessor implements ItemProcessor<Transaction, Transaction> {

    public Transaction process(Transaction item) {
    	System.out.println("process");
        return item;
    }
}
