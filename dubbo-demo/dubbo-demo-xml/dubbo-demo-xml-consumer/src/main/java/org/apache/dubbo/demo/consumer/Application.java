/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.demo.DemoService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
        /**
         * demoService为proxy代理  InvokerInvocationHandler，最终调用结构如下：
         * proxy0#sayHello(String)
         *   —> InvokerInvocationHandler#invoke(Object, Method, Object[])
         *     —> MockClusterInvoker#invoke(Invocation)
         *       —> AbstractClusterInvoker#invoke(Invocation)
         *         —> FailoverClusterInvoker#doInvoke(Invocation, List<Invoker<T>>, LoadBalance)
         *           —> Filter#invoke(Invoker, Invocation)  // 包含多个 Filter 调用
         *             —> ListenerInvokerWrapper#invoke(Invocation)
         *               —> AbstractInvoker#invoke(Invocation)
         *                 —> DubboInvoker#doInvoke(Invocation)
         *                   —> ReferenceCountExchangeClient#request(Object, int)
         *                     —> HeaderExchangeClient#request(Object, int)
         *                       —> HeaderExchangeChannel#request(Object, int)
         *                         —> AbstractPeer#send(Object)
         *                           —> AbstractClient#send(Object, boolean)
         *                             —> NettyChannel#send(Object, boolean)
         *                               —> NioClientSocketChannel#write(Object)
         */
        DemoService demoService = context.getBean("demoService", DemoService.class);
        String hello = demoService.sayHello("world");
        System.out.println("result: " + hello);
    }
}
