package com.tenten.bigdata;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tenten.bigdata.api.service.impl.DataExtractionServiceImpl;

import junit.framework.Assert;

public class DataExtractionServiceTest {

	private DataExtractionServiceImpl dataExtractionService = new DataExtractionServiceImpl();
	private Date startDate;
	private Date endDate;

	@Before
	public void setUp() {
		startDate = new DateTime(2018, 3, 10, 2, 0).toDate();
		endDate = new DateTime(2018, 3, 13, 22, 0).toDate();
	}

	@Test
	public void getRowKeysFromDatesTest() {
		List<String> rowKeyList = dataExtractionService.getRowKeysFromDates(startDate, endDate);
		Assert.assertEquals(rowKeyList.get(0), "2018-03-10");
		Assert.assertEquals(rowKeyList.get(1), "2018-03-11");
		Assert.assertEquals(rowKeyList.get(2), "2018-03-12");
		Assert.assertEquals(rowKeyList.get(3), "2018-03-13");
	}

	@After
	public void tearDown() {

	}
}
