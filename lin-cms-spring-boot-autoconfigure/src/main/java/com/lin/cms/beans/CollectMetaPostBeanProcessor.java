package com.lin.cms.beans;

import com.lin.cms.core.annotation.RouteMeta;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

public class CollectMetaPostBeanProcessor implements BeanPostProcessor {

    private Map<String, RouteMeta> metaMap = new HashMap<>();

    private Map<String, Map<String, Set<String>>> structuralMeta = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            RouteMeta meta = AnnotationUtils.findAnnotation(method, RouteMeta.class);
            if (meta != null) {
                if (meta.mount()) {
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();
                    String identity = className + "#" + methodName;
                    metaMap.put(identity, meta);
                    this.putMetaIntoStructuralMeta(identity, meta);
                }
            }
        }
        return bean;
    }

    private void putMetaIntoStructuralMeta(String identity, RouteMeta meta) {
        String module = meta.module();
        String auth = meta.auth();
        // 如果已经存在了该 module，直接向里面增加
        if (structuralMeta.containsKey(module)) {
            Map<String, Set<String>> moduleMap = structuralMeta.get(module);
            // 如果 auth 已经存在
            this.putIntoModuleMap(moduleMap, identity, auth);
        } else {
            // 不存在 该 module，创建该 module
            Map<String, Set<String>> moduleMap = new HashMap<>();
            // 如果 auth 已经存在
            this.putIntoModuleMap(moduleMap, identity, auth);
            structuralMeta.put(module, moduleMap);
        }
    }

    private void putIntoModuleMap(Map<String, Set<String>> moduleMap, String identity, String auth) {
        if (moduleMap.containsKey(auth)) {
            moduleMap.get(auth).add(identity);
        } else {
            Set<String> eps = new HashSet<>();
            eps.add(identity);
            moduleMap.put(auth, eps);
        }
    }

    public Map<String, RouteMeta> getMetaMap() {
        return metaMap;
    }

    public RouteMeta findMeta(String key) {
        return metaMap.get(key);
    }

    public RouteMeta findMetaByAuth(String auth) {
        Collection<RouteMeta> values = metaMap.values();
        RouteMeta[] objects = values.toArray(new RouteMeta[0]);
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].auth().equals(auth)) {
                return objects[i];
            }
        }
        return null;
    }

    public void setMetaMap(Map<String, RouteMeta> metaMap) {
        this.metaMap = metaMap;
    }

    public Map<String, Map<String, Set<String>>> getStructuralMeta() {
        return structuralMeta;
    }

    public void setStructrualMeta(Map<String, Map<String, Set<String>>> structuralMeta) {
        this.structuralMeta = structuralMeta;
    }
}
