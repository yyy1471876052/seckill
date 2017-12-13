package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecuition;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Yangyy on 2017/12/10.
 */
@Controller
@RequestMapping("/seckill")//url:模块/资源/{id}/细分 /seckill/list
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        //list.jsp + model = ModelAndView
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:seckill/list";
        }
        model.addAttribute("seckill", seckill);

        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> /*TODO*/ exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try{
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);

        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecuition> execute(@PathVariable("seckillId") Long seckillId,
                                                    @PathVariable("md5") String md5,
                                                    @CookieValue(value = "killPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecuition>(false, "未注册");
        }
        SeckillResult<SeckillExecuition> result;
        try {
            SeckillExecuition seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecuition>(true, seckillExecution);
        } catch (RepeatKillException e) {
            SeckillExecuition seckillExecuition = new SeckillExecuition(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecuition>(true, seckillExecuition);
        } catch (SeckillCloseException e) {
            SeckillExecuition seckillExecuition = new SeckillExecuition(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecuition>(true, seckillExecuition);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecuition seckillExecuition = new SeckillExecuition(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecuition>(true, seckillExecuition);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult(true,now.getTime());
    }
}
