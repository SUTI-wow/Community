package com.suti.community;

import com.suti.community.dao.AlphaDao;
import com.suti.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
//导入NewCoderCommunity的配置类
@ContextConfiguration(classes = STCommunityApplication.class)
class STCommunityApplicationTests implements ApplicationContextAware {

	//Spring容器
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);
		//获取bean --方法一：按照类型获取 多个相同class的时候需要指定primary
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		//获取bean -- 方法二：按照名称获取 利用注解指定bean的名称
		AlphaDao alphaDao1 = applicationContext.getBean("AlphaHibernate",AlphaDao.class);
		System.out.println(alphaDao1.select());
	}

	//bean是单例的(默认) 可以通过bean增加@scope
	@Test
	public void testBeanManage(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		AlphaService alphaService1 = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService1);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	//依赖注入 自动装配bean给成员变量
	@Autowired
	@Qualifier("AlphaHibernate")//指定bean的名字来装配有相同类型的bean
	AlphaDao alphaDao;

	@Autowired
	AlphaService alphaService;

	@Autowired
	SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}

}
