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

package org.springframework.cloud.loadbalancer.core;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for XForwadedHeadersTransformer .
 *
 * @author Gandhimathi Velusamy
 */

class XForwardedHeadersTransformerTests {

	private final LoadBalancerProperties.XForwarded xForwarded = new LoadBalancerProperties().getxForwarded();

	private final ServiceInstance serviceInstance = mock(DefaultServiceInstance.class);

	private final ClientRequest request = mock(ClientRequest.class);

	@BeforeEach
	void setUp() {
		when(serviceInstance.getInstanceId()).thenReturn("test1");
		when(request.method()).thenReturn(HttpMethod.GET);
		when(request.url()).thenReturn(URI.create("https://spring.io"));
		when(request.headers()).thenReturn(new HttpHeaders());
	}

	@Test
	void shouldAppendXforwardedHeaderIfEnabledXforward() throws NullPointerException {
		xForwarded.setEnabled(true);
		XForwardedHeadersTransformer transformer = new XForwardedHeadersTransformer(xForwarded);

		ClientRequest newRequest = transformer.transformRequest(request, serviceInstance);

		assertThat(newRequest.headers()).containsKey("X-Forwarded-Host");
		assertThat(newRequest.headers().getFirst("X-Forwarded-Host")).isEqualTo("spring.io");
		assertThat(newRequest.headers()).containsKey("X-Forwarded-Proto");
		assertThat(newRequest.headers().getFirst("X-Forwarded-Proto")).isEqualTo("https");
	}

	@Test
	void shouldNotAppendXforwardedHeaderIfDefault() {
		XForwardedHeadersTransformer transformer = new XForwardedHeadersTransformer(xForwarded);
		ClientRequest newRequest = transformer.transformRequest(request, serviceInstance);
		assertThat(newRequest.headers()).doesNotContainKey("X-Forwarded-Host");
		assertThat(newRequest.headers()).doesNotContainKey("X-Forwarded-Proto");
	}

}
