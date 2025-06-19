// package org.schedx.config;
//
// import org.junit.Test;
// import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
// import org.springframework.util.ReflectionUtils;
//
// import java.lang.reflect.Field;
//
// /**
//  * <p>  </p>
//  * <p>创建于 2025-06-08 21:30 21:30 </p>
//  *
//  * @author <a href="mailto:fgwang.660@gmail.com">witt</a>
//  * @version v1.0
//  * @since
//  */
// public class ReflectionUtilTest {
//
//     @Test
//     public void test() {
//         SchedXAnnotationBeanPostProcessor schedXAnnotationBeanPostProcessor = new SchedXAnnotationBeanPostProcessor((SchedXTaskRegistrar) null);
//         System.out.println(schedXAnnotationBeanPostProcessor.getClass().getGenericSuperclass());
//         // final Field field = ReflectionUtils.findField(ScheduledAnnotationBeanPostProcessor.class,"nonAnnotatedClasses");
//         // System.out.println(field);
//     }
// }