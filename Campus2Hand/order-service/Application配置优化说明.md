# Application.yml 配置优化说明

## ✅ 已修复的问题

### 问题描述
原配置文件中 `spring` 节点出现了**两次**：
- 第4行：`spring:` （包含 application、cloud.openfeign）
- 第41行：`spring:` （包含 cloud.sentinel）❌ **重复定义**

这会导致：
- ❌ YAML解析错误
- ❌ 后面的配置覆盖前面的配置
- ❌ Spring Boot启动失败或配置不生效

### 修复方案
将两个 `spring` 节点**合并为一个**，所有子配置放在同一个 `spring` 下：

```yaml
spring:
  application:
    name: order-service
  cloud:
    openfeign:          # Feign配置
      circuitbreaker:
        enabled: true
      client:
        config:
          default:
            loggerLevel: full
            connectTimeout: 5000
            readTimeout: 5000
    sentinel:           # Sentinel配置（合并到这里）
      transport:
        dashboard: localhost:8080
        port: 8719
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            data-id: ${spring.application.name}-sentinel-rules
            group-id: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

---

## 📋 完整配置结构

### 1. **Server 配置**
```yaml
server:
  port: 8083
```

### 2. **Spring 核心配置**
```yaml
spring:
  application:
    name: order-service
  cloud:
    # Feign 客户端配置
    openfeign:
      circuitbreaker:
        enabled: true
      client:
        config:
          default:
            loggerLevel: full
            connectTimeout: 5000
            readTimeout: 5000
    
    # Sentinel 流控配置
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            data-id: ${spring.application.name}-sentinel-rules
            group-id: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

### 3. **MyBatis Plus 配置**
```yaml
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.orderservice.entity
  configuration:
    map-underscore-to-camel-case: true      # 自动驼峰转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
```

### 4. **Actuator 监控配置**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"              # 暴露所有端点（生产环境建议限制）
  endpoint:
    health:
      show-details: always        # 显示详细健康信息
  metrics:
    export:
      prometheus:
        enabled: true             # 启用Prometheus指标导出
```

### 5. **日志配置**
```yaml
logging:
  level:
    com.orderservice: DEBUG
    com.orderservice.mapper: DEBUG
    com.alibaba.cloud.sentinel: INFO
```

---

## 🔍 配置详解

### Feign 配置
| 配置项 | 值 | 说明 |
|--------|-----|------|
| circuitbreaker.enabled | true | 启用熔断器 |
| loggerLevel | full | 完整日志（请求/响应头、体） |
| connectTimeout | 5000 | 连接超时5秒 |
| readTimeout | 5000 | 读取超时5秒 |

### Sentinel 配置
| 配置项 | 值 | 说明 |
|--------|-----|------|
| dashboard | localhost:8080 | Sentinel控制台地址 |
| port | 8719 | 客户端与控制台通信端口 |
| data-id | order-service-sentinel-rules | Nacos中的规则配置ID |
| rule-type | flow | 流控规则 |

### MyBatis Plus 配置
| 配置项 | 值 | 说明 |
|--------|-----|------|
| map-underscore-to-camel-case | true | 数据库user_id → Java userId |
| log-impl | StdOutImpl | 控制台打印SQL语句 |

### Actuator 配置
| 端点 | 访问地址 | 说明 |
|------|---------|------|
| health | /actuator/health | 健康检查 |
| metrics | /actuator/metrics | 性能指标 |
| info | /actuator/info | 应用信息 |
| env | /actuator/env | 环境变量 |
| prometheus | /actuator/prometheus | Prometheus格式指标 |

---

## ⚠️ 注意事项

### 1. 生产环境安全建议

**限制Actuator端点暴露**：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "health,info,metrics"  # 只暴露必要的端点
```

**添加安全认证**：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. Sentinel 可选配置

如果**不使用Sentinel**，可以删除整个sentinel配置块：
```yaml
# 删除这部分
sentinel:
  transport:
    dashboard: localhost:8080
    port: 8719
  datasource:
    ds1:
      nacos:
        ...
```

并删除pom.xml中的相关依赖。

### 3. MyBatis Plus 日志

**生产环境建议关闭SQL日志**：
```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
```

或者完全删除 `log-impl` 配置。

### 4. Feign 日志级别

**生产环境建议降低日志级别**：
```yaml
openfeign:
  client:
    config:
      default:
        loggerLevel: basic  # 或 none
```

日志级别说明：
- `none`: 无日志
- `basic`: 仅记录请求方法和URL
- `headers`: 记录基本信息和请求/响应头
- `full`: 记录完整信息（开发环境使用）

---

## 🎯 配置优先级

YAML配置的优先级（从高到低）：
1. 命令行参数
2. 环境变量
3. Nacos配置中心（bootstrap.yml）
4. application.yml（本地）
5. application-{profile}.yml

**注意**：bootstrap.yml中的配置会优先加载，application.yml中的配置可以覆盖bootstrap.yml。

---

## 📝 常用配置示例

### 开发环境 (application-dev.yml)
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

logging:
  level:
    com.orderservice: DEBUG
```

### 生产环境 (application-prod.yml)
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: sentinel.prod.com:8080

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

management:
  endpoints:
    web:
      exposure:
        include: "health,info"

logging:
  level:
    com.orderservice: WARN
  file:
    name: /var/log/order-service/app.log
```

---

## ✅ 验证配置

### 1. 启动服务
```bash
mvn spring-boot:run
```

### 2. 检查配置是否生效

**查看Feign配置**：
```bash
curl http://localhost:8083/actuator/env | grep feign
```

**查看Sentinel配置**：
```bash
curl http://localhost:8083/actuator/env | grep sentinel
```

**查看MyBatis Plus配置**：
```bash
curl http://localhost:8083/actuator/env | grep mybatis
```

### 3. 测试API
```bash
# 创建订单
curl -X POST http://localhost:8083/orders?userId=1 \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "productName": "测试商品",
    "price": 99.99,
    "quantity": 1,
    "tradeType": "FACE_TO_FACE"
  }'
```

---

## 🔧 故障排查

### 问题1：配置不生效
**原因**：YAML缩进错误  
**解决**：检查缩进是否为2个空格，不要使用Tab

### 问题2：Sentinel连接失败
**原因**：Sentinel控制台未启动  
**解决**：
```bash
java -Dserver.port=8080 -jar sentinel-dashboard.jar
```

### 问题3：Actuator端点404
**原因**：端点未暴露  
**解决**：检查 `management.endpoints.web.exposure.include` 配置

### 问题4：Feign调用超时
**原因**：超时时间设置过短  
**解决**：增加 `connectTimeout` 和 `readTimeout` 值

---

## 📚 参考文档

- [Spring Boot配置指南](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Sentinel官方文档](https://sentinelguard.io/zh-cn/)
- [MyBatis Plus配置](https://baomidou.com/pages/56bac0/)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)

---

## ✨ 总结

✅ **已修复**：spring配置重复问题  
✅ **已优化**：配置结构清晰，层次分明  
✅ **已完善**：包含完整的微服务治理配置  

现在的application.yml是一个**标准的生产级配置模板**！🎉
