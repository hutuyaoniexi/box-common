package com.box.common.security.config;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.response.Result;
import com.box.common.core.util.JsonUtils;
import com.box.common.security.annotation.CurrentUser;
import com.box.common.security.annotation.RequirePermission;
import com.box.common.security.context.SecurityContextHolder;
import com.box.common.security.context.SecurityUser;
import com.box.common.security.filter.JwtAuthenticationFilter;
import com.box.common.security.permission.PermissionEvaluator;
import com.box.common.security.permission.PermissionService;
import com.box.common.security.properties.SecurityProperties;
import com.box.common.security.token.JwtTokenParser;
import com.box.common.security.token.JwtTokenProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 安全自动配置。
 *
 * <p>仅面向前后端分离场景的接口鉴权，不提供默认登录页。</p>
 * <p>enabled=true 时启用 JWT 鉴权链；enabled=false 时注册全放行过滤链，并继续关闭 Spring Security 默认登录页。</p>
 */
@AutoConfiguration(beforeName = "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
@ConditionalOnClass(SecurityFilterChain.class)
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtTokenProvider jwtTokenProvider(SecurityProperties securityProperties) {
        return new JwtTokenProvider(securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtTokenParser jwtTokenParser(SecurityProperties securityProperties,
                                         JwtTokenProvider jwtTokenProvider) {
        return new JwtTokenParser(securityProperties, jwtTokenProvider);
    }

    @Bean
    @ConditionalOnMissingBean(PermissionService.class)
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public PermissionService permissionService() {
        return new PermissionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtAuthenticationFilter jwtAuthenticationFilter(SecurityProperties securityProperties,
                                                           JwtTokenParser jwtTokenParser) {
        return new JwtAuthenticationFilter(securityProperties, jwtTokenParser);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityProperties securityProperties,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        disableDefaultSecurityFeatures(http);

        http.sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint((request, response, exception) ->
                                writeJson(response, 401, ErrorCode.UNAUTHORIZED.code(), ErrorCode.UNAUTHORIZED.message()))
                        .accessDeniedHandler((request, response, exception) ->
                                writeJson(response, 403, ErrorCode.FORBIDDEN.code(), ErrorCode.FORBIDDEN.message())))
                .authorizeHttpRequests(registry -> {
                    List<String> permitPaths = securityProperties.getPermitPaths();
                    if (permitPaths != null && !permitPaths.isEmpty()) {
                        registry.requestMatchers(permitPaths.toArray(String[]::new)).permitAll();
                    }

                    if (securityProperties.isAuthenticateAllPaths()) {
                        registry.anyRequest().authenticated();
                    } else {
                        registry.anyRequest().permitAll();
                    }
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "false")
    public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        disableDefaultSecurityFeatures(http);

        // 关闭安全组件时仍显式提供过滤链，避免回退到 Spring Boot 默认登录页。
        http.authorizeHttpRequests(registry -> registry.anyRequest().permitAll());
        return http.build();
    }

    @Bean(name = "requirePermissionAdvisor")
    @ConditionalOnMissingBean(name = "requirePermissionAdvisor")
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DefaultPointcutAdvisor requirePermissionAdvisor(PermissionService permissionService) {
        ComposablePointcut pointcut = new ComposablePointcut(
                new AnnotationMatchingPointcut(RequirePermission.class, true)
        ).union(
                new AnnotationMatchingPointcut(null, RequirePermission.class, true)
        );

        MethodInterceptor interceptor = invocation -> {
            Class<?> targetClass = invocation.getThis() == null
                    ? invocation.getMethod().getDeclaringClass()
                    : AopUtils.getTargetClass(invocation.getThis());

            Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
            RequirePermission annotation = AnnotationUtils.findAnnotation(method, RequirePermission.class);
            if (annotation == null) {
                annotation = AnnotationUtils.findAnnotation(targetClass, RequirePermission.class);
            }

            if (annotation != null) {
                permissionService.checkPermissions(annotation.value(), annotation.requireAll());
            }
            return invocation.proceed();
        };

        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    @Bean(name = "securityWebMvcConfigurer")
    @ConditionalOnClass(WebMvcConfigurer.class)
    @ConditionalOnMissingBean(name = "securityWebMvcConfigurer")
    @ConditionalOnProperty(prefix = "box.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public WebMvcConfigurer securityWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(new CurrentUserArgumentResolver());
            }
        };
    }

    private static void writeJson(jakarta.servlet.http.HttpServletResponse response,
                                  int httpStatus,
                                  int code,
                                  String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(httpStatus);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonUtils.toJson(Result.fail(code, message)));
    }

    private static void disableDefaultSecurityFeatures(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable);
    }

    private static final class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(CurrentUser.class)
                    && SecurityUser.class.isAssignableFrom(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter,
                                      ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest,
                                      WebDataBinderFactory binderFactory) {
            return SecurityContextHolder.required();
        }
    }
}
