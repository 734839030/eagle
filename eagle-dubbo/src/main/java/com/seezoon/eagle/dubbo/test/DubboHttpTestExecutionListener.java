package com.seezoon.eagle.dubbo.test;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.seezoon.eagle.dubbo.container.JettyContainer;

/**
 * 使用dubbo 和 http 测试的时候自动启动
 * @author hdf
 *
 */
public class DubboHttpTestExecutionListener implements TestExecutionListener {

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		new JettyContainer().start();
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		// TODO Auto-generated method stub

	}

}
