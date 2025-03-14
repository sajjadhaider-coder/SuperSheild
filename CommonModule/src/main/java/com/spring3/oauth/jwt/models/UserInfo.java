package com.spring3.oauth.jwt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @NotNull
    @Column(name = "USER_NAME", unique = true, nullable = false, length = 20)
    private String username;

    /*@NotNull
    @Column(name = "VERIFICATION_CODE", length = 10)
    private String verificationCode;
*/
   // @JsonIgnore // Prevent password from being serialized
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "LOCATION")
    private String userLocation;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

  /*  @Column(name = "USER_ID")
    private Long userId;
*/
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "PARENT_ID")
    private String parentId;

    @Column(name = "BALANCE")
    private BigDecimal userBalance;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<UserRole> roles = new HashSet<>();

    /*@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<LicenseKey> licenseKey = new HashSet<>();*/

}
