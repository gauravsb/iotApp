package com.tenten.bigdata.api.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ProductResponseModel implements Serializable {

	private static final long serialVersionUID = -2854385357078871110L;
	
	List<Product>  productList;
}
