package com.seezoon.eagle.mybatis.persistence;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 不推荐使用联合主键,推荐使用配套的生成工具
 * @author hdf
 *
 * @param <T>
 */
public interface CrudDao<T> extends BaseDao {

	public int insert(T t);
	public int insertSelective(T t);
	public int updateByPrimaryKeySelective(T t);
	public int updateByPrimaryKey(T t);
	public T selectByPrimaryKey(Serializable id);
	public int deleteByPrimaryKey(Serializable id);
	public List<T> findList(T t);
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize,boolean count);
	public PageInfo<T> findByPage(T t,int pageNum,int pageSize);
}
