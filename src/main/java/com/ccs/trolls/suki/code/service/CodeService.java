package com.ccs.trolls.suki.code.service;

import java.util.ArrayList;
import java.util.List;

import com.ccs.trolls.suki.code.CodeType;
import com.ccs.trolls.suki.code.domain.Descpf;
import com.ccs.trolls.suki.code.domain.DescpfPk;
import com.ccs.trolls.suki.code.domain.DescpfRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ccs.trolls.suki.api.model.CodeName; //TODO: should not depend on suki.

//TODO: move to another service or redis?
@Service
public class CodeService {
  private static final Logger logger = LoggerFactory.getLogger(CodeService.class);

  @Autowired private DescpfRepository descpfRepository;

  @HystrixCommand
  public CodeName getCodeName(String branchId, CodeType codeType, String item) {
    CodeName result = new CodeName();
    item =
        StringUtils.trimWhitespace(
            item); //DB2在获取数据时未自动trim字符串空格，而H2会。这样造成了开发数据库和实际数据库的行为不一致。但是DB2无对应内存数据库，除非用容器。TODO
    if (StringUtils.isEmpty(item)) return null;
    result.setCode(item);
    String table = codeType.getTable();
    Descpf descpf = descpfRepository.findOne(new DescpfPk(branchId, table, item));
    if (descpf == null) return result;
    result.setName(StringUtils.trimWhitespace(descpf.getLongdesc()));
    return result;
  }

  @HystrixCommand
  public List<Descpf> getCodeNames(List<DescpfPk> descpfPks) {
    //TODO:这种写法虽然只调用一次CodeService服务，但是依然会多次查询数据库
    //查询源码发现，源码中是将ids逐条进行查询后，组装成List进行返回：
    //public List<T> findAll(Iterable<ID> ids){
    //	....
    //	List<T> results = new ArrayList<T>();
    //
    //	for (ID id : ids) {
    //		results.add(findOne(id));
    //	}
    //  return results;
    //}

    return (List<Descpf>) descpfRepository.findAll(descpfPks);
  }

  public List<Descpf> getCodeNames(String branchId, String[] codeTypes) {
    return descpfRepository.findByDescpfxAndLanguageAndDesccoyAndDesctablIn(
        "IT", "S", branchId, codeTypes);
  }
}
