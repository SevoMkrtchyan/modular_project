package com.example.common.service.impl;

import com.example.common.entity.Category;
import com.example.common.entity.Listing;
import com.example.common.repository.ListingRepository;
import com.example.common.repository.UserRepository;
import com.example.common.service.CategoryService;
import com.example.common.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @Override
    public List<Listing> findAll() {
        return listingRepository.findAll();
    }

    @Override
    public boolean saveListing(Listing listing) {
        if (listing != null && listing.getCategory() != null && listing.getUser() != null) {
            listingRepository.save(listing);
            return true;
        }
        return false;
    }

    @Override
    public Listing findListingById(int id) {
        Optional<Listing> byId = listingRepository.findById(id);
        return byId.orElse(null);
    }

    @Override
    public boolean deleteListingById(int id) {
        Optional<Listing> fromDB = listingRepository.findById(id);
        if (fromDB.isPresent()) {
            listingRepository.delete(fromDB.get());
            return true;
        }
        return false;
    }

    @Override
    public Listing updateListing(Listing listing) {
        if (listingRepository.findById(listing.getId()).isPresent()) {
            return listingRepository.save(listing);
        }
        return null;
    }

    @Override
    public List<Listing> findListingByUserEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return listingRepository.findAllByUserEmail(email);
        }
        return null;
    }

    @Override
    public List<Listing> findListingByCategoryId(Category category) {
        if (categoryService.findCategoryById(category.getId()) != null) {
            return listingRepository.findAllByCategory(category);
        }
        return null;
    }

}
