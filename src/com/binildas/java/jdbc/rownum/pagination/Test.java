/*
 * Copyright (c) 2008 Binildas A Christudas. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of the author, or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. AUTHOR AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE AUTHOR
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF THE AUTHOR HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */

package com.binildas.java.jdbc.rownum.pagination;

import org.apache.log4j.Logger;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Test{

    private static Logger logger = Logger.getLogger(Test.class.getPackage().getName());
    private static final int BATCH_SIZE = 40;
    private static final int THREAD_COUNT = 4;
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE = 7;
    private static final long KEEP_ALIVE_TIME = 1L;
	private static final int BLOCKING_QUEUE_CAPACITY = 5;

	public static void main(String[] args)throws Exception{

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		Test test = new Test();
		//test.testGetCustomer();
		//test.testGetAllCustomers();
		//test.testGetCustomersSortById();
		//test.testGetCustomerCount();
		test.testGetCustomersSortByIdInBatch();

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}


	private void testGetCustomer(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		CustomerDao customerDao = new CustomerDaoJdbc();
		Customer customer = customerDao.getCustomer(1);

		logger.debug("Customer : " + customer);

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}

	private void testGetAllCustomers(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		CustomerDao customerDao = new CustomerDaoJdbc();
		List customers = customerDao.getAllCustomers();
		Customer customer = null;

		logger.debug("-----------------------------------------");
		logger.debug("customers.size() : " + customers.size());
		for(Iterator iterator = customers.iterator(); iterator.hasNext();){
			customer = (Customer) iterator.next();
			logger.debug("customer : " + customer);
		}
		logger.debug("-----------------------------------------");

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}

	private void testGetCustomersSortById(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		CustomerDao customerDao = new CustomerDaoJdbc();
		List customers = customerDao.getCustomersSortById(16, 20);
		Customer customer = null;

		logger.debug("-----------------------------------------");
		logger.debug("customers.size() : " + customers.size());
		for(Iterator iterator = customers.iterator(); iterator.hasNext();){
			customer = (Customer) iterator.next();
			logger.debug("customer : " + customer);
		}
		logger.debug("-----------------------------------------");

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}

	private void testGetCustomerCount(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		CustomerDao customerDao = new CustomerDaoJdbc();
		int count = customerDao.getCustomerCount();

		logger.debug("count : " + count);

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}



	private void testGetCustomersSortByIdInBatch()throws Exception{

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		int numBatch = 0;

		CustomerDao customerDao = new CustomerDaoJdbc();
		int count = customerDao.getCustomerCount();

		//ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue());
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, new LinkedBlockingQueue(BLOCKING_QUEUE_CAPACITY));


		List bucketToCollect = Collections.synchronizedList(new ArrayList());
		Callable callable = null;
		Customer customer = null;

		int batch = 0;

		Collection collection = new ArrayList();

		logger.info("Batch #");
		for(int i = 0; i < count; i += BATCH_SIZE){

			if(logger.isInfoEnabled()){
				System.out.print(" " + (++numBatch));
			}
			batch = (count - i) > BATCH_SIZE ? BATCH_SIZE : (count - i);

			callable = new ObjectRelationalQueryTask(bucketToCollect, (i + 1), (i + batch));
			collection.add(callable);
		}
		if(logger.isInfoEnabled()){
			System.out.print("\n");
		}

		threadPoolExecutor.invokeAll(collection);

		logger.debug("-----------------------------------------");
		logger.debug("bucketToCollect.size() : " + bucketToCollect.size());
		for(Iterator iterator = bucketToCollect.iterator(); iterator.hasNext();){
			customer = (Customer) iterator.next();
			logger.debug("customer : " + customer);
		}
		logger.debug("-----------------------------------------");

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));
	}

}