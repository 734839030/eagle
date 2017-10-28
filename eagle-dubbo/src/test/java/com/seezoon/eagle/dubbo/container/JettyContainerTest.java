package com.seezoon.eagle.dubbo.container;

import java.io.IOException;

import org.junit.Test;

public class JettyContainerTest {

	@Test
	public void testStart() throws IOException {
		new JettyContainer().start();
		System.in.read();
	}

}
