package com.mobivery.utils;
/**
 * ResponseInfo encapsulates the response code for a request, this keeps the response metadata out of the DTOs returned
 * @author Mobivery
 *
 */
public class ResponseInfo
{
	// Status code
	private int statusCode;
	/**
	 * Status code to be set
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode=statusCode;
	}
	/**
	 * Returns status code for this response
	 * @return
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

}