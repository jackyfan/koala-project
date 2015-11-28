package org.openkoala.organisation.application.impl;

import org.openkoala.organisation.application.OrganizationApplication;
import org.openkoala.organisation.core.domain.*;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 组织机构应用实现层类
 * 
 * @author xmfang
 * 
 */
@Named
@Transactional(value = "transactionManager_org")
public class OrganizationApplicationImpl implements OrganizationApplication {

	public boolean isTopOrganizationExists() {
		return Organization.getTopOrganization() != null;
	}

	@Override
	public void createAsTopOrganization(Company company) {
		company.createAsTopOrganization();
	}

	@Override
	public Company createCompany(Company parent, Company company) {
		company.createUnder(parent);
		return company;
	}

	@Override
	public void assignChildOrganization(Organization parent, Organization child, Date date) {
        new OrganizationLineManagement(parent, child, date).save();
	}

	public Organization getParentOfOrganization(Organization organization, Date date) {
		return OrganizationLineManagement.getParentOfOrganization(organization, date);
	}

	public List<Organization> findChildrenOfOrganization(Organization organization, Date date) {
		return OrganizationLineManagement.findChildrenOfOrganization(organization, date);
	}

	@Override
	public Department createDepartment(Organization parent, Department department) {
		department.createUnder(parent);
		return department;
	}

	@Override
	public void terminateEmployeeOrganizationRelation(Organization organization, Set<Employee> employees) {
		EmployeePostHolding.terminateEmployeeOrganizationRelation(organization, employees);
	}

	@Override
	public Organization getOrganizationById(Long id) {
		return OrganizationAbstractEntity.get(Organization.class, id);
	}

	@Override
	public void updateOrganization(Organization organization) {
		organization.update();
	}

	/**
	 * 这个是错误的时候添加的方法
	 */
	@Override
	public Object getOrganizationTree() {
		return null;
	}

}
