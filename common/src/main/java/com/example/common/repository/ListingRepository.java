package com.example.common.repository;

import com.example.common.entity.Category;
import com.example.common.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ListingRepository extends JpaRepository<Listing, Integer> {

    List<Listing> findAllByUserEmail(String email);

    List<Listing> findAllByCategory(Category category);

    @Modifying
    @Query(value = "UPDATE listing SET category_id=:nullValue WHERE category_id=:id", nativeQuery = true)
    void changeListingCategoryNullWhenCategoryDeleted(@Param("id") Integer id, @Param("nullValue") Integer nullValue);

    @Modifying
    @Query(value = "UPDATE listing SET user_id=:nullValue WHERE user_id=:id", nativeQuery = true)
    void changeListingUserNullWhenUserDeleted(@Param("id") Integer id, @Param("nullValue") Integer nullValue);

}
