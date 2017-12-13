package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Yangyy on 2017/12/9.
 */

/**
 * 配置spring和junit的整合
 * Created by Yangyy on 2017/12/9.
 * 配置spring和junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring 配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long id = 1001L;
        long phone = 13502181181L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount = " + insertCount);

    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long id = 1001L;
        long phone = 13502181181L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);

        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
        /*
        SuccessKilled{
        seckillId=1000,
        userPhone=13502181181,
        state=-1,
        createTime=Sat Dec 09 18:36:01 CST 2017}

        Seckill{
        seckillId=1000,
        name='null',
        number=100,
        startTime=Sun Nov 01 00:00:00 CST 2015,
        endTime=null,
        createTime=Sat Dec 09 12:55:23 CST 2017}

        */


    }
}