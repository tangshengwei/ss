package com.deallinker.ss.security.config;

import com.deallinker.ss.security.dynamicauthentication.MyFilterSecurityInterceptor;
import com.deallinker.ss.security.handler.CustomAuthenticationFailureHandler;
import com.deallinker.ss.security.handler.CustomAuthenticationSuccessHandler;
import com.deallinker.ss.security.mobile.MobileCodeAuthenticationSecurityConfig;
import com.deallinker.ss.security.mobile.MobileValidateFilter;
import com.deallinker.ss.security.properties.SecurityProperties;
import com.deallinker.ss.security.session.CustomLogoutHandler;
import com.deallinker.ss.security.userdetail.CustomUserDetailsService;
import com.deallinker.ss.security.verifycode.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private MobileCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    private MobileValidateFilter mobileValidateFilter;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;

    /**
     * 当同个用户session数量超过指定值之后 ,会调用这个实现类
     */
    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    /**
     * 为了解决退出重新登录问题
     * @return
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * 退出清除缓存
     */
    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    @Autowired
    private DataSource dataSource;

    /**
     * 记住我功能
     * @return
     */
    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        // 是否启动项目时自动创建表，true自动创建
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence charSequence) {
//                return charSequence.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return s.equals(charSequence.toString());
//            }
//        });

        auth.userDetailsService(userDetailsService); //.passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                // 手机验证码
                 addFilterBefore(mobileValidateFilter, UsernamePasswordAuthenticationFilter.class)
                // 图片验证码
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                // 动态鉴权过滤器
//                .addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class)
                // 表单登录方式
                .formLogin()
                // 输入用户名密码登录页面
                .loginPage(securityProperties.getAuthentication().getLoginPage())
                // 登录表单提交处理url, 默认是/login
                .loginProcessingUrl(securityProperties.getAuthentication().getLoginProcessingUrl())
                // 设置登陆成功页
//                .defaultSuccessUrl("/")
                // 默认的是 username
                .usernameParameter(securityProperties.getAuthentication().getUsernameParameter())
                // 默认的是 password
                .passwordParameter(securityProperties.getAuthentication().getPasswordParameter())
                // 登录失败处理器
                .failureHandler(customAuthenticationFailureHandler)
                // 登录成功处理器
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .authorizeRequests()
                // 不需要认证就能访问页面
                .antMatchers(
                        securityProperties.getAuthentication().getLoginPage(),
                        securityProperties.getAuthentication().getImageCodeUrl(),
                        securityProperties.getAuthentication().getMobileCodeUrl()
                ).permitAll()
                // 任何请求都要认证
                .anyRequest().authenticated()
                .and()
                // 记住我功能
                .rememberMe()
                // 保存登录信息
                .tokenRepository(jdbcTokenRepository())
                // 记住我有效时长
                .tokenValiditySeconds(securityProperties.getAuthentication().getTokenValiditySeconds())
                .and()
                // session管理
                .sessionManagement()
                // 当session失效后的处理类
                .invalidSessionStrategy(invalidSessionStrategy)
                // 每个用户在系统中最多可以有多少个session
                .maximumSessions(1)
                // 当用户达到最大session数后，则调用此处的实现
                .expiredSessionStrategy(sessionInformationExpiredStrategy)
//                .maxSessionsPreventsLogin(true) // 当一个用户达到最大session数,则不允许后面再登录
                // 为了解决退出重新登录问题
                .sessionRegistry(sessionRegistry())
                .and()
                .and()
                .logout()
                // 退出清除缓存
                .addLogoutHandler(customLogoutHandler)
                // 退出请求路径，默认 /logout
                .logoutUrl(securityProperties.getAuthentication().getLogoutUrl())
                // 退出成功后跳转地址
                .logoutSuccessUrl(securityProperties.getAuthentication().getLogoutSuccessUrl())
                // 退出后删除什么cookie值
                .deleteCookies(securityProperties.getAuthentication().getDeleteCookies())
        ;

        // 关闭CSRF跨域
        http.csrf().disable();
        // 将手机认证添加到过滤器链上
        http.apply(smsCodeAuthenticationSecurityConfig);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers(securityProperties.getAuthentication().getStaticPaths());
    }
}

