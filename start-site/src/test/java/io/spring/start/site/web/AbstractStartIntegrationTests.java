/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.spring.test.ProjectAssert;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import io.spring.start.site.web.AbstractStartIntegrationTests.Config;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Untar;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
@SpringBootTest(classes = Config.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractStartIntegrationTests {

	public File folder;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;

	@BeforeEach
	public void before(@TempDir Path folder) {
		this.restTemplate = this.restTemplateBuilder.build();
		this.folder = folder.toFile();
	}

	@LocalServerPort
	protected int port;

	protected String host = "localhost";

	protected String createUrl(String context) {
		return "http://" + this.host + ":" + this.port
				+ (context.startsWith("/") ? context : "/" + context);
	}

	/**
	 * Validate the "Content-Type" header of the specified response.
	 * @param response the response
	 * @param expected the expected result
	 */
	protected void validateContentType(ResponseEntity<String> response,
			MediaType expected) {
		MediaType actual = response.getHeaders().getContentType();
		assertThat(actual).isNotNull();
		assertThat(actual.isCompatibleWith(expected))
				.as("Non compatible media-type, expected " + expected + ", got " + actual)
				.isTrue();
	}

	/**
	 * Return a {@link ProjectAssert} for the following archive content.
	 * @param content the source content
	 * @return a project assert
	 */
	protected ProjectAssert zipProjectAssert(byte[] content) {
		return projectAssert(content, ArchiveType.ZIP);
	}

	/**
	 * Return a {@link ProjectAssert} for the following TGZ archive.
	 * @param content the source content
	 * @return a project assert
	 */
	protected ProjectAssert tgzProjectAssert(byte[] content) {
		return projectAssert(content, ArchiveType.TGZ);
	}

	protected ResponseEntity<byte[]> downloadArchive(String context) {
		return this.restTemplate.getForEntity(createUrl(context), byte[].class);
	}

	protected <T> ResponseEntity<T> execute(String contextPath, Class<T> responseType,
			String userAgentHeader, String... acceptHeaders) {
		HttpHeaders headers = new HttpHeaders();
		if (userAgentHeader != null) {
			headers.set("User-Agent", userAgentHeader);
		}
		if (acceptHeaders != null) {
			List<MediaType> mediaTypes = new ArrayList<>();
			for (String acceptHeader : acceptHeaders) {
				mediaTypes.add(MediaType.parseMediaType(acceptHeader));
			}
			headers.setAccept(mediaTypes);
		}
		else {
			headers.setAccept(Collections.emptyList());
		}
		return this.restTemplate.exchange(createUrl(contextPath), HttpMethod.GET,
				new HttpEntity<Void>(headers), responseType);
	}

	protected ProjectAssert projectAssert(byte[] content, ArchiveType archiveType) {
		try {
			File archiveFile = writeArchive(content);

			File project = new File(this.folder, "project");
			switch (archiveType) {
			case ZIP:
				unzip(archiveFile, project);
				break;
			case TGZ:
				untar(archiveFile, project);
				break;
			}
			return new ProjectAssert(project);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot unpack archive", ex);
		}
	}

	private void untar(File archiveFile, File project) {
		Untar expand = new Untar();
		expand.setProject(new Project());
		expand.setDest(project);
		expand.setSrc(archiveFile);
		Untar.UntarCompressionMethod method = new Untar.UntarCompressionMethod();
		method.setValue("gzip");
		expand.setCompression(method);
		expand.execute();
	}

	private void unzip(File archiveFile, File project) {
		Expand expand = new Expand();
		expand.setProject(new Project());
		expand.setDest(project);
		expand.setSrc(archiveFile);
		expand.execute();
	}

	protected File writeArchive(byte[] body) throws IOException {
		File archiveFile = new File(this.folder, "archive");
		try (FileOutputStream stream = new FileOutputStream(archiveFile)) {
			stream.write(body);
		}
		return archiveFile;
	}

	protected JSONObject readJsonFrom(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			try (InputStream stream = resource.getInputStream()) {
				String json = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
				String placeholder = "";
				placeholder = this.host + ":" + this.port;
				// Let's parse the port as it is random
				// TODO: put the port back somehow so it appears in stubs
				String content = json.replaceAll("@host@", placeholder);
				return new JSONObject(content);
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot read JSON from path=" + path);
		}
	}

	private enum ArchiveType {

		ZIP,

		TGZ

	}

	@EnableAutoConfiguration
	public static class Config {

		// Disable metadata fetching from spring.io
		@Bean
		public InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy() {
			return (metadata) -> metadata;
		}

		@Bean
		public HomeController homeController(InitializrMetadataProvider metadataProvider,
				ResourceUrlProvider resourceUrlProvider) {
			return new HomeController(metadataProvider, resourceUrlProvider);
		}

	}

}
