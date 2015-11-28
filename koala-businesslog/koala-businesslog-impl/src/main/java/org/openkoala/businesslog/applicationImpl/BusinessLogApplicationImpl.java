package org.openkoala.businesslog.applicationImpl;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.querychannel.QueryChannelService;
import org.dayatang.utils.Page;
import org.openkoala.businesslog.BusinessLogBaseException;
import org.openkoala.businesslog.application.BusinessLogApplication;
import org.openkoala.businesslog.model.DefaultBusinessLog;
import org.openkoala.businesslog.model.DefaultBusinessLogDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: zjzhai
 * Date: 12/11/13
 * Time: 3:12 PM
 */
@Named
@Transactional
//@Interceptors(value = org.openkoala.koala.util.SpringEJBIntercepter.class)
//@Stateless(name = "BusinessLogApplication")
//@Remote
public class BusinessLogApplicationImpl implements BusinessLogApplication {


    private QueryChannelService queryChannel;

    private QueryChannelService getQueryChannelService() {
        if (queryChannel == null) {
            queryChannel = InstanceFactory.getInstance(QueryChannelService.class, "queryChannel_businessLog");
        }
        return queryChannel;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public DefaultBusinessLogDTO getDefaultBusinessLog(Long id) {
        DefaultBusinessLog defaultBusinessLog = DefaultBusinessLog.load(DefaultBusinessLog.class, id);
        DefaultBusinessLogDTO defaultBusinessLogDTO = new DefaultBusinessLogDTO();
        // 将domain转成VO
        try {
            BeanUtils.copyProperties(defaultBusinessLogDTO, defaultBusinessLog);
        } catch (InvocationTargetException e) {
            throw new BusinessLogBaseException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessLogBaseException(e);
        }
        defaultBusinessLogDTO.setId(defaultBusinessLog.getId());
        return defaultBusinessLogDTO;
    }

    public DefaultBusinessLogDTO saveDefaultBusinessLog(DefaultBusinessLogDTO defaultBusinessLogDTO) {
        DefaultBusinessLog defaultBusinessLog = new DefaultBusinessLog();
        try {
            BeanUtils.copyProperties(defaultBusinessLog, defaultBusinessLogDTO);
        } catch (InvocationTargetException e) {
            throw new BusinessLogBaseException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessLogBaseException(e);
        }
        defaultBusinessLog.save();
        defaultBusinessLogDTO.setId((Long) defaultBusinessLog.getId());
        return defaultBusinessLogDTO;
    }

    public void updateDefaultBusinessLog(DefaultBusinessLogDTO defaultBusinessLogDTO) {
        DefaultBusinessLog defaultBusinessLog = DefaultBusinessLog.get(DefaultBusinessLog.class, defaultBusinessLogDTO.getId());
        // 设置要更新的值
        try {
            BeanUtils.copyProperties(defaultBusinessLog, defaultBusinessLogDTO);
        } catch (InvocationTargetException e) {
            throw new BusinessLogBaseException(e);
        } catch (IllegalAccessException e) {
            throw new BusinessLogBaseException(e);
        }
    }

    public void removeDefaultBusinessLog(Long id) {
        this.removeDefaultBusinessLogs(new Long[]{id});
    }

    @Override
    public void save(DefaultBusinessLog log) {
        log.save();
    }

    public void removeDefaultBusinessLogs(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            DefaultBusinessLog defaultBusinessLog = DefaultBusinessLog.load(DefaultBusinessLog.class, ids[i]);
            defaultBusinessLog.remove();
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DefaultBusinessLogDTO> findAllDefaultBusinessLog() {
        List<DefaultBusinessLogDTO> list = new ArrayList<DefaultBusinessLogDTO>();
        List<DefaultBusinessLog> all = DefaultBusinessLog.findAll(DefaultBusinessLog.class);
        for (DefaultBusinessLog defaultBusinessLog : all) {
            DefaultBusinessLogDTO defaultBusinessLogDTO = new DefaultBusinessLogDTO();
            // 将domain转成VO
            try {
                BeanUtils.copyProperties(defaultBusinessLogDTO, defaultBusinessLog);
            } catch (InvocationTargetException e) {
                throw new BusinessLogBaseException(e);
            } catch (IllegalAccessException e) {
                throw new BusinessLogBaseException(e);
            }
            list.add(defaultBusinessLogDTO);
        }
        return list;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Page<DefaultBusinessLogDTO> pageQueryDefaultBusinessLog(DefaultBusinessLogDTO queryVo, int currentPage, int pageSize) {
        List<DefaultBusinessLogDTO> result = new ArrayList<DefaultBusinessLogDTO>();
        List<Object> conditionVals = new ArrayList<Object>();
        StringBuilder jpql = new StringBuilder("select _defaultBusinessLog from DefaultBusinessLog _defaultBusinessLog   where 1=1 ");

        if (queryVo.getUser() != null && !"".equals(queryVo.getUser())) {
            jpql.append(" and _defaultBusinessLog.user like ?");
            conditionVals.add(MessageFormat.format("%{0}%", queryVo.getUser()));
        }

        if (queryVo.getIp() != null && !"".equals(queryVo.getIp())) {
            jpql.append(" and _defaultBusinessLog.ip like ?");
            conditionVals.add(MessageFormat.format("%{0}%", queryVo.getIp()));
        }

        if (queryVo.getTime() != null) {
            jpql.append(" and _defaultBusinessLog.time between ? and ? ");
            conditionVals.add(queryVo.getTime());
            conditionVals.add(queryVo.getTimeEnd());
        }


        if (queryVo.getCategory() != null && !"".equals(queryVo.getCategory())) {
            jpql.append(" and _defaultBusinessLog.category like ?");
            conditionVals.add(MessageFormat.format("%{0}%", queryVo.getCategory()));
        }

        if (queryVo.getLog() != null && !"".equals(queryVo.getLog())) {
            jpql.append(" and _defaultBusinessLog.log like ?");
            conditionVals.add(MessageFormat.format("%{0}%", queryVo.getLog()));
        }
        Page<DefaultBusinessLog> pages = getQueryChannelService().createJpqlQuery(jpql.toString()).setParameters(conditionVals).setPage(currentPage, pageSize).pagedList();
         for (DefaultBusinessLog defaultBusinessLog : pages.getData()) {
            DefaultBusinessLogDTO defaultBusinessLogDTO = new DefaultBusinessLogDTO();

            // 将domain转成VO
            try {
                BeanUtils.copyProperties(defaultBusinessLogDTO, defaultBusinessLog);
            } catch (InvocationTargetException e) {
                throw new BusinessLogBaseException(e);
            } catch (IllegalAccessException e) {
                throw new BusinessLogBaseException(e);
            }

            result.add(defaultBusinessLogDTO);
        }
        return new Page<DefaultBusinessLogDTO>(pages.getStart(), pages.getResultCount(), pages.getPageSize(), result);
    }

}
