/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.jaspect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class SystemExitCallAspectTest {

	@Test
	public void test() throws IOException {
		System.exit(0);

		Path path = Paths.get("build/reports/systemExit.dump");

		Assert.assertTrue("Dump has not being generated", Files.exists(path));

		Stream<String> lines = Files.lines(path);

		List<String> collect = lines.filter(t -> t.contains("com.liferay.jaspect.SystemExitCallAspect.around(SystemExitCallAspect.java:31)")).collect(Collectors.toList());

		Assert.assertEquals("The Aspect has not been called", 1, collect.size());
	}

}
