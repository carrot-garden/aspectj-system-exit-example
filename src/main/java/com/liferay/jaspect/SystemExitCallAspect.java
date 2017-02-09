package com.liferay.jaspect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Cristina González
 */
@Aspect
public class SystemExitCallAspect {

	@Pointcut("call(* java.lang.System.exit(..)) && args(status)")
	public void systemExitCall(int status){}

	@Around("systemExitCall(status)")
	public void around(ProceedingJoinPoint joinPoint, int status) {
		Properties properties = getProperties();

		String fileName = properties.getProperty(FILE_NAME);

		dumptToFile(fileName);

		if (Boolean.valueOf(properties.getProperty(CONTINUE_EXECUTION))) {
			try {
				joinPoint.proceed();
			}
			catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	private Properties getProperties() {
		Class<? extends SystemExitCallAspect> aClass = this.getClass();

		Properties properties = new Properties();

		InputStream resourceAsStream =
			aClass.getResourceAsStream("/META-INF/dump.properties");

		if (resourceAsStream == null) {
			defaultProperties(properties);

			return properties;
		}

		try {
			properties.load(resourceAsStream);
		}
		catch (IOException e) {
			defaultProperties(properties);
		}

		return properties;
	}

	private void defaultProperties(Properties properties) {
		if (properties.get(FILE_NAME) == null) {
			properties.setProperty(FILE_NAME, "build/reports/systemExit.dump");
		}
		if (properties.get(CONTINUE_EXECUTION) == null) {
			properties.setProperty(CONTINUE_EXECUTION, Boolean.TRUE.toString());
		}
	}

	public static final String FILE_NAME = "file.name";
	public static final String CONTINUE_EXECUTION = "continue.execution";

	private void dumptToFile(String fileName) {
		Map<Thread, StackTraceElement[]> stackTraces =
			Thread.getAllStackTraces();

		try {
			File file = new File (fileName);
			file.getParentFile().mkdirs();

			PrintWriter writer = new PrintWriter(fileName, "UTF-8");

			for (Map.Entry<Thread, StackTraceElement[]> threadEntry :
					stackTraces.entrySet()) {

				Thread thread = threadEntry.getKey();

				writer.println(
					"[" +  thread.getId() + "] " + thread.getName() + " " +
						thread.getState());

				StackTraceElement[] stackTraceElements = threadEntry.getValue();

				for (StackTraceElement stackTraceElement : stackTraceElements) {
					writer.println("\tat " + stackTraceElement);
				}
			}

			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
