package com.qy.game.ssh.biz.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.bean.QueryParam;
import com.qy.game.ssh.biz.SSHUtilBiz;
import com.qy.game.ssh.dao.SSHUtilDao;

@Transactional
@Service
public class SSHUtilBizImpl implements SSHUtilBiz {

	@Resource
	private SSHUtilDao sshUtilDao;

	@Override
	public Serializable saveObject(Object obj) {

		return sshUtilDao.saveObject(obj);
	}

	@Override
	public Object getObjectById(Class<?> clazz, Serializable id) {

		return sshUtilDao.getObjectById(clazz, id);
	}

	@Override
	public Boolean updateObject(Object obj) {

		return sshUtilDao.updObject(obj);
	}

	@Override
	public Boolean deleteObject(Object obj) {

		return sshUtilDao.delObject(obj);
	}

	@Override
	public boolean delObjectById(Class clazz, Serializable id) {
		
		return sshUtilDao.delObjectById(clazz, id);
	}
	
	@Override
	public Integer getObjectCount(Class<?> clazz, String colName, QueryParam queryParam) {

		return sshUtilDao.getCount(clazz, colName, queryParam);
	}

	@Override
	public List<?> getObjectList(Class<?> clazz, QueryParam queryParam, PageUtil pageUtil) {

		return sshUtilDao.getObjectList(clazz, queryParam, pageUtil);
	}

	@Override
	public PageUtil getPageCount(Class<?> clazz, QueryParam queryParam, PageUtil pageUtil) {
		
		// 总记录数
		int totalCount = sshUtilDao.getCount(clazz, "id", queryParam);
		
	    // 总页数
		int pageCount = ((totalCount + pageUtil.getPageSize() - 1)/pageUtil.getPageSize());
		
		
		pageUtil.setPageCount(pageCount);
		pageUtil.setTotalCount(totalCount);
		
		return pageUtil;
	}

	@Override
	public Object getSum(Class<?> clazz, String colName, QueryParam queryParam) {
		
		return sshUtilDao.getSum(clazz, colName, queryParam);
	}

	@Override
	public List<?> getObjectList(String hql, Object[] queryParam,
			PageUtil pageUtil) {
		
		return sshUtilDao.getObjectList(hql, queryParam, pageUtil);
	}
	
	@Override
	public List<?> getObjectListBySQL(String sql, Object[] queryParam,
			PageUtil pageUtil) {
		
		return sshUtilDao.getObjectListBySQL(sql, queryParam, pageUtil);
	}

	@Override
	public Object getAvg(Class<?> clazz, String colName, QueryParam queryParam) {
		
		return sshUtilDao.getAvg(clazz, colName, queryParam);
	}

	@Override
	public Boolean updateObjectBySQL(String sql, Object[] queryParam) {
		return sshUtilDao.updObjectBySQL(sql, queryParam);
		
	}

	@Override
	public Integer getObjectCount(String hql, Object[] queryParam) {
		
		return sshUtilDao.getCount(hql, queryParam);
	}
	
	@Override
	public Object getObject(String hql, Object[] queryParam) {
		List<?> back=getObjectList(hql, queryParam, new PageUtil(1, 1));
		if(back!=null&&back.size()>0){
			return back.get(0);
		}
		return null;
	}
}
