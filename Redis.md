# 缓存redis
### 目的：
    商品库存进行缓存,将多余的请求直接拒绝.减少数据库的访问。

### 下载:
    linux:   https://redis.io/download
    windows: https://github.com/MicrosoftArchive/redis/releases
    ---将Redis加入Windows服务,执行: redis-server --service-install redis.windows.conf
    
### 操作文档：
    http://www.redis.net.cn/order/
    
### SpringBoot集成:
     pom:
     <dependency>
     	<groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-redis</artifactId>
        <version>1.4.7.RELEASE</version>
     </dependency>
     
     application.yml:
     spring:
        redis:
           host: localhost
           port: 6379
           database: 0
           
     注入:
     @Autowired
     RedisTemplate<String, String> redisTemplate;