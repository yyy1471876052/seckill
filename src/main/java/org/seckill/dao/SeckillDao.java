package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by Yangyy on 2017/12/9.
 */
public interface SeckillDao {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新语句更新的行数
     */
    public int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId
     * @return
     */
    public Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品
     * @param offset
     * @param limit
     * @return
     */
    public List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

}
