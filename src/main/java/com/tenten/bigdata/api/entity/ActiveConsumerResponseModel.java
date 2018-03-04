package com.tenten.bigdata.api.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ActiveConsumerResponseModel implements Serializable {

	private static final long serialVersionUID = -6916430465490219773L;
	
	int activeConsumerCount;
}