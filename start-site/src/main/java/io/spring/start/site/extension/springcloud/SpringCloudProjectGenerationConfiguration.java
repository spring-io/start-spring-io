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

package io.spring.start.site.extension.springcloud;

import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;

/**
 * Configuration for generation of projects that depend on Spring Cloud.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
public class SpringCloudProjectGenerationConfiguration {

	@Bean
	public SpringCloudFunctionBuildCustomizer springCloudFunctionBuildCustomizer(
			InitializrMetadata metadata, ResolvedProjectDescription description) {
		return new SpringCloudFunctionBuildCustomizer(metadata, description);
	}

	@Bean
	public SpringCloudGcpBomBuildCustomizer springCloudGcpBomBuildCustomizer(
			ResolvedProjectDescription description) {
		return new SpringCloudGcpBomBuildCustomizer(description);
	}

	@Bean
	public SpringCloudStreamBuildCustomizer springCloudStreamBuildCustomizer() {
		return new SpringCloudStreamBuildCustomizer();
	}

}
