package com.seezoon.eagle.dubbo.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * dubbo 和 http 协议测试
 * @author hdf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) // 使用junit4进行测试
@TestExecutionListeners({DubboHttpTestExecutionListener.class})
public class DubboHttpTest {
}
