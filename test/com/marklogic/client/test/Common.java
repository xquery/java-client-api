package com.marklogic.client.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;

public class Common {
	final public static String USERNAME = "db-developer";
	final public static String PASSWORD = "x";
	final public static String HOST     = "localhost";
	final public static int    PORT     = 8011;

	static DatabaseClient client;
	static void connect() {
		DatabaseClientFactory factory = DatabaseClientFactory.newFactory();
		client = factory.connect(
			Common.HOST, Common.PORT, Common.USERNAME, Common.PASSWORD, Authentication.DIGEST
			);
	}
	static void release() {
		client = null;
	}

	static byte[] streamToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		byte[] b = new byte[1000];
		int len = 0;
		while (((len=is.read(b)) != -1)) {
			baos.write(b, 0, len);
		}
		return baos.toByteArray();
	}
}