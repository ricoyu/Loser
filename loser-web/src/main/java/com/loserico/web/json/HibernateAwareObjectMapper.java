package com.loserico.web.json;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

/**
 * 如果配置该Mapper，OneToMany单向关联、延迟加载时，输出对象A，其关联对象List<B>不会输出到JSON
 * Spring配置如下：
 * 	<mvc:annotation-driven validator="validator">
 * 		<mvc:message-converters>
 * 			<bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
 * 				<property name="objectMapper">
 * 					<bean class="com.loserico.common.json.HibernateAwareObjectMapper" />
 * 				</property>
 * 			</bean>
 * 		</mvc:message-converters>
 * 	</mvc:annotation-driven>
 * @author Loser
 * @since Mar 31, 2016
 * @version 
 **/
public class HibernateAwareObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 986280161021286819L;

	public HibernateAwareObjectMapper() {
		registerModule(new Hibernate5Module());
//		setVisibility(PropertyAccessor.ALL, Visibility.NONE);
//		setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
	}
}