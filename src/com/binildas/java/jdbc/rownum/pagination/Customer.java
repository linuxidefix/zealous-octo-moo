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

import java.io.Serializable;

public class Customer implements Serializable{

	private int customerId;
	private String customerName;
	private int age;

	public int getCustomerId(){
		return customerId;
	}
	public void setCustomerId(int customerId){
		this.customerId = customerId;
	}

	public String getCustomerName(){
		return customerName;
	}
	public void setCustomerName(String customerName){
		this.customerName = customerName;
	}

	public int getAge(){
		return age;
	}
	public void setAge(int age){
		this.age = age;
	}

	public String toString(){

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append("customerId : " ).append(customerId)
					.append("; customerName : " ).append(customerName)
					.append("; age : " ).append(age);

		return stringBuffer.toString();
	}

}