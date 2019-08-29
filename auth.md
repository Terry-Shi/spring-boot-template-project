# 认证授权服务

### 处理流程
1. 客户端用username和密码login，发给“认证授权服务”服务验证账号。
成功后网关生成Token。token中加密保存了clientId和token过期时间。
基于Token认证的好处如下：
服务端无状态：Token 机制在服务端不需要存储 session 信息，因为 Token 自身包含了所有用户的相关信息。
性能较好，因为在验证 Token 时不用再去访问数据库或者远程服务进行权限校验，可以提升性能。

2. 以后客户的请求中一律在http header中包含这个token作为自己身份的标示。网关会解析检查token的值是否合法，是否过期等，解析出username放入http header。下游的服务即可直接取得username
参考：rest_api_test.http 文件中测试例子。

####   JWT 刷新方案
一种常见的做法是增加一个refreshToken（原来的token称为 accessToken）
例如accessToken有效时间15分钟，refreshToken的有效时间30分钟，当前端使用accessToken发请求时发现过期则用refreshToken重新获取一套新的token，包含一套新的accessToken和refreshToken。
也就是refreshToken的有效时间才是真正的JWT有效时间。

或者前端有一个独立线程，每隔10分钟重新获得一次新的token。

