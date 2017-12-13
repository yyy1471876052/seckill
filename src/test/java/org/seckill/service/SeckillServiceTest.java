package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecuition;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Yangyy on 2017/12/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    /**
     * Closing non transactional SqlSession
     * 方法不包含在事物范围内
     */
    public void testGetSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list = {} " + list);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 100;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill = {} " + seckill);
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id= 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer = {} " + exposer);
        /**
         * exposer = {} Exposer{
         * exposed=true,
         * md5='6184fa93d022b398548b04873f050fb5',
         * seckillId=1000,
         * now=0, start=0, end=0}
         */
    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long id = 1000;
        long phone = 13555321453L;
        String md5 = "6184fa93d022b398548b04873f050fb5";
        try{
            SeckillExecuition seckillExecuition = seckillService.executeSeckill(id, phone, md5);
            logger.info("result = {} " + seckillExecuition);
        }catch (RepeatKillException e){
            logger.error(e.getMessage());
        }catch (SeckillCloseException e){
            logger.error(e.getMessage());
        }
        /**
         * org.seckill.exception.SeckillException: seckill data rewrite
         */
        /**
         * 13:57:14.928 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@76797c67] will not be managed by Spring
         13:57:14.937 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==>  Preparing: UPDATE seckill SET number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0;
         13:57:14.980 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2017-12-10 13:57:14.591(Timestamp), 2017-12-10 13:57:14.591(Timestamp)
         13:57:15.121 [main] DEBUG o.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
         13:57:15.121 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2e412d4]
         13:57:15.123 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Creating a new SqlSession
         13:57:15.123 [main] DEBUG org.mybatis.spring.SqlSessionUtils - SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@266dcdd5] was not registered for synchronization because synchronization is not active
         13:57:15.123 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@731d1285] will not be managed by Spring
         13:57:15.123 [main] DEBUG o.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed(seckill_id,user_phone) VALUES (?,?)
         13:57:15.123 [main] DEBUG o.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 13555321453(Long)
         13:57:15.191 [main] DEBUG o.s.d.S.insertSuccessKilled - <==    Updates: 1
         13:57:15.200 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@266dcdd5]
         13:57:15.201 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Creating a new SqlSession
         13:57:15.201 [main] DEBUG org.mybatis.spring.SqlSessionUtils - SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@ea11d31] was not registered for synchronization because synchronization is not active
         13:57:15.203 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@c59de6f] will not be managed by Spring
         13:57:15.204 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==>  Preparing: SELECT sk.seckill_id, sk.user_phone, sk.create_time, sk.state, s.seckill_id "seckill.seckill_id", s.name "seckill..name", s.number "seckill.number", s.start_time "seckill.start_time", s.end_time "seckill..end_time", s.create_time "seckill.create_time" FROM success_killed sk INNER JOIN seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? and sk.user_phone = ?
         13:57:15.204 [main] DEBUG o.s.d.S.queryByIdWithSeckill - ==> Parameters: 1000(Long), 13555321453(Long)
         13:57:15.221 [main] DEBUG o.s.d.S.queryByIdWithSeckill - <==      Total: 1
         13:57:15.228 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@ea11d31]
         13:57:15.230 [main] INFO  o.seckill.service.SeckillServiceTest - result = {} org.seckill.dto.SeckillExecuition@528c4c14
         */
    }


    @Test
    public void testSeckillLogin() throws Exception {
        long id= 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer = {} " + exposer);
            long phone = 13555321453L;
            String md5 = exposer.getMd5();
            try{
                SeckillExecuition seckillExecuition = seckillService.executeSeckill(id, phone, md5);
                logger.info("result = {} " + seckillExecuition);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            //秒杀未开启
            logger.warn("exposer = {} " + exposer);
        }
    }

}