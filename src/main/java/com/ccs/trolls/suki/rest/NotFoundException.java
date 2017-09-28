package com.ccs.trolls.suki.rest;

/**
 * 针对单笔记录查询时提示未找到。
 *
 * <p>在 Rest API 查询数据时，会存在找到则返回结果数据及 200、未找到则返回 404 的情况；但是在 Spring rest controller 的 API
 * 方法实现中，如果返回结果就是简单的 Business Object、而不想使用复杂的 ResponseEntity<Object>，那么就无法分别返回 200 或 404，这时可以针对未找到抛出
 * NotFoundException 然后 AOP 处理成 404，参见 ApiControllerExceptionHandler 及 AccountController 等程序。 注意 404
 * 仅针对单笔数据查询，多笔数据查询即使为空也应该返回 200 及空数组。
 */
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }
}
