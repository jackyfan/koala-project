package org.openkoala.organisation.application.impl;

import org.dayatang.querychannel.Page;
import org.openkoala.organisation.application.EmployeeApplication;
import org.openkoala.organisation.application.dto.EmployeeDTO;
import org.openkoala.organisation.core.EmployeeMustHaveAtLeastOnePostException;
import org.openkoala.organisation.core.domain.Employee;
import org.openkoala.organisation.core.domain.EmployeePostHolding;
import org.openkoala.organisation.core.domain.Organization;
import org.openkoala.organisation.core.domain.Post;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.*;

/**
 * 员工应用实现层类
 * @author xmfang
 */
@Named
@Transactional(value = "transactionManager_org")
public class EmployeeApplicationImpl implements EmployeeApplication {

    @Override
    public Organization getOrganizationOfEmployee(Employee employee, Date date) {
        return employee.getOrganization(date);
    }

    @Override
    public void createEmployeeWithPost(Employee employee, Post post) {
        employee.saveWithPost(post);
    }

    @Override
    public void transformPost(Employee employee, Map<Post, Boolean> responsiblePosts) {
        if (responsiblePosts == null || responsiblePosts.isEmpty()) {
            throw new EmployeeMustHaveAtLeastOnePostException();
        }

        List<EmployeePostHolding> existHoldings = EmployeePostHolding.getByEmployee(employee, new Date());
        Set<Post> postsForOutgoing = employee.getPosts(new Date());
        Map<Post, Boolean> postsForAssign = new HashMap<Post, Boolean>();

        boolean existAccountsability = false;
        for (Post post : responsiblePosts.keySet()) {
            for (EmployeePostHolding ejHolding : existHoldings) {
                if (ejHolding.getCommissioner().equals(post)) {
                    postsForOutgoing.remove(post);
                    ejHolding.setPrincipal(responsiblePosts.get(post));
                    ejHolding.save();
                    existAccountsability = true;
                    break;
                }
            }
            if (existAccountsability) {
                existAccountsability = false;
                continue;
            }
            postsForAssign.put(post, responsiblePosts.get(post));
        }

        employee.outgoingPosts(postsForOutgoing);

        for (Post post : postsForAssign.keySet()) {
            employee.assignPost(post, postsForAssign.get(post));
        }
    }

    @Override
    public Map<Post, Boolean> getPostsByEmployee(Employee employee) {
        Map<Post, Boolean> results = new HashMap<Post, Boolean>();
        for (EmployeePostHolding holding : EmployeePostHolding.getByEmployee(employee, new Date())) {
            results.put(holding.getCommissioner(), holding.isPrincipal());
        }
        return results;
    }

    /**
     * 这个是错误的时候添加的方法
     * @param example
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public Page<EmployeeDTO> pagingQueryEmployees(EmployeeDTO example, int page, int pagesize) {
        return null;
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Page<EmployeeDTO> pagingQueryEmployeesWhoNoPost(EmployeeDTO example, int page, int pagesize) {
        return null;
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Page<EmployeeDTO> pagingQueryEmployeesByOrganizationAndChildren(EmployeeDTO example, org.openkoala.organisation.domain.Organization organization, int page, int pagesize) {
        return null;
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Page<EmployeeDTO> pagingQueryEmployeesByOrganization(EmployeeDTO example, org.openkoala.organisation.domain.Organization organization, int page, int pagesize) {
        return null;
    }

    /**
     * 这个是错误的时候添加的方法
     */
    @Override
    public Object getEmployeeById(Long id) {
        return null;
    }

}
