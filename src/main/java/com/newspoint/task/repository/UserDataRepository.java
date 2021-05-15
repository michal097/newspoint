package com.newspoint.task.repository;

import com.newspoint.task.model.UserDataModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserDataRepository extends CrudRepository<UserDataModel, Long> {
    Page<UserDataModel> findAll(Pageable pageable);
    List<UserDataModel> findAll();
}
