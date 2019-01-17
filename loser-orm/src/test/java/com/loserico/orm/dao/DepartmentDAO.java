package com.loserico.orm.dao;

import java.util.List;

import com.loserico.orm.entity.DepartmentEntity;

public interface DepartmentDAO {
	public List<DepartmentEntity> getAllDepartments();

	public DepartmentEntity getDepartmentById(Integer id);

	public boolean addDepartment(DepartmentEntity dept);

	public boolean removeDepartment(DepartmentEntity dept);

	public boolean removeAllDepartments();
}
