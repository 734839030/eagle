package com.seezoon.eagle.mybatis.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seezoon.eagle.mybatis.mapper.CrudMapper;

/**
 * 当需要自动拥有增删改查功能时候继承
 * @author hdf
 * 2017年8月30日
 * @param <M> mapper
 * @param <T> entity
 */
public abstract class CrudDao<M extends CrudMapper<T>,T extends BaseEntity>{

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected M m; 
	
	public int insert(T t){
		return m.insert(t);
	}
	public int insertSelective(T t){
		return m.insert(t);
	}
	public int updateByPrimaryKeySelective(T t){
		return m.updateByPrimaryKeySelective(t);
	}
	public int updateByPrimaryKey(T t){
		return m.updateByPrimaryKey(t);
	}
	public T selectByPrimaryKey(Serializable id){
		return m.selectByPrimaryKey(id);
	}
	public int deleteByPrimaryKey(Serializable id){
		return m.deleteByPrimaryKey(id);
	}
	public int visualDeleteByPrimaryKey(T t){
		return m.visualDeleteByPrimaryKey(t);
	}
	public List<T> findList(T t){
		return m.findList(t);
	}
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize,boolean count){
		PageHelper.startPage(pageNum, pageSize, count);
		List<T>  list = this.findList(t);
		PageInfo<T> pageInfo = new PageInfo<T>(list);
		return pageInfo;
	}
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize){
		PageHelper.startPage(pageNum, pageSize, Boolean.TRUE);
		List<T>  list = this.findList(t);
		PageInfo<T> pageInfo = new PageInfo<T>(list);
		return pageInfo;
	}
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize,String order,String direction){
		PageHelper.startPage(pageNum, pageSize, Boolean.TRUE);
		List<T>  list = this.findList(t);
		PageInfo<T> pageInfo = new PageInfo<T>(list);
		return pageInfo;
	}
}
