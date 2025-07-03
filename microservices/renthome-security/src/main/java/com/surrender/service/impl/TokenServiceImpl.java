package com.surrender.service.impl;

import com.surrender.model.Token;
import com.surrender.repo.TokenRepo;
import com.surrender.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl extends CRUDImpl<Token, Integer> implements ITokenService {

    @Autowired
    private TokenRepo tokenRepo;

    @Override
    protected TokenRepo getRepo() {
        return tokenRepo;
    }
}
