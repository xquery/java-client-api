/*
 * Copyright 2014-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marklogic.client.functionaltest;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.marklogic.client.query.QueryManager;
import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import org.junit.*;
public class TestSearchMultipleForests extends BasicJavaClientREST {

	private static String dbName = "TestSearchMultipleForestsDB";
	private static String [] fNames = {"TestSearchMultipleForestsDB-1", "TestSearchMultipleForestsDB-2"};
	private static String restServerName = "REST-Java-Client-API-Server";

	@BeforeClass 
	public static void setUp() throws Exception 
	{
		System.out.println("In setup");
		setupJavaRESTServer(dbName, fNames[0],  restServerName,8011);
		createForest(fNames[1],dbName);
		setupAppServicesGeoConstraint(dbName);
	}

	@Test	
	public void testSearchMultipleForest() throws IOException, SAXException, ParserConfigurationException, TransformerException
	{	
		System.out.println("Running testSearchMultipleForest");
		// connect the client
		DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8011, "rest-writer", "x", Authentication.DIGEST);

		DocumentManager docMgr = client.newDocumentManager();
		docMgr.setForestName("TestSearchMultipleForestsDB-1");
		StringHandle writeHandle1 = new StringHandle();
		for(int a = 1; a <= 10; a++ )
		{
			writeHandle1.set("<root>hello</root>");
			docMgr.write("/forest-A/file" + a + ".xml", writeHandle1);
		}

		docMgr.setForestName("TestSearchMultipleForestsDB-2");
		StringHandle writeHandle2 = new StringHandle();
		for(int b = 1; b <= 10; b++ )
		{
			writeHandle1.set("<root>hello</root>");
			docMgr.write("/forest-B/file" + b + ".xml", writeHandle1);
		}

		QueryManager queryMgr = client.newQueryManager();

		// create query def
		StringQueryDefinition querydef = queryMgr.newStringDefinition("");
		querydef.setCriteria("");

		SearchHandle sHandle = new SearchHandle();
		queryMgr.search(querydef, sHandle);
		System.out.println(sHandle.getTotalResults());
		assertTrue("Document count is incorrect", sHandle.getTotalResults() == 20);

		client.release();
	}

	@AfterClass	
	public static void tearDown() throws Exception
	{
		System.out.println("In tear down");
		tearDownJavaRESTServer(dbName, fNames, restServerName);
	}
}
