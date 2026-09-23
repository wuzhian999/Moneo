package com.wza.moneo.service;

import com.wza.moneo.dto.AccountSaveRequest;
import com.wza.moneo.entity.Account;
import com.wza.moneo.vo.AccountVo;

import java.util.List;

public interface AccountService {
    List<AccountVo> list();
    AccountVo getById(Long id);
    AccountVo create(AccountSaveRequest request);
    AccountVo update(Long id, AccountSaveRequest request);
    void delete(Long id);
    Account getRequired(Long id);
}
