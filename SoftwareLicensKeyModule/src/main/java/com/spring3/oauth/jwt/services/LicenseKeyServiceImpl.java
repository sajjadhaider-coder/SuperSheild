package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.model.LicenseKey;
import com.spring3.oauth.jwt.repository.LicenseKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LicenseKeyServiceImpl implements LicenseKeyService {

@Autowired
LicenseKeyRepository licenseKeyRepository;

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
}
