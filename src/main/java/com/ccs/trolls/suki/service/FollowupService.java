package com.ccs.trolls.suki.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ccs.trolls.suki.code.CodeType;
import com.ccs.trolls.suki.code.domain.Descpf;
import com.ccs.trolls.suki.code.service.CodeService;
import com.ccs.trolls.suki.domain.Fluppf;
import com.ccs.trolls.suki.domain.FluppfRepository;
import com.ccs.trolls.suki.domain.Hxclpf;
import com.ccs.trolls.suki.domain.HxclpfRepository;
import com.ccs.trolls.suki.domain.Zhclpf;
import com.ccs.trolls.suki.domain.ZhclpfRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.ccs.trolls.suki.api.model.CodeName;
import com.ccs.trolls.suki.api.model.Followup;

@Service
public class FollowupService {
  private static final Logger logger = LoggerFactory.getLogger(FollowupService.class);

  @Autowired private FluppfRepository fluppfRepository;

  @Autowired private HxclpfRepository hxclpfRepository;

  @Autowired private ZhclpfRepository zhclpfRepository;

  @Autowired private CodeService codeService;

  @HystrixCommand
  public List<Followup> getFollowup(String branchId, String policyId) {
    List<Fluppf> fluppfList = fluppfRepository.findByChdrcoyAndChdrnum(branchId, policyId);
    List<Hxclpf> hxclpfList =
        hxclpfRepository.findByChdrcoyAndChdrnumOrderByHxclseqno(branchId, policyId);

    List<Descpf> descpfs =
        codeService.getCodeNames(
            branchId,
            new String[] {
              CodeType.FOLLOWUP_CODE.getTable(),
              CodeType.FOLLOWUP_TYPE.getTable(),
              CodeType.FOLLOWUP_STATUS.getTable(),
              CodeType.FOLLOWUP_LETTYP.getTable()
            });

    List<Followup> result = new ArrayList<Followup>();
    for (Fluppf flup : fluppfList) {
      Followup dto = new Followup();
      dto.setBranchId(flup.getChdrcoy());
      dto.setPolicyId(flup.getChdrnum());
      dto.setSeq(flup.getFupno());
      /*dto.setCode(
      codeService.getCodeName(
          flup.getChdrcoy(), CodeType.FOLLOWUP_CODE, flup.getFupcode()));*/
      //TODO：应该批量取出。
      dto.setCode(
          this.getCodeName(descpfs, flup.getChdrcoy(), CodeType.FOLLOWUP_CODE, flup.getFupcode()));

      /*dto.setType(
      codeService.getCodeName(flup.getChdrcoy(), CodeType.FOLLOWUP_TYPE, flup.getFuptype()));*/
      dto.setType(
          this.getCodeName(descpfs, flup.getChdrcoy(), CodeType.FOLLOWUP_TYPE, flup.getFuptype()));

      dto.setFollowupDate(flup.getFupremdtDate());
      dto.setRemark(StringUtils.trimWhitespace(flup.getFupremk()));
      /*dto.setStatus(
      codeService.getCodeName(flup.getChdrcoy(), CodeType.FOLLOWUP_STATUS, flup.getFupstat()));*/
      dto.setStatus(
          this.getCodeName(
              descpfs, flup.getChdrcoy(), CodeType.FOLLOWUP_STATUS, flup.getFupstat()));

      dto.setInsuredNo(flup.getLife());
      dto.setJointNo(StringUtils.trimWhitespace(flup.getJlife()));
      dto.setTransactionNo(flup.getTranno());
      dto.setTransactionDateTime(flup.getTransactionDateTime());
      dto.setClaimId(StringUtils.trimWhitespace(flup.getClamnum()));
      dto.setAutoFollow("Y".equals(flup.getZautoind()));
      if (hxclpfList != null && hxclpfList.size() > 0) {
        dto.setSpecialClause(mergeHxclpfNote(hxclpfList, flup.getFupno()));
        /*dto.setLetterType(
        codeService.getCodeName(
            flup.getChdrcoy(),
            CodeType.FOLLOWUP_LETTYP,
            hxclpfList.get(hxclpfList.size() - 1).getHxcllettyp()));*/
        dto.setLetterType(
            this.getCodeName(
                descpfs,
                flup.getChdrcoy(),
                CodeType.FOLLOWUP_LETTYP,
                hxclpfList.get(hxclpfList.size() - 1).getHxcllettyp()));
      } else {
        List<Zhclpf> zhclpfList =
            zhclpfRepository.findByChdrcoyAndChdrnumOrderByHxclseqno(branchId, policyId);
        dto.setSpecialClause(mergeZhclpfNote(zhclpfList, flup.getTranno()));
        /*dto.setLetterType(
        codeService.getCodeName(
            flup.getChdrcoy(),
            CodeType.FOLLOWUP_LETTYP,
            zhclpfList.get(zhclpfList.size() - 1).getHxcllettyp()));*/
        dto.setLetterType(
            this.getCodeName(
                descpfs,
                flup.getChdrcoy(),
                CodeType.FOLLOWUP_LETTYP,
                zhclpfList.get(zhclpfList.size() - 1).getHxcllettyp()));
      }
      dto.setUserCode(StringUtils.trimWhitespace(flup.getUserProfile()));
      result.add(dto);
    }
    return result;
  }

  private String mergeHxclpfNote(List<Hxclpf> hxclpfList, long fupno) {
    return hxclpfList
        .stream()
        .filter(h -> fupno == h.getFupno())
        .map(h -> h.getHxclseqno() + ", " + h.getHxclnote())
        .reduce("", (s1, s2) -> s1 + s2);
  }

  private String mergeZhclpfNote(List<Zhclpf> zhclpfList, long tranno) {
    return zhclpfList
        .stream()
        .filter(z -> tranno == z.getTranno())
        .map(z -> z.getHxclseqno() + ", " + z.getHxclnote())
        .reduce("", (s1, s2) -> s1 + s2);
  }

  private CodeName getCodeName(
      List<Descpf> descpfs, String branchId, CodeType codeType, String item) {
    CodeName result = new CodeName();
    result.setCode(item);

    result.setName(
        StringUtils.trimWhitespace(
            descpfs
                .stream()
                .filter(
                    p ->
                        p != null
                            && branchId.equals(p.getDesccoy())
                            && codeType.getTable().equals(p.getDesctabl())
                            && item.equals(p.getDescitem()))
                .map(p -> p.getLongdesc())
                .collect(
                    Collectors
                        .joining()) //TODO:descpfs做了distinct处理，这里使用Collectors.joining()没有问题。但是只取某一个属性作为String返回，应该有其他写法
            ));
    return result;
  }
}
