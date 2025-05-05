
## 📚简介
`SchedX`是一个Java类库，对原生Spring scheduling功能进行了增强，并内置了若干扩展点，灵感来源于`xxl-job`。旨在降低Java中小型项目集成分布式调度任务的下限，该库不改变Spring原有`@Scheduled`的设计，理论上一个jar支持所有Spring版本，仅做增强增强，相关功能还在持续开发中，尽情期待！


## 🏗️开发计划

- [x] `Scheduled`任务链路追踪
- [ ] `Scheduled`多节点并发锁 
	- [ ] 基于`redis`实现
	- [ ] 基于`redisson`实现
	- [ ] 基于数据库行锁实现
- [x] 优雅启动与停止
- [ ] `ScheduledTask`实时运行日志
- [ ] `ScheduledTask`方法动态参数（多租户）
	- [x] 解析器注册管理
	- [x] `String`解析器
	- [x] 不可反序列化类型（`IO`，`ServletRequest`，`ServletResponse`等）解析器
- [ ] `ScheduledJob`生命周期管理
	- [ ] 启动时注册
	- [ ] 停止Job
	- [ ] 恢复Job运行
	- [ ] 修改Job

- [ ] `ScheduledTask`生命周期管理
	- [ ] 启动时注册
	- [ ] 单次立即运行
	- [ ] 暂停task运行
	- [ ] 恢复task运行
	- [ ] 实时修改task
	- [ ] Task状态变更通知
- [ ] `Spring`兼容性适配
	- [x] `SpringBoot2.3.x`
	- [x] `SpringBoot2.7.x`
	- [x] `SpringBoot3.0.x`
	- [x] `SpringBoot3.1.x`
	- [x] `SpringBoot3.2.x`
	- [x] `SpringBoot3.4.x`