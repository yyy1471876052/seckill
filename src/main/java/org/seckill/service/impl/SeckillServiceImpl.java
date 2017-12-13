package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecuition;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.util.Date;
import java.util.List;

/**
 * Created by Yangyy on 2017/12/10.
 */
//@Component @Service @Dao @ Controller
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入service依赖
    @Autowired //@Resource @Inject
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;
    //md5盐值字符串，用于混淆md5
    private final String slat = "ashdy182e3ajsdn2893y4jjashd82qy91he?><xasd'las1d21;2l1lasd";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Transactional
    /**
     * 使用注解控制事方法的优点
     * 1.开发团队达成一致约定，明确标注事物方法的变成风格。
     * 2.保证事物方法的执行时间尽可能短，不要穿插其他的网络操作rpc/http请求或者剥离到事物方法外部。
     * 3.不是所有的方发都需要事物，如只有一条修改操作，只读操作不需要事物控制。
     */
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null){
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统时间
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId,startTime.getTime(),nowTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);

        return new Exposer(true, md5, seckillId);
    }

    /**
     * 私有方法生成MD5
     * @param seckillId
     * @return
     */
    @Transactional
    private String getMD5(long seckillId){
        String base = seckillId + "/" +slat;
        //spring工具类生成md5
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SeckillExecuition executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();

        try {
            //减库存
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                //没有跟新记录，秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecuition(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            //所有编译器异常 转化为运行期异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }
}
