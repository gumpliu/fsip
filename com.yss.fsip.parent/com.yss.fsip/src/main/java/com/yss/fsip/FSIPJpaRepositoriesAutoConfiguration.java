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

package com.yss.fsip;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

/***
 * 修改jpa自动化配置 JpaRepositoriesAutoConfiguration，
 * 		在FSIPJpaRepositoriesAutoConfigureRegistrar中修改EnableJpaRepositories默认值
 *      @EnableJpaRepositories(repositoryFactoryBeanClass = BaseJpaRepositoryFactoryBean.class)。
 * FSIPJpaRepositoriesAutoConfiguration 粘贴 JpaRepositoriesAutoConfiguration类，修改点：
 *    1、添加@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
 *    2、@Import(JpaRepositoriesAutoConfigureRegistrar.class) 改为
 *    		@Import(FSIPJpaRepositoriesAutoConfigureRegistrar.class)
 *
 * FSIPJpaRepositoriesAutoConfigureRegistrar 粘贴 JpaRepositoriesAutoConfigureRegistrar，修改点：
 *           @EnableJpaRepositories 修改默认默认值，改为
 *           @EnableJpaRepositories(repositoryFactoryBeanClass = BaseJpaRepositoryFactoryBean.class)
 *
 * @author lsp
 * @date 2020-02-25 10:00
 */

@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(JpaRepository.class)
@ConditionalOnMissingBean({ JpaRepositoryFactoryBean.class, JpaRepositoryConfigExtension.class })
@ConditionalOnProperty(prefix = "spring.data.jpa.repositories", name = "enabled",
		havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(JpaRepositoriesAutoConfiguration.class)
@Import(FSIPJpaRepositoriesAutoConfigureRegistrar.class)
@AutoConfigureAfter({ HibernateJpaAutoConfiguration.class,
		TaskExecutionAutoConfiguration.class })
public class FSIPJpaRepositoriesAutoConfiguration {

	@Bean
	@Conditional(BootstrapExecutorCondition.class)
	public EntityManagerFactoryBuilderCustomizer entityManagerFactoryBootstrapExecutorCustomizer(
			Map<String, AsyncTaskExecutor> taskExecutors) {
		return (builder) -> {
			AsyncTaskExecutor bootstrapExecutor = determineBootstrapExecutor(
					taskExecutors);
			if (bootstrapExecutor != null) {
				builder.setBootstrapExecutor(bootstrapExecutor);
			}
		};
	}

	private AsyncTaskExecutor determineBootstrapExecutor(
			Map<String, AsyncTaskExecutor> taskExecutors) {
		if (taskExecutors.size() == 1) {
			return taskExecutors.values().iterator().next();
		}
		return taskExecutors
				.get(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME);
	}

	private static final class BootstrapExecutorCondition extends AnyNestedCondition {

		BootstrapExecutorCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnProperty(prefix = "spring.data.jpa.repositories",
				name = "bootstrap-mode", havingValue = "deferred", matchIfMissing = false)
		static class DeferredBootstrapMode {

		}

		@ConditionalOnProperty(prefix = "spring.data.jpa.repositories",
				name = "bootstrap-mode", havingValue = "lazy", matchIfMissing = false)
		static class LazyBootstrapMode {

		}

	}

}
