/*
 * Copyright 2012-2021 the original author or authors.
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

package io.spring.start.site.extension.code.kotlin;

import java.util.function.Function;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.kotlin.KotlinVersionResolver;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;

/**
 * {@link KotlinVersionResolver} that determines the Kotlin version using the dependency
 * management from the project description's platform version.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class ManagedDependenciesKotlinVersionResolver implements KotlinVersionResolver {

	private static final Version SPRING_BOOT_2_5_0 = Version.parse("2.5.0");

	private final DependencyManagementVersionResolver resolver;

	private final Function<ProjectDescription, String> fallback;

	public ManagedDependenciesKotlinVersionResolver(DependencyManagementVersionResolver resolver,
			Function<ProjectDescription, String> fallback) {
		this.resolver = resolver;
		this.fallback = fallback;
	}

	@Override
	public String resolveKotlinVersion(ProjectDescription description) {
		// TODO: temporary to let users on current GA to benefit from latest fix
		if (description.getPlatformVersion().equals(SPRING_BOOT_2_5_0)) {
			return "1.5.10";
		}
		String kotlinVersion = this.resolver.resolve("org.springframework.boot", "spring-boot-dependencies",
				description.getPlatformVersion().toString()).get("org.jetbrains.kotlin:kotlin-reflect");
		return (kotlinVersion != null) ? kotlinVersion : this.fallback.apply(description);
	}

}
