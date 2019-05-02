package com.bank.accounting.service;

import com.bank.accounting.core.annotation.Bean;
import com.bank.accounting.dao.DenemeDao;
import com.bank.accounting.model.ResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/money")
public class MoneyTransferService {

    @Bean
    static DenemeDao denemeDao;

    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getTrackInJSON(ResponseModel request) {
        this.getClass();
        denemeDao.save(request.getField());
    }

}
