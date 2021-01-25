package com.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.batch.model.Transaction;

public class CustomItemWriter implements ItemWriter<Transaction> {

	@Override
	public void write(List<? extends Transaction> items) throws Exception {
		System.out.println("write");
	}

}
