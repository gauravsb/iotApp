package com.tenten.bigdata.api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenten.bigdata.api.entity.ActiveConsumerResponseModel;
import com.tenten.bigdata.api.entity.ProductResponseModel;
import com.tenten.bigdata.api.service.DataExtractionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/iotApp/")
@Slf4j
public class DataApiController {

	@Autowired
	private DataExtractionService dataExtractionService;

	/**
	 * Given a starting and ending date, return the top 10 popular products in
	 * those dates.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return Top 10 popular products between fromDate and toDate
	 */
	@RequestMapping(value = "/popularProducts/{from}/{to}", method = RequestMethod.GET)
	public ResponseEntity<ProductResponseModel> getPopularProducts(
			@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

		// perform validation of request parameters and headers, if required

		// generate response
		ProductResponseModel productResponseModel = new ProductResponseModel();
		productResponseModel.setProductList(dataExtractionService.getPopularProducts(fromDate, toDate));

		return ResponseEntity.ok(productResponseModel);
	}

	/**
	 * Get the count of active consumers within a starting and ending date.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return consumers who purchased between fromDate and toDate
	 */
	@RequestMapping(value = "/activeConsumers/{from}/{to}", method = RequestMethod.GET)
	public ResponseEntity<ActiveConsumerResponseModel> getActiveConsumers(
			@RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {

		// perform validation of request parameters and headers, if required

		// generate response
		ActiveConsumerResponseModel activeConsumerResponseModel = new ActiveConsumerResponseModel();
		activeConsumerResponseModel.setActiveConsumerCount(dataExtractionService.getActiveConsumers(fromDate, toDate));

		return ResponseEntity.ok(activeConsumerResponseModel);
	}

}
