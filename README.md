##测试用户
### 用户名、密码登录
* 用户名：a 密码：1234
* 用户名：b 密码：1234 
### 短信登录
* 手机号：11
* 手机号：22
### 备注
*  这个分支是在主分支基础上修改的，主要是支持了 token 登录，关闭了 session；
*  主分支：使用session，用于前后端不分离项目；
*  token分支：适用于前后端分离项目（前后端不分离也可以采用），关闭了session，用户登录获取到 token 后，
后续请求认证都是从token中获取的用户信息进行认证。
* 修改文件：主要修改 SpringSecurityConfig 配置，增加 JwtAuthenticationTokenFilter
* MobileCodeAuthenticationFilter 有写死的路径 