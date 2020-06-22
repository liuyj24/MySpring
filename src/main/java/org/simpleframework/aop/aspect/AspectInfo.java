package org.simpleframework.aop.aspect;

import lombok.Getter;

@Getter
public class AspectInfo {
    private int orderIndex;
    private DefaultAspect aspectObject;

    public AspectInfo(int orderIndex, DefaultAspect aspectObject) {
        this.orderIndex = orderIndex;
        this.aspectObject = aspectObject;
    }
}
