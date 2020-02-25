package com.spring.batch.comm.exception;

public class CustomException extends Exception 
{
    private static final long serialVersionUID = 1L;

    private String code = null;
    private String msg = null;
    private String svcName = null;
    
    private String fullMsg = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSvcName() {
        return svcName;
    }

    public void setSvcName(String svcName) {
        this.svcName = svcName;
    }

    public CustomException() {
        super();
    }

    public CustomException(String code, String message) {
    	this(code, message, null, null);
    }

    public CustomException(String code, String message, String svcName) {
    	this(code, message, svcName, null);
    }

    public CustomException(String code, String message, String svcName ,String fullMsg) {
        super(getErrorMsg(svcName, code, message));

        this.setCode(code);
        this.setMsg(message);
        this.setSvcName(svcName);
        this.setFullMsg(fullMsg);
    }

    public String getErrorMsg() {
        return getErrorMsg(svcName, getCode(), getMsg());
    }

    /**
     * Make Exception Message String.
     *
     * @param svcName
     * @param code
     * @param msg
     * @return
     * @auth xeni
     * @date 2007. 06. 15
     */
    public static String getErrorMsg(String svcName, String code, String msg) {
        String retVal = "";
        if ( svcName != null )
            retVal = "CustomException : " + svcName + " 서비스에서 " + code + " 발생 ( " + msg + " )";
        else
            retVal = "CustomException : " + code + " 발생 ( " + msg + " )";
        return retVal;
    }

    public String getFullMsg() {
        return fullMsg;
    }

    public void setFullMsg(String fullMsg) {
        this.fullMsg = fullMsg;
    }
}