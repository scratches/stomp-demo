/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.boot.samples.stomp.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.config.EnableWebSocketMessageBroker;
import org.springframework.messaging.simp.config.MessageBrokerConfigurer;
import org.springframework.messaging.simp.config.StompEndpointRegistry;
import org.springframework.messaging.simp.config.WebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnClass({ StompEndpointRegistry.class, MetricRepository.class})
public class StompMetricsAutoConfiguration {
	
	@Aspect
	@Component
	@EnableAspectJAutoProxy
	public static class MetricInterceptor implements WebSocketMessageBrokerConfigurer {

		private static Log logger = LogFactory.getLog(MetricInterceptor.class);
		
		@Autowired
		private MetricRepository metricRepository;
		
		@Override
		public void registerStompEndpoints(StompEndpointRegistry registry) {
			registry.addEndpoint("/stomp").withSockJS();
		}
		
		@Override
		public void configureMessageBroker(MessageBrokerConfigurer configurer) {
			configurer.enableSimpleBroker("/topic/");
		}
		
		@Autowired
		private MessageSendingOperations<String> messagingTemplate;

		@AfterReturning(pointcut = "(execution(* *..MetricRepository+.set(String,..)) || execution(* *..MetricRepository+.increment(String,..))) && target(repository) && args(name,..)")
		public void broadcast(MetricRepository repository, String name) {
			logger.debug("Updating: " + name);
			messagingTemplate.convertAndSend("/topic/metrics/" + name, repository.findOne(name));
		}

	}

}
