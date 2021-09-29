package com.example.common.service;

import com.example.common.entity.Category;
import com.example.common.entity.Listing;

import java.util.List;

public interface ListingService {


    List<Listing> findAll();

    boolean saveListing(Listing listing);

    Listing findListingById(int id);

    List<Listing> findListingByCategoryId(Category category);

    List<Listing> findListingByUserEmail(String email);

    boolean deleteListingById(int id);

    Listing updateListing(Listing listing);

}
