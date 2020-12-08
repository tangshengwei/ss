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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    // 校验手机验证码
    @Autowired
    private MobileValidateFilter mobileValidateFilter;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 明文+随机盐值》加密存储
        return new BCryptPasswordEncoder();
    }

    /**
     * 当同个用户session数量超过指定值之后 ,会调用这个实现类
     */
    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

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
    protected void configure(HttpSecurity http) throws Exception {
        http.
                // 手机验证码
                 addFilterBefore(mobileValidateFilter, UsernamePasswordAuthenticationFilter.class)
                // 图片验证码
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                // 动态鉴权过滤器
                .addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class)
                .formLogin()
                .loginPage(securityProperties.getAuthentication().getLoginPage())
                // 登录表单提交处理url, 默认是/login
                .loginProcessingUrl(securityProperties.getAuthentication().getLoginProcessingUrl())
                // 设置登陆成功页
//                .defaultSuccessUrl("/")
                // 默认的是 username
                .usernameParameter(securityProperties.getAuthentication().getUsernameParameter())
                // 默认的是 password
                .passwordParameter(securityProperties.getAuthentication().getPasswordParameter())
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers(
                        securityProperties.getAuthentication().getLoginPage(),
                        securityProperties.getAuthentication().getImageCodeUrl(),
                        securityProperties.getAuthentication().getMobileCodeUrl()
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .rememberMe()
                // 保存登录信息
                .tokenRepository(jdbcTokenRepository())
                // 记住我有效时长
                .tokenValiditySeconds(securityProperties.getAuthentication().getTokenValiditySeconds())
                .and()
                .sessionManagement()// session管理
                .invalidSessionStrategy(invalidSessionStrategy) //当session失效后的处理类
                .maximumSessions(1) // 每个用户在系统中最多可以有多少个session
                .expiredSessionStrategy(sessionInformationExpiredStrategy)// 当用户达到最大session数后，则调用此处的实现
//                .maxSessionsPreventsLogin(true) // 当一个用户达到最大session数,则不允许后面再登录
////                .sessionRegistry(sessionRegistry())
                .and()
                .and()
                .logout()
                .addLogoutHandler(customLogoutHandler) // 退出清除缓存
                .logoutUrl("/user/logout") // 退出请求路径，默认 /logout
                .logoutSuccessUrl("/login/page") //退出成功后跳转地址
                .deleteCookies("JSESSIONID") // 退出后删除什么cookie值
        ;
        // 关闭CSRF跨域
        http.csrf().disable();
        //将手机认证添加到过滤器链上
        http.apply(smsCodeAuthenticationSecurityConfig);

    }

    /**
     * 退出清除缓存
     */
    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    /**
     * 为了解决退出重新登录问题
     * @return
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers(securityProperties.getAuthentication().getStaticPaths());
    }
}

