package org.simpleframework.mvc.render;

import org.simpleframework.mvc.RequestProcessorChain;

/**
 * 默认渲染器
 */
public class DefaultResultRender implements ResultRender {
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        //设置响应状态码，默认是200
        requestProcessorChain.getResponse().setStatus(requestProcessorChain.getResponseCode());

    }
}
