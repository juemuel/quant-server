package com.juemuel.trend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * Unit test for simple App.
 */
public class IndexCodesApplicationTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IndexCodesApplicationTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( IndexCodesApplicationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    @Autowired
    private CacheManager cacheManager;

    public void testCacheManagerType() {
        assertTrue(cacheManager instanceof RedisCacheManager);
        System.out.println("当前使用的缓存管理器：" + cacheManager.getClass().getName());
    }

}
