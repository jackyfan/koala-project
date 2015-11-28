package org.openkoala.organisation.domain;

import org.dayatang.domain.EntityRepository;
import org.dayatang.domain.InstanceFactory;
import org.dayatang.utils.DateUtils;
import org.openkoala.organisation.SnIsExistException;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
public abstract class Party extends OrganizationAbstractEntity {

	private static final long serialVersionUID = -6083088250263550905L;

	// 编码
	
	private String sn;

	// 名称
	
	private String name;
	
	// 创建日期
	
	private Date createDate = new Date();

	// 终结日期
	
	private Date terminateDate = DateUtils.MAX_DATE;

	Party() {
	}

	public Party(String name) {
		this.name = name.trim();
	}

	public Party(String name, String sn) {
		this.name = name.trim();
		this.sn = sn.trim();
	}

	@Column(name = "sn", length = 50)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn.trim();
	}

	@Column(name = "name", length = 30, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "terminate_date")
	public Date getTerminateDate() {
		return terminateDate;
	}

	public void setTerminateDate(Date terminateDate) {
		this.terminateDate = terminateDate;
	}

	public static <T extends Party> List<T> findAll(Class<T> clazz, Date date) {
		return getRepository().createCriteriaQuery(clazz)
				.le("createDate", date)
				.gt("terminateDate", date).list();
	}
	
	@Override
	public void save() {
		if (getId() == null) {
			checkSnExist();
		} else {
			Party party = get(Party.class, getId());
			if (!party.getSn().equals(sn)) {
				checkSnExist();
			}
		}
		super.save();
	}
	
	private void checkSnExist() {
		if (isExistSn(getClass(), sn, new Date())) {
			throw new SnIsExistException();
		}
	}
	
	public static <P extends Party> boolean isExistSn(Class<P> clazz, String sn, Date date) {
		List<P> parties = getRepository().createCriteriaQuery(clazz)
				.eq("sn", sn)
				.le("createDate", date)
				.gt("terminateDate", date).list();
		return parties.isEmpty() ? false : true;
	}
	
	private static EntityRepository repository;
	
	public static EntityRepository getRepository() {
		if (repository == null) {
			repository = InstanceFactory.getInstance(EntityRepository.class,"repository_org");
		}
		return repository;
	}
	
	/**
	 * 判断在指定的日期是否“存活”，即参数date处于其生命周期内。
	 * 
	 * @param date
	 * @return
	 */
	public boolean isActive(Date date) {
		return DateUtils.isSameDay(date, getCreateDate())
				|| date.after(getCreateDate())
				&& date.before(getTerminateDate());
	}

	/**
	 * 终结当事人，例如撤销机构、撤销岗位、员工离职退休等
	 * 
	 * @param date
	 */
	@SuppressWarnings("rawtypes")
	public void terminate(Date date) {
		for (Accountability each : Accountability.findAccountabilitiesByParty(
				this, date)) {
			each.terminate(date);
		}
		terminateDate = date;
		save();
	}

}
