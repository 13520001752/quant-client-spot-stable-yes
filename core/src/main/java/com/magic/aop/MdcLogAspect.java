package com.magic.aop;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.constant.Constants;
import com.magic.utils.TraceIdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MDC 日志切面
 *
 * @author sevenmagicbeans
 * @date 2022/7/9
 */
@Slf4j
@Aspect
@Order(100)
@Component
public class MdcLogAspect {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();//用于存储执行方法的开始时间

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 匹配方法
     * 格式：execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
     * 这里问号表示当前项可以有也可以没有，其中各项的语义如下：
     * - modifiers-pattern：方法的可见性，如public，protected；
     * - ret-type-pattern：方法的返回值类型，如int，void等；
     * - declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
     * - name-pattern：方法名类型，如buisinessService()；
     * - param-pattern：方法的参数类型，如java.lang.String；
     * - throws-pattern：方法抛出的异常类型，如java.lang.Exception；
     * <p>
     * 此例表示匹配ap包及其下所有类的所有方法
     */
    @Pointcut("execution(public * com.magic.service..*.*(..))")
    private void logService(){}

    @Pointcut("execution(public * com.magic.controller..*.*(..))")
    private void logController(){}

    @Pointcut("execution(public * com.magic.kafka..*.*(..))")
    private void logKafka(){}

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    private void webPointcut() {
        // doNothing
    }


    @Pointcut("logController() || logKafka() || webPointcut()")
    private void logPointcut(){}


   // @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void logPointcut1() {
    }



    /**
     * 匹配com.magic.service.impl这个包下所有类的方法
     */
    //@Pointcut("within(ap.service.impl.*)")
    public void logPointcut2() {
    }

    /**
     * 前置通知: 该注解标注的方法在业务模块代码执行(即连接点)之前执行，其不能阻止业务模块的执行，除非抛出异常；
     *
     * @param joinPoint
     */
    @SneakyThrows
    //@Before("logPointcut()")
    public void doBefore(JoinPoint joinPoint) {

        //为了便于日志追踪，通过MDC增加uuid作为唯一表示, 在日志输出格式配置中使用%X{}获取自定义的值，本例：%X{logTraceId}
        MDC.put(Constants.TRACE_ID, TraceIdUtil.getTraceId());

        startTime.set(System.currentTimeMillis());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        log.info("doBefore URL: {}, HTTP_METHOD: {} ", request.getRequestURL(), request.getMethod());
        log.info("doBefore start method: {}, parms: {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                objectMapper.writeValueAsString(joinPoint.getArgs()));

    }

    /**
     * * 后置通知
     * 这里需要注意的是:
     * 如果参数中的第一个参数为JoinPoint，则第二个参数为返回值的信息
     * 如果参数中的第一个参数不为JoinPoint，则第一个参数为returning中对应的参数
     * returning：限定了只有目标方法返回值类型与通知方法的参数类型相同时才能执行后置返回通知，否则不执行，
     * 对于returning对应的通知方法参数为Object类型将匹配任何目标返回值
     *
     * @param returnValue
     * @throws Throwable
     */
    @SneakyThrows
    //@AfterReturning(returning = "returnValue", pointcut = "logPointcut()")
    public void doAfterReturning(Object returnValue) {
        //打印返回值，对于集合类型的数据只打印数据总数，不打印具体数据明细
        if (returnValue instanceof Collection) {
            log.info("doAfterReturning reture value is Collection  CollectionSize: {}", ((Collection) returnValue).size());
        } else {
            log.info("doAfterReturning retureValue: {} ", objectMapper.writeValueAsString(returnValue));
        }

        log.info("doAfterReturning end, cost: {} ms", System.currentTimeMillis() - startTime.get());
        startTime.remove();
        //用完后清除, 此例中可以不清除，应为mdc的key可以只有一个，每个请求过来时会覆盖该key的值，并不会产生更多的mdc的值
        //		MDC.clear();
    }

    /**
     * 后置异常通知
     * 定义一个名字，该名字用于匹配通知实现方法的一个参数名，当目标方法抛出异常返回后，将把目标方法抛出的异常传给通知方法；
     * throwing:限定了只有目标方法抛出的异常与通知方法相应参数异常类型时才能执行后置异常通知，否则不执行，
     * 对于throwing对应的通知方法参数为Throwable类型将匹配任何异常。
     * @param joinPoint
     */
    //@AfterThrowing(pointcut = "logPointcut()", throwing = "throwable")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
        log.info("doAfterThrowing start, flag: {}, cost: {} ms", throwable.getMessage(), System.currentTimeMillis() - startTime.get());
        startTime.remove();
        //用完后清除, 此例中可以不清除，应为mdc的key可以只有一个，每个请求过来时会覆盖该key的值，并不会产生更多的mdc的值
        //		MDC.clear();
    }

    /**
     * 后置最终通知（目标方法只要执行完了就会执行后置通知方法）, 在@AfterReturning/@doAfterThrowing之前执行
     *
     * @param joinPoint
     */
    //@After("logPointcut()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("doAfter...........................");
    }

    /**
     * 环绕通知：
     * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     * <p>
     * 注意用了环绕通知，就可以不要再用@Before、@After、@AfterReturning、@AfterThrowing通知了，因为环绕通知可以处理所有情况，再用的话有点多余了
     */
    @Around("logPointcut()")
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        //尝试获取http请求中的traceId
        String mdc = TraceIdUtil.getTraceId();
        MDC.put(Constants.TRACE_ID, mdc);
        long startTime1 = System.currentTimeMillis();


        Object result = null;
        String ipAddr =null;
        String requestUrl= null;
        String methodCommentName = null;
        String methodName = null;
        String className =null;
        String[] parameterNames = null;
        List<Object> argList = new ArrayList<>();
        try {
            result = proceedingJoinPoint.proceed();
            ServletRequestAttributes ss = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(null!=ss){
                HttpServletRequest request = ss.getRequest();
            }else{
                log.warn("RequestContextHolder.getRequestAttributes is null,result:{}",result);
                return result;
            }
            HttpServletRequest request = null !=ss ?ss.getRequest():null;
            // ip地址
            ipAddr = getRemoteHost(request);
            // 请求路径
            requestUrl = request.getRequestURL().toString();

            // 获取请求参数进行打印
            Signature signature = proceedingJoinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;

            // 类名
            // swagger中文注释名
            String classCommentName = null;

            if(null != methodSignature && null!=methodSignature.getMethod() && null!= methodSignature.getMethod().getDeclaringClass()
                    && null!= methodSignature.getMethod().getDeclaringClass().getAnnotation(Api.class) &&
                    null != methodSignature.getMethod().getDeclaringClass().getAnnotation(Api.class).tags()
            ){
                classCommentName = methodSignature.getMethod().getDeclaringClass().getAnnotation(Api.class).tags()[0];
            }


            String[] sourceName = signature.getDeclaringTypeName().split("\\.");
            className = sourceName[sourceName.length - 1] + "[" + classCommentName +"]";

            // 方法名
            // swagger中文注释名
            if(null!=methodSignature.getMethod() && null!=methodSignature.getMethod().getAnnotation(ApiOperation.class)) {
                methodCommentName = methodSignature.getMethod().getAnnotation(ApiOperation.class).value();
            }
            methodName = signature.getName() + "[" + methodCommentName + "]";

            // 参数名数组
            parameterNames = ((MethodSignature) signature).getParameterNames();
            // 构造参数组集合

            for (Object arg : proceedingJoinPoint.getArgs()) {
                // request/response无法使用toJSON
                if (arg instanceof HttpServletRequest) {
                    argList.add("request");
                } else if (arg instanceof HttpServletResponse) {
                    argList.add("response");
                } else {
                    argList.add(JSONUtil.toJsonStr(arg));
                }
            }
        } catch (Throwable e) {
            log.error("doAroundAdvice error, msg:", e);
        }finally {
            log.info("IP:{}, URL:{}, {}, {}, param:{}:{}, resp:{}, cost:{}ms",
                    ipAddr, requestUrl,
                    className, methodName,
                    JSONUtil.toJsonStr(parameterNames), JSONUtil.toJsonStr(argList),
                    JSONUtil.toJsonStr(result), System.currentTimeMillis() - startTime1);
            MDC.remove(Constants.TRACE_ID);
        }


        return result;
    }

    /**
     * 获取目标主机的ip
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;



    }

}
