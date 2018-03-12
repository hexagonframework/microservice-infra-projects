package io.github.hexagonframework.microservice.infra.uaa.domain.exception;

/**
 * @author Xuegui Yuan
 */
public class SequenceException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private String errCode;
  private String errMsg;

  public SequenceException(String errMsg) {
    super(errMsg);
    this.errMsg = errMsg;
  }

  public String getErrCode() {
    return errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
}
