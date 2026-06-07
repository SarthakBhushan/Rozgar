package com.rozgar.backend.business.service;


import com.rozgar.backend.auth.entity.User;
import com.rozgar.backend.business.dto.request.CreateBusinessRequest;
import com.rozgar.backend.business.dto.request.UpdateBusinessRequest;
import com.rozgar.backend.business.dto.response.BusinessResponse;
import com.rozgar.backend.business.entity.Business;
import com.rozgar.backend.business.enums.BusinessStatus;
import com.rozgar.backend.business.enums.BusinessType;
import com.rozgar.backend.business.repository.BusinessRepository;
import com.rozgar.backend.common.exception.ConflictException;
import com.rozgar.backend.common.exception.ForbiddenException;
import com.rozgar.backend.common.exception.ResourceNotFoundException;
import com.rozgar.backend.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    @Transactional
    public BusinessResponse create(CreateBusinessRequest request, User currentUser){

        if(businessRepository.existsByOwnerId(currentUser.getId())){
            throw new ConflictException("You already have a registered business");
        }

        if(request.gstNumber()!=null && businessRepository.existsByGstNumber(request.gstNumber())){
            throw new ConflictException("A business with this GST number already exists");
        }

        if(request.panNumber()!=null && businessRepository.existsByPanNumber(request.panNumber())){
            throw new ConflictException("A business with this PAN number already exists");
        }

        Business business = Business.builder()
                .name(request.name())
                .description(request.description())
                .businessType(request.businessType())
                .gstNumber(request.gstNumber())
                .panNumber(request.panNumber())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .address(request.address())
                .phone(request.phone())
                .website(request.website())
                .ownerId(currentUser.getId())
                .status(BusinessStatus.PENDING)
                .build();

        return BusinessResponse.from(businessRepository.save(business));
    }

    @Transactional(readOnly = true)
    public BusinessResponse getById(Long id){
        return BusinessResponse.from(findById(id));
    }

    @Transactional(readOnly = true)
    public BusinessResponse getMyBusiness(User currentUser){
        Business business = businessRepository.findByOwnerId(currentUser.getId())
                .orElseThrow(()-> new ResourceNotFoundException(
                        "You do not have a registered business yet"
                ));
        return BusinessResponse.from(business);
    }

    @Transactional
    public BusinessResponse update(Long id, UpdateBusinessRequest request, User currentUser){
        Business business = findById(id);
        if(!business.getOwnerId().equals(currentUser.getId())){
            throw new ForbiddenException("You are not the owner of this business");
        }
        if(request.name() !=null)business.setName(request.name());
        if(request.description() !=null)business.setDescription(request.description());
        if (request.businessType() != null) business.setBusinessType(request.businessType());
        if (request.city()         != null) business.setCity(request.city());
        if (request.state()        != null) business.setState(request.state());
        if (request.pincode()      != null) business.setPincode(request.pincode());
        if (request.address()      != null) business.setAddress(request.address());
        if (request.phone()        != null) business.setPhone(request.phone());
        if (request.website()      != null) business.setWebsite(request.website());
        return BusinessResponse.from(businessRepository.save(business));
    }

    @Transactional
    public void delete(Long id, User currentUser){
        Business business = findById(id);
        if(!business.getOwnerId().equals(currentUser.getId())){
            throw new ForbiddenException("You are not the owner of this business");
        }
        businessRepository.delete(business);
    }

    @Transactional(readOnly = true)
    public PagedResponse<BusinessResponse> browse(
            String city, BusinessType type, int page, int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if(type!=null && city!=null){
            return PagedResponse.from(
                    businessRepository.findByBusinessTypeAndStatus(
                            type, BusinessStatus.VERIFIED, pageable).map(BusinessResponse::from));
        }

        if(type!=null){
            return PagedResponse.from(
                    businessRepository.findByBusinessTypeAndStatus(
                            type, BusinessStatus.VERIFIED, pageable).map(BusinessResponse::from));
        }

        if(city!=null){
            return PagedResponse.from(
                    businessRepository.findByCityIgnoreCaseAndStatus(
                            city, BusinessStatus.VERIFIED, pageable).map(BusinessResponse::from));
        }

        return PagedResponse.from(
                businessRepository.findByStatus(BusinessStatus.VERIFIED, pageable).map(BusinessResponse::from));
    }

    private Business findById(Long id){
        return businessRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Business", String.valueOf(id)));
    }
}
