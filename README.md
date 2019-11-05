# gmall
各个微服务的端口号
gmall-user-web:8080
gmall-user-manage-web:8081
gmall-istem-web:8082
gmall-search-web:8083
gmall-cart-web:8084
gmall-passport:8085
gmall-order-web:8086
gmall-payment:8087
gmall-user-service:8070
gmall-user-manege-service：8071
gmall-search-service:8073
gmall-cart-service:8074
gmall-order-service:8076
模块功能：模块一般由web层和service层一起组成一个微服务，但有的模块可能功能较少，所以没有service层
fastdfs-client-java:连接分布式文件系统的依赖
gmall-parent:管理所有微服务依赖的包的版本号
gmall-manage-service：是很重要的一个服务，
                     它提供了对商品销售属性和库存属性的增删查改操作，在使用其它web微服务是必须开启该微服务
gmall-manage-web:后台管理模块，主要功能是添加商品、管理库存，使用了fastdfs分布式文件存储系统
gmall-istem-web：商品选择模块，提供了对某个商品各个型号的选择服务，在此使用了redis作为缓存并使用了分布式锁
gmall-search-web：商品搜索服务，提供了对该平台所有商品的搜索，在此使用了搜索引擎elasticsearch
gmall-cart-web：购物车服务，用户在登录或不登录的情况下都可以把商品加入购物车，在不登录的时候购物车数据保存在cookie中，登录之后则保存到数据库中并同步到缓存里
gmall-passport：用户登录服务，提供了单点登录服务，并写了自己的注解用于区分是否需要登录



