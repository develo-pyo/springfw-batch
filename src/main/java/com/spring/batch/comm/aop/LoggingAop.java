package com.spring.batch.comm.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class LoggingAop {
	
	/** The Constant logger. */
    private Logger logger = LoggerFactory.getLogger(this.getClass());
	
    /**
	 * Around advice.
	 * 
	 * @param proceedingJoinPoint the proceeding join point
	 * 
	 * @return the object
	 * 
	 * @throws Throwable the throwable
	 */
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
	    String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();
		Object[] params = proceedingJoinPoint.getArgs();
		
		String values = "";
		StringBuffer sb = new StringBuffer();
		if(params!=null) 
		{
			for(int i=0;i<params.length;i++) 
			{
				sb.append(params[i]);
				sb.append(",");
				// values += params[i] + ",";
			}
			values = sb.toString();
		}
		
		if(values!=null&&values.length()>0) {
			values = values.substring(0,values.length()-1);
		}
		
		StopWatch watch = new StopWatch();
		if (logger.isDebugEnabled()) {
			logger.debug("#IN #[" + className + "." + methodName + "] [" + values + "]");
		}
		
		Object object = null;
		try {
			watch.start();
			object = proceedingJoinPoint.proceed();
		}
		
		catch(Exception ex) {
			logger.error("Exception Method Call", ex);
			throw ex;
//			throw new CustomException("9999", "오류", "EgovFileScrty", ex.toString());
			// throw ex;
		}
		finally {
			watch.stop();
			if (logger.isDebugEnabled()) {
				logger.debug("#OUT#[" + className + "." + methodName + "] [" + object + "] " + "("+ watch.getTotalTimeMillis()+"ms)");
			}
		}

		return object;
	}

}
