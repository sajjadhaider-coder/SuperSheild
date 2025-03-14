package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.models.BuyLicenseKey;
import com.spring3.oauth.jwt.models.LicenseKey;
import com.spring3.oauth.jwt.models.Softwares;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.repositories.UserRepository;
import com.spring3.oauth.jwt.repository.BuyLicenseKeyRepository;
import com.spring3.oauth.jwt.repository.LicenseKeyRepository;
import com.spring3.oauth.jwt.repository.SoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LicenseKeyServiceImpl implements LicenseKeyService {

@Autowired
LicenseKeyRepository licenseKeyRepository;

@Autowired
SoftwareRepository softwareRepository;

@Autowired
    BuyLicenseKeyRepository buyLicenseKeyRepository;

@Autowired
UserRepository userRepository;


    @Override
    public LicenseKey addLicense(LicenseKey licenseKey) {
        return licenseKeyRepository.save(licenseKey);
    }

    @Override
    public Boolean deleteLicense(LicenseKey licenseKey) {

        Boolean isDeleted = false;
        try {
            licenseKeyRepository.delete(licenseKey);
            isDeleted = true;
        } catch (Exception e){
            isDeleted = false;
        }
        return  isDeleted;
    }

    @Override
    public List<LicenseKey> getAllLicense() {
        List<LicenseKey> licenseKeys = null;
        try {
            licenseKeys = licenseKeyRepository.findAll();
        } catch (Exception e){
        }
        return licenseKeys;
    }

    @Override
    public LicenseKey updateLicense(LicenseKey licenseKey) {
        return null;
    }

    @Override
    public Optional<LicenseKey> getLicenseDetailsById(Long licenseKeyId) {
        return licenseKeyRepository.findById(licenseKeyId);
    }

   /* public UserInfo buyKey(List<String> keyIds, String userId) {
        Set<LicenseKey> licenseKeysIds = new HashSet<>();
        Optional<LicenseKey> userRole = null;
        UserInfo userInfo = null;
        try {
            // Fetch the user
            Optional<UserInfo> userInfoOpt = userRepository.findById(Long.valueOf(userId));
            if (userInfoOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            userInfo = userInfoOpt.get();

            // Get existing roles
            Set<LicenseKey> existingLinceseKey = userInfo.getLicenseKey();
            if (existingLinceseKey == null) {
                existingLinceseKey = new HashSet<>();
            }

            // Fetch and add new roles
            for (String id : keyIds) {
                Optional<LicenseKey> lkey = licenseKeyRepository.findById(Long.valueOf(id));
                lkey.ifPresent(existingLinceseKey::add);
            }

            // Set updated roles and save
            userInfo.setLicenseKey(existingLinceseKey);
            return userRepository.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }*/

/*
    @Override
    public UserInfo buyKey(List<String> keyIds, String userId) {
        Set<LicenseKey> licenseKeysIds = new HashSet<>();
        Optional<LicenseKey> licenseKey = null;
        UserInfo userInfo = null;
        try {
            // Fetch the user
            Optional<UserInfo> userInfoOpt = userRepository.findById(Long.valueOf(userId));
            if (userInfoOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            userInfo = userInfoOpt.get();

            // Get existing roles
         //   Set<LicenseKey> existingLinceseKey = userInfo.getLicenseKey();
            if (existingLinceseKey == null) {
                existingLinceseKey = new HashSet<>();
            }

            // Fetch and add new roles
            for (String id : keyIds) {
                Optional<LicenseKey> lkey = licenseKeyRepository.findById(Long.valueOf(id));
                lkey.ifPresent(existingLinceseKey::add);
            }

            // Set updated roles and save
            userInfo.setLicenseKey(existingLinceseKey);
            return userRepository.save(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
    */

    @Override
    public Softwares assignKey(List<String> keyIds, String softwareId) {


        Set<LicenseKey> licenseKeys = new HashSet<>();
        Optional<LicenseKey> licenseKey = null;
        Softwares softwares = null;
        try {
            // Fetch the user
            Optional<Softwares> sftInfoOpt = softwareRepository.findById(Long.valueOf(softwareId));
            if (sftInfoOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + softwareId);
            }
            softwares = sftInfoOpt.get();

            // Get existing roles
            Set<LicenseKey> existingLicenseKeys = softwares.getLiceseKeys();
            if (existingLicenseKeys == null) {
                existingLicenseKeys = new HashSet<>();
            }

            // Fetch and add new roles
            for (String id : keyIds) {
                Optional<LicenseKey> lkeys = licenseKeyRepository.findById(Long.valueOf(id));
                lkeys.ifPresent(existingLicenseKeys::add);
            }

            // Set updated roles and save
            softwares.setLiceseKeys(existingLicenseKeys);
            softwares = softwareRepository.save(softwares);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return softwares;
    }

    @Override
    public BuyLicenseKey buyKey(BuyLicenseKey buyLicenseKey) {
        Optional<Softwares> softwares = softwareRepository.findById(buyLicenseKey.getSoftwareId());
        if (softwares.isPresent()) {
            Long softwareLicenseId = buyLicenseKey.getSoftwareLicenseId();
            boolean softwareLicense = false;
            BigDecimal softwarePrice = BigDecimal.ZERO;
            for (LicenseKey licenseKey : softwares.get().getLiceseKeys()) {
                if (Long.valueOf(licenseKey.getId()).equals(softwareLicenseId)) {
                    softwareLicense = true;
                    softwarePrice = licenseKey.getKeyPrice();
                    break; // Exit the loop once we find a match
                }
            }

            if (softwareLicense) {
                Optional<UserInfo> userInfo = userRepository.findById(buyLicenseKey.getUserId());
                if (userInfo.isPresent()) {
                    boolean result = userInfo.get().getUserBalance().compareTo(softwarePrice) == 0 ||
                            userInfo.get().getUserBalance().compareTo(softwarePrice) > 0;
                    if (result) {
                        // Save and return the updated buyLicenseKey
                        return buyLicenseKeyRepository.save(buyLicenseKey);
                    } else {
                        throw new RuntimeException("Your available balance is limited");
                    }
                } else {
                    throw new RuntimeException("User not found");
                }
            } else {
                throw new RuntimeException("License key not found for the given software.");
            }
        } else {
            throw new RuntimeException("software's not found");
        }
    }
}