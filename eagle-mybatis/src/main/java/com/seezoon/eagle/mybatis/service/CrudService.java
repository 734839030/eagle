package com.seezoon.eagle.mybatis.service;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seezoon.eagle.mybatis.persistence.BaseEntity;
import com.seezoon.eagle.mybatis.persistence.CrudDao;

import net.sf.jsqlparser.statement.truncate.Truncate;

/**
 * 当需要自动拥有增删改查功能时候继承
 * @author hdf
 *
 */
@Transactional(rollbackFor=Exception.class)
public abstract class CrudService<D extends CrudDao<T>,T extends BaseEntity>{

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected D dao; 
	
	public int insert(T t){
		return dao.insert(t);
	}
	public int insertSelective(T t){
		return dao.insert(t);
	}
	public int updateByPrimaryKeySelective(T t){
		return dao.updateByPrimaryKeySelective(t);
	}
	public int updateByPrimaryKey(T t){
		return dao.updateByPrimaryKey(t);
	}
	@Transactional(readOnly=true)
	public T selectByPrimaryKey(Serializable id){
		return dao.selectByPrimaryKey(id);
	}
	public int deleteByPrimaryKey(Serializable id){
		return dao.deleteByPrimaryKey(id);
	}
	@Transactional(readOnly=true)
	public List<T> findList(T t){
		return dao.findList(t);
	}
	@Transactional(readOnly=true)
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize,boolean count){
		PageHelper.startPage(pageNum, pageSize, count);
		List<T>  list = this.findList(t);
		PageInfo<T> pageInfo = new PageInfo<T>(list);
		return pageInfo;
	}
	@Transactional(readOnly=true)
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize){
		PageHelper.startPage(pageNum, pageSize, Boolean.TRUE);
		List<T>  list = this.findList(t);
		PageInfo<T> pageInfo = new PageInfo<T>(list);
		return pageInfo;
	}
}
