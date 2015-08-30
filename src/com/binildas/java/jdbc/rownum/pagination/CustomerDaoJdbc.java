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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

public class CustomerDaoJdbc implements CustomerDao{

    private static Logger logger = Logger.getLogger(CustomerDaoJdbc.class.getPackage().getName());

	private static final String DRIVER = "jdbc:oracle:thin:";
	private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	private static final String JDBC_URL = "jdbc:oracle:thin:scott/tiger@127.0.0.1:1521:orcl";

	private	static final String QUERY_CUSTOMER_FOR_ID = "select customerid, customername, age from customers where customerid = ?";
	private	static final String QUERY_ALL_CUSTOMERS = "select customerid, customername, age from customers";
	private	static final String QUERY_CUSTOMERS_SORT_BY_ID_01 = "select * from ( select /*+ FIRST_ROWS(";
	private	static final String QUERY_CUSTOMERS_SORT_BY_ID_02 = ") */ a.*, ROWNUM rnum from ( select customerid, customername, age from customers order by customerid ) a where ROWNUM<=";
	private	static final String QUERY_CUSTOMERS_SORT_BY_ID_03 = " ) where rnum>=";
	private	static final String QUERY_CUSTOMER_COUNT = "select count(*) from customers";

	public Customer getCustomer(int customerId){


		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		Connection connection = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Customer customer = null;

		try{
			connection = getConnection();
			preparedStatement = connection.prepareStatement(QUERY_CUSTOMER_FOR_ID);
			preparedStatement.setInt(1, customerId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
				logger.debug("Got at least one Customer.");
				customer = new Customer();
				customer.setCustomerId(resultSet.getInt(1));
				customer.setCustomerName(resultSet.getString(2));
				customer.setAge(resultSet.getInt(3));
			}
			else{
				logger.warn("Did not get any Customer.");
			}
		}
		catch(Exception exception){
			logger.error(exception.getMessage());
		}
        finally{

            try{
                if (resultSet!=null)
                    resultSet.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (preparedStatement!=null)
                    preparedStatement.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (connection!=null)
                    connection.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
        }

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));

		return customer;
	}

	public List getAllCustomers(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		Connection connection = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List list = null;
        Customer customer = null;

		try{
			connection = getConnection();
			preparedStatement = connection.prepareStatement(QUERY_ALL_CUSTOMERS);
			//preparedStatement.setString(1, "1");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
				if(list == null){
					list = new ArrayList();
				}
				customer = new Customer();
				customer.setCustomerId(resultSet.getInt(1));
				customer.setCustomerName(resultSet.getString(2));
				customer.setAge(resultSet.getInt(3));
				list.add(customer);
			}
		}
		catch(Exception exception){
			logger.error(exception.getMessage());
		}
        finally{

            try{
                if (resultSet!=null)
                    resultSet.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (preparedStatement!=null)
                    preparedStatement.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (connection!=null)
                    connection.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
        }

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));

		return list;
	}

	public List getCustomersSortById(int startIndex, int endIndex){

		Date start = new Date();
		//logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		Connection connection = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List list = null;
        Customer customer = null;
        String queryCustomersSortById = null;
        int times = 0;
        int randomInt = 0;
        Random random = new Random();

		try{
			connection = getConnection();

			//queryCustomersSortById = QUERY_CUSTOMERS_SORT_BY_ID_01 + ((endIndex - startIndex) + 1) + QUERY_CUSTOMERS_SORT_BY_ID_02 + endIndex + QUERY_CUSTOMERS_SORT_BY_ID_03 + startIndex;

			queryCustomersSortById = new StringBuffer()	.append(QUERY_CUSTOMERS_SORT_BY_ID_01)
														.append((endIndex - startIndex) + 1)
														.append(QUERY_CUSTOMERS_SORT_BY_ID_02)
														.append(endIndex)
														.append(QUERY_CUSTOMERS_SORT_BY_ID_03)
														.append(startIndex).toString();

			logger.debug("TX-ID : " + ThreadLocalTxCounter.get() + " | queryCustomersSortById : " + queryCustomersSortById);

			preparedStatement = connection.prepareStatement(queryCustomersSortById);
			//preparedStatement.setInt(1, startIndex);
			//preparedStatement.setInt(2, endIndex);


            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
				if(list == null){
					list = new ArrayList();
				}
				customer = new Customer();
				customer.setCustomerId(resultSet.getInt(1));
				customer.setCustomerName(resultSet.getString(2));
				customer.setAge(resultSet.getInt(3));
				list.add(customer);

				times++;
				if((times % 15) == 0){
					randomInt = random.nextInt(6);
					logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Processed : " + times + " | Waiting " + randomInt + " seconds...");
					try{
						Thread.currentThread().sleep(1000L * randomInt);
					}
					catch(InterruptedException interruptedException){
						;//ignored
					}
				}//if

			}//while
			if((times % 15) != 0){
				logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Processed : " + times + ".");
			}
		}
		catch(Exception exception){
			exception.printStackTrace();
			logger.error(exception.getMessage());
		}
        finally{

            try{
                if (resultSet!=null)
                    resultSet.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (preparedStatement!=null)
                    preparedStatement.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (connection!=null)
                    connection.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
        }

	    Date end = new Date();
		//logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));

		return list;
	}

	public int getCustomerCount(){

		Date start = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | Start ");

		Connection connection = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = -1;

		try{
			connection = getConnection();
			preparedStatement = connection.prepareStatement(QUERY_CUSTOMER_COUNT);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
				//logger.debug("Got Customer count.");
				count = resultSet.getInt(1);
			}
			else{
				//logger.warn("Did not get Customer count.");
			}
		}
		catch(Exception exception){
			logger.error(exception.getMessage());
		}
        finally{

            try{
                if (resultSet!=null)
                    resultSet.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (preparedStatement!=null)
                    preparedStatement.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
            try{
                if (connection!=null)
                    connection.close();
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
        }

	    Date end = new Date();
		logger.info("TX-ID : " + ThreadLocalTxCounter.get() + " | End | TimeElapsed(ms) : " + (end.getTime() - start.getTime()));

		return count;

	}

	private Connection getConnection()throws Exception{


		Connection connection = null;

		Class.forName (DRIVER_CLASS);
		//logger.debug("Driver found");
		connection = DriverManager.getConnection(JDBC_URL);
		//logger.debug("Connection got");

		return connection;
	}

}