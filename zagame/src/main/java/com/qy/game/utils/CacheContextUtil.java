package com.qy.game.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * Context 宸ュ叿绫�
 */
@SuppressWarnings("static-access")
@Component
public class CacheContextUtil implements ApplicationContextAware {
    private static ApplicationContext commonApplicationContext;


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.commonApplicationContext = context;
    }


    /**
     * 鏍规嵁鎻愪緵鐨刡ean鍚嶇О寰楀埌鐩稿簲鐨勬湇鍔＄被
     *
     * @param beanId bean鐨刬d
     * @return 杩斿洖bean鐨勫疄渚嬪璞�
     */
    public static Object getBean(String beanId) {
        return commonApplicationContext.getBean(beanId);
    }


    /**
     * 鏍规嵁鎻愪緵鐨刡ean鍚嶇О寰楀埌瀵瑰簲浜庢寚瀹氱被鍨嬬殑鏈嶅姟绫�
     *
     * @param beanId bean鐨刬d
     * @param clazz  bean鐨勭被绫诲瀷
     * @return 杩斿洖鐨刡ean绫诲瀷, 鑻ョ被鍨嬩笉鍖归厤, 灏嗘姏鍑哄紓甯�
     */
    public static <T> T getBean(String beanId, Class<T> clazz) {
        return commonApplicationContext.getBean(beanId, clazz);
    }
}