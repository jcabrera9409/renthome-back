package com.surrender.service.impl;

import com.surrender.model.Inquilino;
import com.surrender.repo.InquilinoRepo;
import com.surrender.service.IInquilinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InquilinoServiceImpl extends CRUDImpl<Inquilino, Integer> implements IInquilinoService {

    @Autowired
    private InquilinoRepo inquilinoRepo;

    @Override
    protected InquilinoRepo getRepo() {
        return inquilinoRepo;
    }
}
