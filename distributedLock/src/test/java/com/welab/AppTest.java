package com.welab;

import static org.junit.Assert.assertTrue;

import com.welab.zookeeper.DistributedLock;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }


    public static void main(String[] args) {
        DistributedLock lock = new DistributedLock("127.0.0.1:2181", "test1");
        lock.unlock();
    }
}
