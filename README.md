# space-mybatis 使用

## 作者

XuLingLin Java CRUD开发工作者

个人博客：https://xulinglin.com
## 功能介绍

<ul>
    <li>对代码无侵入并且高效的 Mybatis 工具</li>
    <li>关联表，表查询。(避免业务关联表字段进行复杂繁琐的操作,简化获取关联表字段的字段值。只需要提供 MybatisInterceptor 实现类,代码美观简化。)</li>
    <li>数据权限实现,动态拼接sql 实现</li>
</ul>

## 插件实现
使用Mybatis Interceptor拦截查询结果处理。

## POM 引用

```
<dependency>
  <groupId>io.github.xulinglin</groupId>
  <artifactId>space-core</artifactId>
  <version>1.0.2.RELEASE</version>
</dependency>
```

## 使用说明

### 表关联查询

#### 1.在字段添加 FieldBind 注解,并且提供 Interceptor 实现类进行结果返回(目前查询结果集只支持Map进行拼接处理)

#### 2.调用 FieldInterceptor.setFieldValue()进行处理

FieldBind 属性：

column 为当前查询关联条件

interceptor 为 MybatisInterceptor 返回结果集超类

mybatis 为 true 时间调用 mybatis 进行数据查询才会执行,默认不执行

手动调用 FieldInterceptor.setFieldValue() 或者在方法添加 @ExecutedBind 注解

```
//角色名称
@FieldBind(column = "roleId",interceptor = RoleNameInterceptor.class,mybatis = true)
@TableField(exist = false)
private String roleName;
```

### 数据权限

#### 1.实现数据权限bean
```
/**
 * 权限配置,获取部门id
 * @return
 */
@Bean
public AuthorityInterface authorityInterface(){
    return new AuthorityInterfaceImpl();
}

/**
 * 权限插件
 * @return
 */
@Bean
public AuthorityConfig authorityConfig() {
//    return new AuthorityConfig("dept_id",new AuthorityInterfaceImpl()); 也可以直接用new方式,是否需要spring管理有开发人员控制
      return new AuthorityConfig("dept_id",authorityInterface());

}
```

#### 2.使用 @Authority 注解
```
@Authority
public IPage<User> pageService(Page<User> page, User user) {
    return this.page(page, new QueryWrapper<User>(user));
}
```

@Authority 添加注解后执行当前方法的线程内SQL全部动态加数据权限。

管理员用AuthorityAop.setAdmin()设置跳过数据权限操作. AuthorityAop支持管理员跟普通用户来回切换

注意： 如果不配置权限插件bean @Authority 声明也无效


## 如果对你有帮助,打赏项目 Img 包

## 打赏的大佬



----------

## 提交版本记录

1.2021年9月29日 1.0.0.RELEASE版  描述：初始化项目 

2.2021年10月1日 1.0.1.RELEASE版 (已发布)

描述：space-mybatis-core 改为 space-core 以及优化代码

2.2021年10月4日 1.0.2.RELEASE版 (已发布)

描述：添加 mybatis 数据权限插件,提供 ExecutedBind 返回执行