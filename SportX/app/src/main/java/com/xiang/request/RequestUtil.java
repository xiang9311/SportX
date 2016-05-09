package com.xiang.request;

import com.xiang.Util.Constant;
import com.xiang.Util.UserStatic;
import com.xiang.proto.nano.Common.RequestCommon;
import com.google.protobuf.nano.MessageNano;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class RequestUtil {
	
	// init okhttp client
	private static OkHttpClient client = getClient();
	public static OkHttpClient getClient(){
		OkHttpClient  tempOkHttp=new OkHttpClient();
		tempOkHttp.setConnectTimeout(20000,TimeUnit.MILLISECONDS);//5000
		tempOkHttp.setReadTimeout(15000,TimeUnit.MILLISECONDS);//10000
		tempOkHttp.setWriteTimeout(30000, TimeUnit.MILLISECONDS);//10000
		return tempOkHttp;
	}
	
	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			int lastIndex =imageURL.lastIndexOf("/");
			String subImageURL = URLEncoder.encode(imageURL.substring(lastIndex+1),"utf-8");
			String finalURL=imageURL.substring(0,lastIndex+1)+subImageURL;
			URL url = new URL(finalURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}


	public static class FormFile implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private byte[] data;
		private String filname;
		private String formname;
		private String contentType = "application/octet-stream";

		public FormFile(String filname, byte[] data, String formname,
				String contentType) {
			this.data = data;
			this.filname = filname;
			this.formname = formname;
			if (contentType != null)
				this.contentType = contentType;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public String getFilname() {
			return filname;
		}

		public void setFilname(String filname) {
			this.filname = filname;
		}

		public String getFormname() {
			return formname;
		}

		public void setFormname(String formname) {
			this.formname = formname;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

	}
	
	// com.xiang.proto buf

	/**
	 *
	 * @param cmdid
	 * @param timestamp
	 * @return
	 */
	public static RequestCommon getProtoCommon(
			int cmdid, long timestamp) {
		RequestCommon request = new RequestCommon();
		request.cmdid = cmdid;
		request.platform = 0;
		request.timestamp = timestamp;
		request.userid = UserStatic.userId;
		request.userkey = "123456";
		request.version = "1.0.0";
		return request;
	}
	
	public static RequestCommon getProtoCommon(
			int cmdid, long timestamp, int userid, String userKey) {
		RequestCommon request = new RequestCommon();
		request.cmdid = cmdid;
		request.platform = 0;
		request.timestamp = timestamp;
		request.userid = userid;
		request.userkey = userKey;
		request.version = "1.0.0";
		return request;
	}

	/**
	 *
	 * @param params
	 * @param url
	 * @param cmdid
	 * @param timestamps
	 * @return
	 */
	public static byte[] postWithProtobuf(MessageNano params, String url,
			int cmdid, long timestamps) {
		// post
		RequestBody formBody = null;
		try {
			com.squareup.okhttp.Request.Builder requestBuilder = new com.squareup.okhttp.Request.Builder();
			// add header
			requestBuilder.addHeader(Constant.HEADER_CMDID, cmdid + "");
			requestBuilder
					.addHeader(Constant.HEADER_TIMESTAMP, timestamps + "");
			// set body
			formBody = RequestBody.create(MediaType.parse("application/octet-stream")
					, MessageNano.toByteArray(params));
			requestBuilder.url(url);
			if (formBody != null) {
				requestBuilder.post(formBody);
			}

			com.squareup.okhttp.Request requestOk = requestBuilder.build();
			Response response = client.newCall(requestOk).execute();
			if (response.isSuccessful()) {
				return response.body().bytes();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}