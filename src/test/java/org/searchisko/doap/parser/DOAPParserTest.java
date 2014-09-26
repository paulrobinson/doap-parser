/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */

package org.searchisko.doap.parser;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.openrdf.repository.RepositoryException;
import org.searchisko.doap.json.Converter;
import org.searchisko.doap.model.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Lukas Vlcek (lvlcek@redhat.com)
 */
public class DOAPParserTest {

	private DOAPParser parser = new DOAPParser();

	@Before
	public void setUp() throws RepositoryException {
		parser.setUp();
	}

	@After
	public void tearDown() throws RepositoryException {
		parser.tearDown();
	}

	/**
	 * Read file from classpath into String. UTF-8 encoding expected.
	 *
	 * @param filePath in classpath to read data from.
	 * @return file content.
	 * @throws IOException
	 */
	public String readStringFromClasspathFile(String filePath) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(getClass().getResourceAsStream(filePath), stringWriter, "UTF-8");
		return stringWriter.toString();
	}

	/**
	 * Test parsing of project.
	 * @throws Exception
	 */
	@Test
	public void testProjectParsing() throws Exception {
		parser.loadLocalFile(getClass().getResource("/doap-asf-examples/doap_camel.rdf").getPath());
		Project project = parser.getProject();
		String json = Converter.objectToJSON(project);
		JSONAssert.assertEquals(readStringFromClasspathFile("/doap-json/camel.json"), json, JSONCompareMode.NON_EXTENSIBLE);
	}

	/**
	 * Test parsing of person objects from project.
	 * @throws Exception
	 */
	@Test
	public void testPersonParsing() throws Exception {
		parser.loadLocalFile(getClass().getResource("/doap-asf-examples/doap_maven.rdf").getPath());
		Collection<Person> persons = parser.getPersons();
		// try to convert each person
		for (Person p : persons) {
			String json = Converter.objectToJSON(p);
		}
		assertEquals(14, persons.size());
	}

	/**
	 * Test parsing of version objects from project.
	 * @throws Exception
	 */
	@Test
	public void testVersionParsing() throws Exception {
		parser.loadLocalFile(getClass().getResource("/doap-asf-examples/doap_maven.rdf").getPath());
		Collection<Version> versions = parser.getVersions();
		// try to convert each version
		for (Version p : versions) {
			String json = Converter.objectToJSON(p);
		}
		assertEquals(15, versions.size());
	}

	/**
	 * Perl defines four repositories, test that we can parse all of them.
	 * @throws Exception
	 */
	@Test
	public void testRepositoryParsing() throws Exception {
		parser.loadLocalFile(getClass().getResource("/doap-asf-examples/doap_perl.rdf").getPath());
		Collection<? extends Repository> repositories = parser.getSVNRepository();
		for (Repository r : repositories) {
			String json = Converter.objectToJSON(r);
		}
		assertEquals(4, repositories.size());
	}

	@Test
	public void testAllRepositoryTypesParsing() throws Exception {
		parser.loadLocalFile(getClass().getResource("/doap-artificial-examples/doap_multi_repositories.rdf").getPath());
		Collection<Repository> repositories = new ArrayList<Repository>();
		repositories.addAll(parser.getArchRepository());
		repositories.addAll(parser.getBazaarBranch());
		repositories.addAll(parser.getBKRepository());
		repositories.addAll(parser.getCVSRepository());
		repositories.addAll(parser.getDarcsRepository());
		repositories.addAll(parser.getGitRepository());
		repositories.addAll(parser.getHgRepository());
		repositories.addAll(parser.getSVNRepository());
		String json = Converter.objectToJSON(repositories);
		assertEquals(8, repositories.size());
	}
}
