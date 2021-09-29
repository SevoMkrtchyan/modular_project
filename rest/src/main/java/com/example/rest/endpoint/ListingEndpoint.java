package com.example.rest.endpoint;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/listings")
@RequiredArgsConstructor
@Slf4j
public class ListingEndpoint {

    private final ListingService listingService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping()
    public List<ListingDto> findAll() {
        List<Listing> listings = listingService.findAll();
        List<ListingDto> listingDtos = parseListingsToListingDtoList(listings);
        log.info("Requested to find all listings," +
                "sending response with parsed listingsDto list which size is {}", listingDtos.size());
        return listingDtos;
    }

    @PostMapping()
    public ResponseEntity saveListing(@RequestBody ListingCreateDto listing) {
        if (listingService.saveListing(parseCreateListingToListing(listing))) {
            log.info("Listing was saved by User with email {} ", listing.getUserDto().getEmail());
            return ResponseEntity.ok().build();
        }
        log.info("Failed to create listing");
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Listing> deleteListingById(@PathVariable(name = "id") int id) {
        Listing listing = listingService.findListingById(id);
        if (listing == null) {
            log.info("Requested to delete listing by id {}, which not exist", id);
            return ResponseEntity.notFound().build();
        }
        listingService.deleteListingById(listing.getId());
        log.info("Listing with {} id was deleted", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<ListingDto> getListingById(@PathVariable(name = "id") int id) {
        Listing listing = listingService.findListingById(id);
        if (listing == null) {
            log.info("Attempt to find listing by id {} which does not exist ", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Sending response with listing which id is {}", id);
        return ResponseEntity.ok(parseListingToDto(listing));
    }

    @PutMapping
    public ResponseEntity<ListingDto> updateListing(@RequestBody ListingCreateDto listing) {
        Listing updatedListing = listingService.updateListing(parseCreateListingToListing(listing));
        if (updatedListing != null) {
            log.info("Listing with id {} have been updated successfully ", updatedListing.getId());
            return ResponseEntity.ok(parseListingToDto(updatedListing));
        }
        log.info("Failed to update listing,may be wrong id or wrong user");
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
    }

    @GetMapping("/byUser/{email}")
    public ResponseEntity<List<ListingDto>> getListingsByUserEmail(@PathVariable(name = "email") String email) {
        List<Listing> listingFromDB = listingService.findListingByUserEmail(email);
        if (listingFromDB != null) {
            List<ListingDto> listingDtoList = parseListingsToListingDtoList(listingFromDB);
            log.info("Sending response with parsed listingsDto list searched by user email {}" +
                    " , list size is {}", email, listingDtoList.size());
            return ResponseEntity.ok(listingDtoList);
        }
        log.info("Requested listings by user email {} is not present", email);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<ListingDto>> getListingsByCategoryId(@PathVariable(name = "categoryId") int id) {
        try {
            List<Listing> listingsFromDbByCategoryID = listingService.findListingByCategoryId(categoryService.findCategoryById(id));
            List<ListingDto> listingDtoList;
            if (listingsFromDbByCategoryID != null) {
                listingDtoList = parseListingsToListingDtoList(listingsFromDbByCategoryID);
                log.info("Sending to response parsed listingsDto list founded by category id {}", id);
                return ResponseEntity.ok(listingDtoList);
            }
            log.info("Cannot find listings by category id {}", id);
            return ResponseEntity.notFound().build();
        } catch (NullPointerException e) {
            log.info("Cannot find listings by category id {}", id);
            return ResponseEntity.notFound().build();
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
