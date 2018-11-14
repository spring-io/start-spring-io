/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;
import org.springframework.stereotype.Component;

/**
 * A {@link ProjectRequestPostProcessor} that automatically adds "spring-kafka-test" when
 * kafka is selected.
 *
 * @author wonwoo lee
 */
@Component
class KafkaTestRequestPostProcessor extends AbstractProjectRequestPostProcessor {

    private static final Version VERSION_1_5_0 = Version.parse("1.5.0.RC1");

    private static final Dependency KAFKA_TEST = Dependency.withId("spring-kafka-test",
    		"org.springframework.kafka", "spring-kafka-test", null,
    		Dependency.SCOPE_TEST);

    @Override
    public void postProcessAfterResolution(ProjectRequest request,
    		InitializrMetadata metadata) {
    	if (hasDependency(request, "kafka")
        		&& isSpringBootVersionAtLeastAfter(request, VERSION_1_5_0)) {
            request.getResolvedDependencies().add(KAFKA_TEST);
        }
    }
}