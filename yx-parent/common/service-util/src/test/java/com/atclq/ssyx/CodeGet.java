package com.atclq.ssyx;
//mybatis-plus代码生成器 来自官方

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.sun.xml.internal.bind.v2.TODO;

public class CodeGet {

    public static void main(String[] args) {

        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("D:\\programme\\IdeaProjects\\ssyx-parent\\service\\service-order"+"/src/main/java");//TODO 输出路径***改***/

        gc.setServiceName("%sService");	//去掉Service接口的首字母I
        gc.setAuthor("atclq");
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/shequ-order?serverTimezone=GMT%2B8&useSSL=false");//TODO 数据库连接地址***改***/
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.atclq.ssyx");
        pc.setModuleName("order"); //TODO 模块名***改***/
        pc.setController("controller");
        pc.setService("service");
        pc.setMapper("mapper");
        mpg.setPackageInfo(pc);

        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();

        //TODO 指定表名（可指定多个，用逗号分隔）***改***/
        strategy.setInclude("order_info","order_item");

        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略

        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作

        strategy.setRestControllerStyle(true); //restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符

        mpg.setStrategy(strategy);

        // 6、执行
        mpg.execute();
    }
}
