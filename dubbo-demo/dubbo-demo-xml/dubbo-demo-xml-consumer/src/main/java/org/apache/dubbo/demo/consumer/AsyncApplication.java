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

import org.apache.dubbo.demo.AsyncDemoService;
import org.apache.dubbo.demo.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CompletableFuture;

public class AsyncApplication {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
        //demoService为proxy代理  InvokerInvocationHandler
        AsyncDemoService asyncService = context.getBean("asyncDemoService", AsyncDemoService.class);
        // 调用直接返回CompletableFuture
        CompletableFuture<String> future = asyncService.sayHello("async call request");
//        // 增加回调，后面的打印语句会先执行
//        future.whenComplete((v, t) -> {
//            if (t != null) {
//                t.printStackTrace();
//            } else {
//                System.out.println("Response: " + v);
//            }
//        });
//        System.out.println("Executed before response return.");

        try{
            /**
             * 1、会阻塞等待获取结果，然后才向下执行，阻塞的是当前的主线程
             * 2、但是获取结果，还是通过netty的EventLoop来监听可读事件，然后设置到Future中，
             *    最终通过DefaultFuture.doReceived来获取结果
             */

            String result = future.get();
            System.out.println("result："+result);
            System.out.println("Executed after response return.");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
