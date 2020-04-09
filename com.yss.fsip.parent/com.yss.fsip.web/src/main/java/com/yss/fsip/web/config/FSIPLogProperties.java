package com.yss.fsip.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * fsip 是否打印日志配置
 *
 * @author gumpliu
 * @create 2019-07-07 21:45
 */
@ConfigurationProperties("fsip.web.log")
public class FSIPLogProperties {

  //是否打印请求参数
  private boolean request = true;

  //打印请求参数最大长度，默认长度为500
  private int requestMax = 500;

  //是否打印返回数据
  private boolean response = false;

  //打印返回数据最大长度，默认长度为500
  private int responseMax = 500;

  public boolean isRequest() {
    return request;
  }

  public void setRequest(boolean request) {
    this.request = request;
  }

  public boolean isResponse() {
    return response;
  }

  public void setResponse(boolean response) {
    this.response = response;
  }

  public int getRequestMax() {
    return requestMax;
  }

  public void setRequestMax(int requestMax) {
    this.requestMax = requestMax;
  }

  public int getResponseMax() {
    return responseMax;
  }

  public void setResponseMax(int responseMax) {
    this.responseMax = responseMax;
  }
}
