package com.tenten.bigdata.api.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Product implements Serializable {

	private static final long serialVersionUID = -534534051376127021L;

	int product_count;
	String product_code;
	
	public Product(int count, String code){
		product_count = count;
		product_code = code;
	}

}
