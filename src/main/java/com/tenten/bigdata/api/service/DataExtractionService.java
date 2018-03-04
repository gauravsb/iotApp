package com.tenten.bigdata.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tenten.bigdata.api.entity.Product;

@Service
public interface DataExtractionService {

	public List<Product> getPopularProducts(Date from, Date to);

	public int getActiveConsumers(Date from, Date to);

}
