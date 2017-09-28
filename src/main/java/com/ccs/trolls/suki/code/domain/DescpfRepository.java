package com.ccs.trolls.suki.code.domain;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface DescpfRepository extends PagingAndSortingRepository<Descpf, DescpfPk> {
  List<Descpf> findByDescpfxAndLanguageAndDesccoyAndDesctablIn(
      String descpfx, String language, String branchId, String[] codeTypes);
}
