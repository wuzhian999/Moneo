package com.wza.moneo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wza.moneo.common.exception.ResourceNotFoundException;
import com.wza.moneo.dto.AccountSaveRequest;
import com.wza.moneo.entity.Account;
import com.wza.moneo.entity.Bill;
import com.wza.moneo.mapper.AccountMapper;
import com.wza.moneo.mapper.BillMapper;
import com.wza.moneo.service.AccountService;
import com.wza.moneo.vo.AccountVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final BillMapper billMapper;

    @Override
    public List<AccountVo> list() {
        return accountMapper.selectList(null).stream().map(this::toVo).toList();
    }

    @Override
    public AccountVo getById(Long id) {
        return toVo(getRequired(id));
    }

    @Override
    @Transactional
    public AccountVo create(AccountSaveRequest request) {
        Account account = new Account();
        apply(account, request);
        accountMapper.insert(account);
        applyDefault(account);
        return toVo(account);
    }

    @Override
    @Transactional
    public AccountVo update(Long id, AccountSaveRequest request) {
        Account account = getRequired(id);
        apply(account, request);
        accountMapper.updateById(account);
        applyDefault(account);
        return toVo(account);
    }

    @Override
    public void delete(Long id) {
        Account account = getRequired(id);
        Long billCount = billMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Bill>()
                .eq(Bill::getAccountId, id));
        if (billCount != null && billCount > 0) {
            throw new com.wza.moneo.common.exception.BusinessException(409, "已有账单使用该账户，无法删除");
        }
        accountMapper.deleteById(account.getId());
    }

    @Override
    public Account getRequired(Long id) {
        Account account = accountMapper.selectById(id);
        if (account == null) {
            throw new ResourceNotFoundException("账户");
        }
        return account;
    }

    private void apply(Account account, AccountSaveRequest request) {
        account.setName(request.name());
        account.setIcon(request.icon() == null || request.icon().isBlank() ? "💳" : request.icon());
        account.setColor(request.color() == null || request.color().isBlank() ? "#E4F0FA" : request.color());
        account.setInitialBalance(request.initialBalance() == null ? BigDecimal.ZERO : request.initialBalance());
        account.setIsDefault(Boolean.TRUE.equals(request.isDefault()));
    }

    private void applyDefault(Account account) {
        if (Boolean.TRUE.equals(account.getIsDefault())) {
            accountMapper.update(null, new LambdaUpdateWrapper<Account>()
                    .eq(Account::getIsDefault, true)
                    .ne(Account::getId, account.getId())
                    .set(Account::getIsDefault, false));
        }
    }

    private AccountVo toVo(Account account) {
        return new AccountVo(account.getId(), account.getName(), account.getIcon(), account.getColor(), account.getInitialBalance(), account.getIsDefault());
    }
}
