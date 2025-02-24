package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.models.BuyLicenseKey;

import java.util.List;

public interface BuyLicenseKeyService {

    public List<BuyLicenseKey> getAllBuyLicenseKey();

    public List<BuyLicenseKey> getByUserId(Long userId);

    public void buyLicenseKeyExpiryValidate();
}
