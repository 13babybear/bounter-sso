package com.bounter.sso.client.utility;

/**
 * 封装请求返回的json数据
 * @author simon
 *
 * @param <T>
 */
public class ResponseObject<T> {
	//请求成功失败的标志
	private boolean success;

	//返回的数据
    private T data;

    //请求出错时的错误信息
    private String errorMsg;
    
    //分页时的总记录数，不分页时为null
    private int totalCount;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
    
}
