package com.spring3.oauth.jwt.dtos;

import com.spring3.oauth.jwt.models.UserInfo;
import lombok.Data;

import java.util.List;

@Data
public class SubAgentListResponse {
    public UserInfo userInfo;
    public List<UserInfo> subAgentList;
}
