package com.ccs.trolls.suki.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ccs.trolls.suki.service.FollowupService;
import com.ccs.trolls.suki.api.model.Followup;

@RestController
@RequestMapping(path = "/api/v1/followup", produces = "application/json;charset=utf-8")
public class FollowupController {
  private static final Logger logger = LoggerFactory.getLogger(FollowupController.class);

  @Autowired private FollowupService service;

  @RequestMapping(path = "/{branchId}/{policyId}")
  @ResponseBody
  public List<Followup> getFollowup(@PathVariable String branchId, @PathVariable String policyId) {
    return service.getFollowup(branchId, policyId);
  }
}
