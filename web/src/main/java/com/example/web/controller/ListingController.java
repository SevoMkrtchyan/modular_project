package com.example.web.controller;

import com.example.common.dto.ListingCreateDto;
import com.example.common.dto.ListingDto;
import com.example.common.dto.UserDto;
import com.example.common.entity.Listing;
import com.example.common.service.CategoryService;
import com.example.common.service.ListingService;
import com.example.common.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ListingController {


    private final ListingService listingService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping("/listings")
    public String findAll(ModelMap modelMap) {
        List<Listing> listings = listingService.findAll();
        List<ListingDto> listingDtos = parseListingsToListingDtoList(listings);
        modelMap.addAttribute("listings", listingDtos);
        log.info("Requested to find all listings," +
                "sending response with parsed listingsDto list which size is {}", listingDtos.size());
        return "listings";
    }

    @PostMapping("/saveListing")
    public String saveListing(@RequestBody ListingCreateDto listing) {
        if (listingService.saveListing(parseCreateListingToListing(listing))) {
            log.info("Listing was saved by User with email {} ", listing.getUserDto().getEmail());
            return "redirect:/listings";
        }
        log.info("Failed to create listing");
        return "redirect:/listings";
    }

    @GetMapping(value = "/deleteListingById/{id}")
    public String deleteListingById(@PathVariable(name = "id") int id) {
        Listing listing = listingService.findListingById(id);
        if (listing == null) {
            log.info("Requested to delete listing by id {}, which not exist", id);
            return "redirect:/listings";
        }
        listingService.deleteListingById(listing.getId());
        log.info("Listing with {} id was deleted", id);
        return "redirect:/listings";
    }

    @GetMapping(value = "/getListingById/{id}")
    public String getListingById(@PathVariable(name = "id") int id, ModelMap modelMap) {
        Listing listing = listingService.findListingById(id);
        if (listing == null) {
            log.info("Attempt to find listing by id {} which does not exist ", id);
            return "redirect:/listings";
        }
        modelMap.addAttribute("listing", parseListingToDto(listing));
        log.info("Sending response with listing which id is {}", id);
        return "singleListing";
    }

    @PostMapping("/updateListing")
    public String updateListing(@RequestBody ListingCreateDto listing) {
        Listing updatedListing = listingService.updateListing(parseCreateListingToListing(listing));
        if (updatedListing != null) {
            log.info("Listing with id {} have been updated successfully ", updatedListing.getId());
            return "redirect:/getListingById/" + updatedListing.getId();
        }
        log.info("Failed to update listing,may be wrong id or wrong user");
        return "redirect:/listings";
    }

    @GetMapping("/byUser/{email}")
    public String getListingsByUserEmail(@PathVariable(name = "email") String email, ModelMap modelMap) {
        List<Listing> listingFromDB = listingService.findListingByUserEmail(email);
        if (listingFromDB != null) {
            List<ListingDto> listingDtoList = parseListingsToListingDtoList(listingFromDB);
            log.info("Sending response with parsed listingsDto list searched by user email {}" +
                    " , list size is {}", email, listingDtoList.size());
            modelMap.addAttribute("listings", listingDtoList);
            return "listingsByEmail";
        }
        log.info("Requested listings by user email {} is not present", email);
        return "redirect:/listings";
    }

    @GetMapping("/byCategory/{categoryId}")
    public String getListingsByCategoryId(@PathVariable(name = "categoryId") int id, ModelMap modelMap) {
        try {
            List<Listing> listingsFromDbByCategoryID = listingService.findListingByCategoryId(categoryService.findCategoryById(id));
            List<ListingDto> listingDtoList;
            if (listingsFromDbByCategoryID != null) {
                listingDtoList = parseListingsToListingDtoList(listingsFromDbByCategoryID);
                log.info("Sending to response parsed listingsDto list founded by category id {}", id);
                modelMap.addAttribute("listings", listingDtoList);
                return "listingByCategory";
            }
            log.info("Cannot find listings by category id {}", id);
            return "redirect:/listings";
        } catch (NullPointerException e) {
            log.info("Cannot find listings by category id {}", id);
            return "redirect:/listings";
        }
    }

    private List<ListingDto> parseListingsToListingDtoList(List<Listing> listings) {
        List<ListingDto> listingDtos = listings.stream().map(e ->
                modelMapper.map(e, ListingDto.class)).collect(Collectors.toList());
        listingDtos.forEach(e -> {
            for (Listing listing : listings) {
                if (listing.getUser() != null && listing.getId() == e.getId()) {
                    e.setUserDto(modelMapper.map(listing.getUser(), UserDto.class));
                }
            }
        });
        return listingDtos;
    }

    private ListingDto parseListingToDto(Listing listing) {
        ListingDto listingDto = modelMapper.map(listing, ListingDto.class);
        listingDto.setUserDto(modelMapper.map(listing.getUser(), UserDto.class));
        return listingDto;
    }

    private Listing parseCreateListingToListing(ListingCreateDto listingCreateDto) {
        Listing listing = modelMapper.map(listingCreateDto, Listing.class);
        listing.setUser(userService.findUserByEmail(listingCreateDto.getUserDto().getEmail()));
        return listing;
    }

}
