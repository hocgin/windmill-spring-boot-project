## Windmill
> 防重放实现

[项目案例](./windmill-spring-boot-sample)

## 共识
- 仅请求方式为非 GET 请求进行签名
- 请求体格式为 JSON 格式
- 客户端签名放入请求头 `sign`
- 仅对请求体字符串进行 MD5 签名

## 请求例子
POST /example/handle

Header:
- sign: 813ab04c7214c3a14f44700aa791aa91

Body:
```json
{
    "nonce":1578106801925,
    "timestamp":1578106801925
}
```
