package com.seezoon.eagle.mybatis.mapper;

import java.io.Serializable;
import java.util.List;

/**
 * 通用mapper 
 * @author hdf
 * 2017年8月30日
 * @param <T>
 */
public interface CrudMapper<T> extends BaseMapper {

	public int insert(T t);
	public int insertSelective(T t);
	public int updateByPrimaryKeySelective(T t);
	public int updateByPrimaryKey(T t);
	public T selectByPrimaryKey(Serializable id);
	public int deleteByPrimaryKey(Serializable id);
	public int visualDeleteByPrimaryKey(T t);
	public List<T> findList(T t);
}
