package com.ccs.trolls.suki.rest;

import java.util.UUID;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixRuntimeException.FailureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ccs.trolls.suki.api.model.Response;

@RestController
//@ControllerAdvice(annotations=RestController.class) //该限制的目的是为了仅供Rest API使用、正常的Web Page Controller另外处理，但是却捕获不到Controller之前发生的Exception， 比如RequestMappingInfoHandlerMapping的HttpMediaTypeNotSupportedException。TODO
@ControllerAdvice
public class ApiControllerExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(ApiControllerExceptionHandler.class);

  @Autowired Tracer tracer;

  /*
  @ResponseStatus(value=HttpStatus.BAD_GATEWAY)
  @ExceptionHandler(MpException.class)
  @ResponseBody
  public ApiStatus handleException(MpException ex) {
      ApiStatus status = new ApiStatus(null, ex.getMessage());
      return status;
  }

  由于Hystrix又包裹了一层Exception，所以不能直接使用以上简洁方式， 否则ResponseStatus无法根据不同种类的Exception做调整，改为以下做法。
  */
  @ExceptionHandler(Exception.class) // Or Throwable.class?
  public ResponseEntity<Response> handleException(Exception ex) {
    Throwable cause;
    boolean isShortCircuit = false;
    if (ex instanceof HystrixRuntimeException) {
      logger.debug(
          "HystrixRuntimeException catched!"); //TODO：在application.java里@EnableAspectJAutoProxy(proxyTargetClass=true)后，仍然会Catch到HystrixRuntimeException？
      isShortCircuit =
          (FailureType.SHORTCIRCUIT == ((HystrixRuntimeException) ex).getFailureType());
      cause = ex.getCause();
    } else {
      logger.debug("HystrixRuntimeException NOT catched!");
      cause = ex;
    }

    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    Span span = tracer.getCurrentSpan();
    String traceId = (span == null ? null : Span.idToHex(span.getTraceId()));
    if (traceId == null || traceId.isEmpty())
      traceId = "[NoClientTraceId]" + UUID.randomUUID().toString();

    String msg;
    boolean errLog = false;
    if (cause instanceof NotFoundException) {
      msg = cause.getMessage();
      httpStatus = HttpStatus.NOT_FOUND;
    } else if (cause instanceof BadRequestException
        || cause instanceof org.springframework.web.bind.MissingServletRequestParameterException) {
      msg = "入参有误：" + cause.getMessage();
      httpStatus = HttpStatus.BAD_REQUEST;
    } else if (cause instanceof org.springframework.web.HttpMediaTypeNotSupportedException) {
      msg = "服务请求仅接受\"application/json;charset=utf-8\": " + cause.getMessage();
      httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    } else if (cause
        instanceof org.springframework.http.converter.HttpMessageNotReadableException) {
      msg = "服务请求仅接受\"application/json;charset=utf-8\": " + cause.getMessage();
      httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
    } else if (cause
        instanceof org.springframework.web.bind.UnsatisfiedServletRequestParameterException) {
      msg = "调用方式错误：" + cause.getMessage();
      httpStatus = HttpStatus.BAD_REQUEST;
    } else {
      //TODO：在此不应该笼统的返回 HttpStatus.INTERNAL_SERVER_ERROR，Spring 本身已做一定处理，参见 [Handling Standard Spring MVC Exceptions|http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions]。
      logger.error(traceId, ex);
      errLog = true;
      String reason = cause.getMessage();
      if (isShortCircuit) {
        msg = "系统连续出错、暂不调用直接返回";
      } else if (cause instanceof org.springframework.web.client.ResourceAccessException
          && reason.contains("I/O error")
          && reason.contains("api.weixin.qq.com")) {
        msg = "网络或Internet访问错误";
      } else {
        msg = "系统错误";
      }
    }
    if (!errLog) logger.debug(traceId + " " + msg);
    Response resp = new Response();
    resp.setTraceId(traceId);
    resp.setMessage(msg);
    return new ResponseEntity<Response>(resp, httpStatus);
    /*
             TODO：启用 Web 安全（非 OAuth2）后如果没有将 security.basic.enabled 改为 false，那么出现401后 ApiStatus 并没有被返回给用户，原因从以下日志看很直观（Failed to invoke @ExceptionHandler method:......），但怎么解决待定：

    2016-03-14 20:56:58.877 DEBUG [mermaid,46cd683695df9251,46cd683695df9251,false] 7960 --- [http-nio-8080-exec-5] .t.w.m.w.a.ApiControllerExceptionHandler : HystrixRuntimeException NOT catched!
    2016-03-14 20:56:58.927 ERROR [mermaid,46cd683695df9251,46cd683695df9251,false] 7960 --- [http-nio-8080-exec-5] .t.w.m.w.a.ApiControllerExceptionHandler : [NoClientTraceId]a3403965-06b4-4963-9953-df25f0d3246f

    org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at org.apache.catalina.connector.OutputBuffer.realWriteBytes(OutputBuffer.java:393) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        at org.apache.tomcat.util.buf.ByteChunk.flushBuffer(ByteChunk.java:426) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        ......
    Caused by: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at sun.nio.ch.SocketDispatcher.write0(Native Method) ~[na:1.8.0_74]
        at sun.nio.ch.SocketDispatcher.write(SocketDispatcher.java:51) ~[na:1.8.0_74]
        ......
        ... 42 common frames omitted

    2016-03-14 20:56:58.959 ERROR [mermaid,46cd683695df9251,46cd683695df9251,false] 7960 --- [http-nio-8080-exec-5] .m.m.a.ExceptionHandlerExceptionResolver : Failed to invoke @ExceptionHandler method: public org.springframework.http.ResponseEntity

    org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at org.apache.catalina.connector.OutputBuffer.realWriteBytes(OutputBuffer.java:393) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        at org.apache.tomcat.util.buf.ByteChunk.flushBuffer(ByteChunk.java:426) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        ......
    Caused by: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at sun.nio.ch.SocketDispatcher.write0(Native Method) ~[na:1.8.0_74]
        at sun.nio.ch.SocketDispatcher.write(SocketDispatcher.java:51) ~[na:1.8.0_74]
        ......
        ... 44 common frames omitted

    2016-03-14 20:56:58.983 ERROR [mermaid,46cd683695df9251,46cd683695df9251,false] 7960 --- [http-nio-8080-exec-5] o.a.c.c.C.[Tomcat].[localhost]           : Exception Processing ErrorPage[errorCode=0, location=/error]

    org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at org.apache.catalina.connector.OutputBuffer.realWriteBytes(OutputBuffer.java:393) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        at org.apache.tomcat.util.buf.ByteChunk.flushBuffer(ByteChunk.java:426) ~[tomcat-embed-core-8.0.32.jar:8.0.32]
        ......
    Caused by: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
        at sun.nio.ch.SocketDispatcher.write0(Native Method) ~[na:1.8.0_74]
        at sun.nio.ch.SocketDispatcher.write(SocketDispatcher.java:51) ~[na:1.8.0_74]
        ......
        ... 42 common frames omitted

             */
  }
}
