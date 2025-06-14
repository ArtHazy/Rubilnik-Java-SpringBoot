package org.rubilnik.auth_service.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("server")
@EnableJpaRepositories(basePackages = "org.rubilnik.auth_service.repositories")
@EntityScan(basePackages = "org.rubilnik.core")
public class JpaConfig {}
