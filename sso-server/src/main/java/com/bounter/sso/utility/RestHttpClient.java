package com.bounter.sso.utility;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * rest http请求的客户端工具 每个新的请求独立启动一个线程去发送，请求间不会相互堵塞
 * 
 * @author sheng.zhao
 *
 */
public class RestHttpClient {

	// 初始化HttpClient对象
	private static CloseableHttpClient httpClient = HttpClients
					.custom()
					.setConnectionManager(
									new PoolingHttpClientConnectionManager())
					.build();

	/**
	 * 启用新的线程发送Get请求， 利用 FutureTask的回调方法让主线程从新线程中获取返回内容
	 * 
	 * @param url
	 *        请求的url
	 * @return 服务器端响应内容字符串
	 * @throws Exception
	 */
	public static String sendHttpGetRequest (final String url) throws Exception {
		FutureTask<String> task = new FutureTask<String>(
						new Callable<String>() {

							@Override
							public String call () throws Exception {
								String result = "";
								// 创建HttpGet对象
								HttpGet httpGet = new HttpGet(url);
								// 发送get请求获取HttpResponse
								CloseableHttpResponse response = httpClient
												.execute(httpGet);
								try {
									// 如果服务器成功地返回响应
									if (response.getStatusLine()
													.getStatusCode() == 200) {
										// 获取服务器响应内容
										result = EntityUtils.toString(
														response.getEntity(),
														StandardCharsets.UTF_8);
									}
								} finally {
									response.close();
								}
								return result;
							}
						});
		new Thread(task).start();
		return task.get();
	}

	/**
	 * 启用新的线程发送Post请求， 利用 FutureTask的回调方法让主线程从新线程中获取返回内容
	 * 
	 * @param url
	 *        请求url
	 * @param reqParamsMap
	 *        请求参数,utf-8编码
	 * @return 服务器端响应内容字符串
	 * @throws Exception
	 */
	public static String sendHttpPostRequest (final String url,
					final Map<String, String> reqParamsMap) throws Exception {
		FutureTask<String> task = new FutureTask<String>(
						new Callable<String>() {

							@Override
							public String call () throws Exception {
								String result = "";
								// 创建HttpPost对象
								HttpPost httpPost = new HttpPost(url);
								// 对传递的请求参数进行封装
								List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
								for (String key : reqParamsMap.keySet()) {
									requestParams.add(new BasicNameValuePair(
													key, reqParamsMap.get(key)));
								}
								// 设置请求参数
								httpPost.setEntity(new UrlEncodedFormEntity(
												requestParams,
												StandardCharsets.UTF_8));
								// 发送post请求获取HttpResponse
								CloseableHttpResponse response = httpClient
												.execute(httpPost);
								try {
									// 如果服务器成功地返回响应
									if (response.getStatusLine()
													.getStatusCode() == 200) {
										// 获取服务器响应内容
										result = EntityUtils.toString(
														response.getEntity(),
														StandardCharsets.UTF_8);
									}
								} finally {
									response.close();
								}
								return result;
							}
						});
		new Thread(task).start();
		return task.get();
	}
}
