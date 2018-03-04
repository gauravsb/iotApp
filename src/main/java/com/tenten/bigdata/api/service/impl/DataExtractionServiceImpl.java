package com.tenten.bigdata.api.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tenten.bigdata.api.entity.Product;
import com.tenten.bigdata.api.service.DataExtractionService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataExtractionServiceImpl implements DataExtractionService {

	@Override
	public List<Product> getPopularProducts(Date from, Date to) {
		return null;
	}

	@Override
	public int getActiveConsumers(Date from, Date to) {
		return 0;
	}

}
