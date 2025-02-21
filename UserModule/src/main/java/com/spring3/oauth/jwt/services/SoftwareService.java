package com.spring3.oauth.jwt.services;

import com.spring3.oauth.jwt.models.Softwares;

import java.util.List;
import java.util.Optional;

public interface SoftwareService {

    Softwares addSoftware(Softwares softwares);
    Boolean deleteSoftware(Softwares softwares);
    List<Softwares> getAllSoftwares();
    Softwares updateSoftware(Softwares softwares);

    Optional<Softwares> getSoftwareDetailsById(Long softwareId);

}
