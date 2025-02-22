package com.spring3.oauth.jwt.services;


import com.spring3.oauth.jwt.model.LicenseKey;

import java.util.List;
import java.util.Optional;

public interface LicenseKeyService {

    LicenseKey addLicense(LicenseKey licenseKey);
    Boolean deleteLicense(LicenseKey licenseKey);
    List<LicenseKey> getAllLicense();
    LicenseKey updateLicense(LicenseKey licenseKey);

    Optional<LicenseKey> getLicenseDetailsById(Long licenseKeyId);

}
